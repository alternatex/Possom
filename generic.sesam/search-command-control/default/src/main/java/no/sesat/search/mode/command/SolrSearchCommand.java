/*
 * Copyright (2008-2009) Schibsted Søk AS
 * This file is part of SESAT.
 *
 *   SESAT is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   SESAT is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with SESAT.  If not, see <http://www.gnu.org/licenses/>.
 */
package no.sesat.search.mode.command;

import java.lang.ref.Reference;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import no.sesat.commons.ref.ReferenceMap;
import no.sesat.search.datamodel.generic.StringDataObject;
import no.sesat.search.datamodel.request.ParametersDataObject;
import no.sesat.search.mode.config.FacetedCommandConfig;
import no.sesat.search.mode.config.SolrCommandConfig;
import no.sesat.search.result.BasicResultItem;
import no.sesat.search.result.BasicResultList;
import no.sesat.search.result.Navigator;
import no.sesat.search.result.ResultItem;
import no.sesat.search.result.ResultList;
import no.sesat.search.site.config.SiteConfiguration;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

/** Searching against a Solr index using the Solrj client.
 * see http://wiki.apache.org/solr/Solrj
 *
 * The query syntax could be improved
 *  see http://lucene.apache.org/java/docs/queryparsersyntax.html
 *
 * @version $Id$
 */
public class SolrSearchCommand extends AbstractSearchCommand{

    // Constants -----------------------------------------------------

    private static final Logger LOG = Logger.getLogger(SolrSearchCommand.class);

    // Attributes ----------------------------------------------------

    private SolrServer server;
    private final FacetToolkit facetToolkit;

    // Static --------------------------------------------------------

    private static final ReferenceMap<String,SolrServer> SERVERS = new ReferenceMap<String,SolrServer>(
            ReferenceMap.Type.SOFT,
            new ConcurrentHashMap<String, Reference<SolrServer>>());

    // Constructors --------------------------------------------------

    public SolrSearchCommand(final Context cxt) {

        super(cxt);
        try {

            final String serverUrlKey = ((SolrCommandConfig)cxt.getSearchConfiguration()).getServerUrl();
            final SiteConfiguration siteConf = cxt.getDataModel().getSite().getSiteConfiguration();
            final String serverUrl = siteConf.getProperty(serverUrlKey);

            server = SERVERS.get(serverUrl);

            if(null == server){
                server = new CommonsHttpSolrServer(serverUrl);
                SERVERS.put(serverUrl, server);
            }

        } catch (MalformedURLException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        facetToolkit = new SimpleFacetToolkitImpl();
    }

    // Public --------------------------------------------------------

    @Override
    public ResultList<ResultItem> execute() {

        final ResultList<ResultItem> searchResult = new BasicResultList<ResultItem>();

        try {
            // set up query
            final SolrQuery query = new SolrQuery()
                    .setQuery(getTransformedQuery())
                    .setFilterQueries(getSearchConfiguration().getFilteringQuery())
                    .setStart(getOffset())
                    .setRows(getSearchConfiguration().getResultsToReturn())
                    .setFields(getSearchConfiguration().getResultFieldMap().keySet().toArray(new String[]{}));

            createFacets(query);

            // when the root logger is set to DEBUG do not limit connection times
            if(Logger.getRootLogger().getLevel().isGreaterOrEqual(Level.INFO)){
                query.setTimeAllowed(getSearchConfiguration().getTimeout());
            }

            final Map<String,String> sortMap = getSearchConfiguration().getSortMap();
            for(Map.Entry<String,String> entry : sortMap.entrySet()){
                final SolrQuery.ORDER order = SolrQuery.ORDER.valueOf(entry.getValue());
                query.addSortField(entry.getKey(), order);
            }

            DUMP.info(query.toString());

            // query
            final QueryResponse response = server.query(query);
            final SolrDocumentList docs = response.getResults();

            searchResult.setHitCount((int)docs.getNumFound());

            // iterate through docs
            for(SolrDocument doc : docs){

                searchResult.addResult(createItem(doc));
            }

        } catch (SolrServerException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return searchResult;
    }

    @Override
    public SolrCommandConfig getSearchConfiguration() {
        return (SolrCommandConfig)super.getSearchConfiguration();
    }

    // Package protected ---------------------------------------------

    // Protected -----------------------------------------------------

    protected FacetToolkit createFacetToolkit(){

        FacetToolkit toolkit = null;
        final String toolkitName = getSearchConfiguration().getFacetToolkit();
        if(null != toolkit){
            try{
                final Class<FacetToolkit> cls = (Class<FacetToolkit>) Class.forName(toolkitName);
                toolkit = cls.newInstance();
            }catch(ClassNotFoundException cnfe){
                LOG.error(cnfe.getMessage());
            }catch(InstantiationException ie){
                LOG.error(ie.getMessage());
            }catch(IllegalAccessException iae){
                LOG.error(iae.getMessage());
            }
        }
        return toolkit;
    }

    protected final void createFacets(final SolrQuery query){
        if(null != facetToolkit){
            facetToolkit.createFacets(context, query);
        }
    }

    protected BasicResultItem createItem(final SolrDocument doc) {

        BasicResultItem item = new BasicResultItem();

        for (final Map.Entry<String,String> entry : getSearchConfiguration().getResultFieldMap().entrySet()){

            item = item.addField(entry.getValue(), (String)doc.getFieldValue(entry.getKey()));

        }

        return item;
    }

    // Private -------------------------------------------------------

    // Inner classes -------------------------------------------------

    /**
     * Provider to add facets from request to SolrQuery.
     */
    public interface FacetToolkit{
        void createFacets(SearchCommand.Context context, SolrQuery query);
    }

    /**
     * Solr's Simple Faceting toolkit.
     *
     * {@link http://wiki.apache.org/solr/SolrFacetingOverview}
     * {@link http://wiki.apache.org/solr/SimpleFacetParameters}
     */
    public static class SimpleFacetToolkitImpl implements FacetToolkit{

        public void createFacets(final SearchCommand.Context context, final SolrQuery query) {

            final Map<String,Navigator> facets = getSearchConfiguration(context).getFacets();

            query.setFacet(0 < facets.size());

            // facet counters
            for(Navigator facet : facets.values()){
                query.addFacetField(facet.getField());
            }

            // facet selection
            for (final Navigator facet : facets.values()) {

                final StringDataObject facetValue = context.getDataModel().getParameters().getValue(facet.getId());

                if (null != facetValue) {

                    // splitting here allows for multiple navigation selections within the one navigation level.
                    for(String navSingleValue : facetValue.getString().split(",")){

                        final String value =  facet.isBoundaryMatch()
                                ? "^\"" + navSingleValue + "\"$"
                                : "\"" + navSingleValue + "\"";

                        query.addFacetQuery(facet.getField() + ':' + value);
                    }
                }
            }
        }

        private FacetedCommandConfig getSearchConfiguration(final SearchCommand.Context context){
            return (FacetedCommandConfig) context.getSearchConfiguration();
        }
    }
}

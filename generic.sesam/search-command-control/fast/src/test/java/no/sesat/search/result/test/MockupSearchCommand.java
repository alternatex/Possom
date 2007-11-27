/* Copyright (2006-2007) Schibsted Søk AS
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
package no.sesat.search.result.test;

import javax.xml.parsers.DocumentBuilder;
import no.schibstedsok.commons.ioc.ContextWrapper;
import no.sesat.search.mode.command.SearchCommand;
import no.sesat.search.mode.config.SearchConfiguration;
import no.sesat.search.site.SiteKeyedFactoryInstantiationException;
import no.sesat.search.run.RunningQuery;
import no.sesat.search.run.RunningQueryImpl;
import java.util.Properties;
import no.sesat.search.datamodel.DataModel;
import no.sesat.search.datamodel.DataModelTestCase;
import no.sesat.search.mode.SearchMode;
import no.sesat.search.result.ResultItem;
import no.sesat.search.result.ResultList;
import no.sesat.search.site.config.*;
import no.sesat.search.site.SiteContext;
import no.sesat.search.view.config.SearchTab;
import no.sesat.search.view.SearchTabFactory;

/** Create a Mockup SearchCommand.
 *
 * @author <a href="mailto:magnus.eklund@schibsted.no">Magnus Eklund</a>
 * @version <tt>$Id$</tt>
 */
public class MockupSearchCommand extends DataModelTestCase implements SearchCommand {

    private final RunningQuery.Context rqCxt = new RunningQuery.Context() {

        private final SearchMode mode = new SearchMode();
        private final DataModel datamodel = getDataModel();

        public SearchMode getSearchMode() {
            return mode;
        }
        public SearchTab getSearchTab(){
            return SearchTabFactory.valueOf(
                ContextWrapper.wrap(SearchTabFactory.Context.class, this))
                .getTabByKey("d");
        }
        public PropertiesLoader newPropertiesLoader(
                final SiteContext siteCxt,
                final String resource,
                final Properties properties) {

            return FileResourceLoader.newPropertiesLoader(siteCxt, resource, properties);
        }
        public DocumentLoader newDocumentLoader(
                final SiteContext siteCxt,
                final String resource,
                final DocumentBuilder builder) {

            return FileResourceLoader.newDocumentLoader(siteCxt, resource, builder);
        }
        public BytecodeLoader newBytecodeLoader(SiteContext context, String className, String jar) {
            return UrlResourceLoader.newBytecodeLoader(context, className, jar);
        }

        public DataModel getDataModel(){
                return datamodel;
            }
    };

    private RunningQuery query;

    public MockupSearchCommand() throws SiteKeyedFactoryInstantiationException {
        query = new RunningQueryImpl(rqCxt, "");
    }

    public MockupSearchCommand(final String queryString) throws SiteKeyedFactoryInstantiationException {
        query = new RunningQueryImpl(rqCxt, queryString);
    }

    public SearchConfiguration getSearchConfiguration() {
        return null;
    }

    public RunningQuery getRunningQuery() {
        return query;
    }

    public ResultList<ResultItem> execute() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ResultList<ResultItem> call() throws Exception {
        return null;
    }

    public boolean handleCancellation() {
        return false;
    }
    
    public boolean isCancelled(){
        return false;
    }
}

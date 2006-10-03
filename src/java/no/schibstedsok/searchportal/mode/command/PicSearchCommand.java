// Copyright (2006) Schibsted Søk AS
package no.schibstedsok.searchportal.mode.command;


import no.schibstedsok.common.ioc.ContextWrapper;
import no.schibstedsok.searchportal.mode.config.SiteConfiguration;
import no.schibstedsok.searchportal.http.HTTPClient;
import no.schibstedsok.searchportal.result.BasicSearchResult;
import no.schibstedsok.searchportal.result.BasicSearchResultItem;
import no.schibstedsok.searchportal.result.SearchResult;
import no.schibstedsok.searchportal.mode.config.PicSearchConfiguration;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.net.URLEncoder;

/**
 * @author <a href="mailto:magnus.eklund@schibsted.no">Magnus Eklund</a>
 * @version <tt>$Revision$</tt>
 */
public final class PicSearchCommand extends AbstractSearchCommand {

    private static final Logger LOG = Logger.getLogger(PicSearchCommand.class);
    private final HTTPClient client;

    /**
     * Creates a new command in given context.
     *
     * @param cxt Context to run in.
     * @param parameters Command parameters.
     */
    public PicSearchCommand(final Context cxt, final Map parameters) {
        super(cxt, parameters);
        final SiteConfiguration siteConfig
                = SiteConfiguration.valueOf(ContextWrapper.wrap(SiteConfiguration.Context.class, cxt));
        client = HTTPClient.instance(
                "picture_search",
                siteConfig.getProperty("picsearch.host"),
                Integer.valueOf(siteConfig.getProperty("picsearch.port")));
    }

    /** {@inheritDoc} */
    public SearchResult execute() {

        String query = getTransformedQuery().replace(' ', '+');
        final PicSearchConfiguration psConfig = (PicSearchConfiguration) context.getSearchConfiguration();

        try {
            query = URLEncoder.encode(query, "utf-8");
        } catch (UnsupportedEncodingException e) {
            LOG.error(e);
        }

        final String url = "/query?ie=UTF-8&tldb=" + psConfig.getPicsearchCountry()
                + "&filter=medium&custid=558735&version=2.6&thumbs=" + psConfig.getResultsToReturn()
                + "&q=" + query
                + "&start=" + getCurrentOffset(1);

        LOG.info("Using " + url);

        final Document doc = doSearch(url);

        final BasicSearchResult searchResult = new BasicSearchResult(this);

        if (doc != null) {

            final Element resultElement = doc.getDocumentElement();

            searchResult.setHitCount(Integer.parseInt(resultElement.getAttribute("hits")));

            final NodeList list = resultElement.getElementsByTagName("image");
            for (int i = 0; i < list.getLength(); i++) {

                final Element picture = (Element) list.item(i);

                final BasicSearchResultItem item = new BasicSearchResultItem();

                for (final Map.Entry<String,String> entry : psConfig.getResultFields().entrySet()) {

                    item.addField(entry.getValue(), picture.getAttribute(entry.getKey()));
                }
                searchResult.addResult(item);

            }
        }
        return searchResult;
    }

    private Document doSearch(final String url) {
        try {
            return client.getXmlDocument("picture_search", url);
        } catch (IOException e) {
            LOG.error("Problems with connection to " + url, e);
        } catch (SAXException e) {
            LOG.error("XML Parse error " + url , e);
        }
        return null;
    }
}

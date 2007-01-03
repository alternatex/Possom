// Copyright (2006) Schibsted Søk AS
package no.schibstedsok.searchportal.mode.command;

import no.schibstedsok.searchportal.mode.config.OverturePPCSearchConfiguration;
import no.schibstedsok.searchportal.query.token.TokenPredicate;
import no.schibstedsok.searchportal.result.BasicSearchResultItem;
import no.schibstedsok.searchportal.result.OvertureSearchResult;
import no.schibstedsok.searchportal.result.SearchResult;
import no.schibstedsok.searchportal.InfrastructureException;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 *
 * This command gets the overture ads to display. It also does some analysis of
 * the query to decide if it is a query that yields a high click frequency for
 * the ads. This is done by evaluating the predicate "exact_ppctoplist".
 */
public final class OverturePPCSearchCommand extends AbstractYahooSearchCommand {

    private static final String OVERTURE_PPC_ELEMENT = "Listing";

    private static final Logger LOG = Logger.getLogger(OverturePPCSearchCommand.class);

    private boolean top = false;

    /**
     * Create new overture command.
     *
     * @param query
     * @param configuration
     * @param parameters
     */
    public OverturePPCSearchCommand(
                             final Context cxt,
                             final Map parameters) {
        
        super(cxt, parameters);

    }
    /**
     * Execute the command.
     *
     * @return
     */
    public SearchResult execute() {
        
        // Need to rerun the token evaluation stuff on the transformed query
        // The transformed query does not contain site: and nyhetskilde: which
        // could have prevented exact matching in the previous evaluation.
        final ReconstructedQuery rq = createQuery(getTransformedQuery());
        
        top = rq.getEngine().evaluateQuery(TokenPredicate.EXACT_PPCTOPLIST, rq.getQuery());
        
        try {
            final Document doc = getXmlResult();
            final OvertureSearchResult searchResult = new OvertureSearchResult(this, top);

            if (doc != null) {
                final Element elem = doc.getDocumentElement();
                final NodeList list = elem.getElementsByTagName(OVERTURE_PPC_ELEMENT);

                for (int i = 0; i < list.getLength(); ++i) {
                    final Element listing = (Element) list.item(i);
                    final BasicSearchResultItem item = createItem(listing);
                    searchResult.addResult(item);
                }
                final NodeList resultSetList = elem.getElementsByTagName("ResultSet");
                if(resultSetList.getLength()>0){
                    searchResult.setHitCount(Integer.parseInt(
                            ((Element)resultSetList.item(0)).getAttribute("numResults")));
                }
            }

            return searchResult;
        } catch (IOException e) {
            throw new InfrastructureException(e);
        } catch (SAXException e) {
            throw new InfrastructureException(e);
        }
    }

    /** TODO comment me. **/
    protected String createRequestURL() {

        final OverturePPCSearchConfiguration ppcConfig
                = (OverturePPCSearchConfiguration) context.getSearchConfiguration();

        final StringBuilder url = new StringBuilder(ppcConfig.getUrl());

        try {
            url.append("&Partner=" + getPartnerId());
            if( null != ppcConfig.getType() && ppcConfig.getType().length() > 0){
                url.append("&type=" + ppcConfig.getType());
            }
            url.append("&Keywords=");
            url.append(URLEncoder.encode(getTransformedQuery().replace(' ', '+'), ppcConfig.getEncoding()));
            url.append("&maxCount=");
            url.append(getResultsToReturn());
            
        }  catch (UnsupportedEncodingException e) {
            throw new InfrastructureException(e);
        }
        return url.toString();
    }


    /** TODO comment me. **/
    protected int getResultsToReturn(){

        final int resultsToShow = context.getRunningQuery().getSearchTab().getAdLimit();
        final int resultsOnTop = context.getRunningQuery().getSearchTab().getAdOnTop();

        if (top && (!getParameters().containsKey("ss") && !isVgSiteSearch())) {
            return resultsToShow + resultsOnTop;
        } else {
            return resultsToShow;
        }
    }

    /**
     **/
    protected BasicSearchResultItem createItem(final Element ppcListing) {

        final BasicSearchResultItem item = new BasicSearchResultItem();
        final NodeList click = ppcListing.getElementsByTagName("ClickUrl");

        item.addField("title", ppcListing.getAttribute("title"));
        item.addField("description", ppcListing.getAttribute("description"));
        item.addField("siteHost", ppcListing.getAttribute("siteHost"));

        if (click.getLength() > 0) {
            item.addField("clickURL", click.item(0).getFirstChild().getNodeValue());
        }

        return item;
    }


}



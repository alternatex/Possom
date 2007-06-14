/* Copyright (2005-2007) Schibsted Søk AS
 * RunningQuery.java
 *
 * Created on 16 February 2006, 19:52
 *
 */

package no.schibstedsok.searchportal.run;

import java.util.List;
import java.util.Locale;
import no.schibstedsok.commons.ioc.BaseContext;
import no.schibstedsok.searchportal.datamodel.DataModelContext;
import no.schibstedsok.searchportal.mode.SearchMode;
import no.schibstedsok.searchportal.site.config.ResourceContext;
import no.schibstedsok.searchportal.query.Query;
import no.schibstedsok.searchportal.result.Modifier;
import no.schibstedsok.searchportal.view.config.SearchTab;

/** A RunningQuery is the central controller for a user's submitted search.
 * It has a one-to-one mapping to a search mode (see tabs.xml).
 *
 * @version $Id$
 * @author <a href="mailto:mick@wever.org">Michael Semb Wever</a>
 */
public interface RunningQuery {

    public interface Context extends BaseContext, ResourceContext, DataModelContext {
        /** TODO comment me. **/
        SearchMode getSearchMode();
        /** TODO comment me. **/
        SearchTab getSearchTab();
    }

    /** TODO comment me. **/
    void addSource(final Modifier modifier);

    /** TODO comment me. **/
    List getEnrichments();

    /** TODO comment me. **/
    List getGeographicMatches();

    /**
     * First find out if the user types in an advanced search etc by analyzing the queryStr.
     * Then lookup correct tip using messageresources.
     *
     * @return user tip
     */
    String getGlobalSearchTips();

    /** TODO comment me. **/
    Locale getLocale();

    /** TODO comment me. **/
    Integer getNumberOfHits(final String configName);

    /** TODO comment me. **/
    Query getQuery();

    /** TODO comment me. **/
    SearchMode getSearchMode();

    /** TODO comment me. **/
    SearchTab getSearchTab();

    /** TODO comment me. **/
    List<Modifier> getSources();

    /**
     * Thread run
     *
     * @throws InterruptedException
     */
    void run() throws InterruptedException;

}

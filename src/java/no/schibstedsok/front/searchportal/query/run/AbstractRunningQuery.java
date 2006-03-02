/* Copyright (2005-2006) Schibsted Søk AS
 *
 * AbstractRunningQuery.java
 *
 * Created on 16 February 2006, 19:49
 *
 */

package no.schibstedsok.front.searchportal.query.run;

import no.schibstedsok.front.searchportal.configuration.SearchMode;
import no.schibstedsok.front.searchportal.configuration.SearchTabsCreator;
import org.apache.log4j.Logger;

/**
 * @version $Id$
 * @author <a href="mailto:mick@wever.org">Michael Semb Wever</a>
 */
public abstract class AbstractRunningQuery implements RunningQuery {

    private static final Logger LOG = Logger.getLogger(AbstractRunningQuery.class);

    protected final Context context;

    /** Creates a new instance of AbstractRunningQuery */
    protected AbstractRunningQuery(final Context cxt) {
        context = cxt;
    }


    /**
     * Remote duplicate spaces. Leading and trailing spaces will
     * be preserved
     * @param query that may conaint duplicate spaces
     * @return string with duplicate spaces removed
     */
    protected static String trimDuplicateSpaces(String query){

        LOG.trace("trimDuplicateSpaces(" + query + ")");

        if(query == null){ return null; }
        
        if("".equals(query)) { return ""; }

        //query = query.trim();
        query = query.replaceAll("\\s+", " ");

        return query;
    }

}

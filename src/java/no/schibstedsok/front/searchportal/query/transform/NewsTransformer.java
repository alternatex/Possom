// Copyright (2006) Schibsted Søk AS
package no.schibstedsok.front.searchportal.query.transform;


/**
 * NewsTransformer is part of no.schibstedsok.front.searchportal.query
 *
 * @author Ola Marius Sagli <a href="ola@schibstedsok.no">ola at schibstedsok</a>
 * @version 0.1
 * @vesrion $Revision$, $Author$, $Date$
 */
public class NewsTransformer extends AbstractQueryTransformer implements QueryTransformer {



    /**
     * Add keywords to query to get better searchresults
     *
     * @param originalQuery
     * @return
     */
    public String getTransformedQuery(final Context cxt) {

        final String originalQuery = cxt.getTransformedQuery();

       return originalQuery;
    }

    /**
     * Set filter for thiw query.
     * Example to add docdatetime argument
     * <p/>
     * +docdatetime:>2005-10-28
     *
     * @return filterstring
     */
    public String getFilter(final Context cxt) {

        final String origQuery = cxt.getTransformedQuery();

        if (origQuery == null) {
            throw new IllegalArgumentException("setQuery not called with minimum empty query");
        }

        if ("".equals(origQuery.trim())) {
            return " +size:>0 ";
        }

        return null;
    }


}

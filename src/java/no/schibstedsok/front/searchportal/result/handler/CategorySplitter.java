// Copyright (2006) Schibsted Søk AS
package no.schibstedsok.front.searchportal.result.handler;

import java.util.Map;
import java.util.Iterator;
import no.schibstedsok.front.searchportal.result.SearchResultItem;


/**
 * Created by IntelliJ IDEA.
 * User: itthkjer
 * Date: 24.okt.2005
 * Time: 09:36:39
 * To change this template use File | Settings | File Templates.
 */

public class CategorySplitter implements ResultHandler {

    public void handleResult(final Context cxt, final Map parameters) {

        for (final Iterator iterator = cxt.getSearchResult().getResults().iterator(); iterator.hasNext();) {
            final SearchResultItem searchResultItem = (SearchResultItem) iterator.next();

            final String ypbransje = searchResultItem.getField("ypbransje");

            if (ypbransje != null) {
                //splits bransje to show categories under YIP page
                final String[]  split = ypbransje.split("FASTpbFAST");
                for (int i = 0; i < split.length; i++)
                    searchResultItem.addToMultivaluedField("manyCategories", split[i].trim());
            }

        }
    }
}

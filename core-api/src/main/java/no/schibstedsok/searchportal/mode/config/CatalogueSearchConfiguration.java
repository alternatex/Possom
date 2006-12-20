/*
 * Copyright (2005-2006) Schibsted Søk AS
 */
package no.schibstedsok.searchportal.mode.config;

/**
 *
 * An implementation of Search Configuration for yellow searches.
 *
 * @author <a href="larsj@conduct.no">Lars Johansson</a>
 * @version $Revision: 1 $
 */
public class CatalogueSearchConfiguration extends FastSearchConfiguration {

    public CatalogueSearchConfiguration(){
        super(null);
    }

    public CatalogueSearchConfiguration(final SearchConfiguration asc){
        super(asc);
    }

}

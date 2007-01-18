/*
 * Copyright (2005-2006) Schibsted Søk AS
 */
package no.schibstedsok.searchportal.mode.command;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import no.fast.ds.search.ISearchParameters;
import no.fast.ds.search.SearchParameter;
import no.schibstedsok.searchportal.query.Query;
import no.schibstedsok.searchportal.result.BasicSearchResultItem;
import no.schibstedsok.searchportal.result.CatalogueSearchResultItem;
import no.schibstedsok.searchportal.result.FastSearchResult;
import no.schibstedsok.searchportal.result.SearchResult;

import org.apache.log4j.Logger;


/**
 * 
 */
public class CatalogueSearchCommand extends AbstractSimpleFastSearchCommand {

    private static final Logger LOG = Logger.getLogger(CatalogueSearchCommand.class);
    private String queryTwo=null;
    

    /** Creates a new catalogue search command.
     * TODO. Rewrite from scratch. This is insane.
     **/
    public CatalogueSearchCommand(final Context cxt, final Map parameters) {
    	super(cxt, parameters);
    	LOG.info("CatalogueSearchCommand constructor.");
    	LOG.info("Where:"+getSingleParameter("where"));

    	// hvis "where" parametern er sendt inn, så tar vi og leser inn query fra
    	// den.
    	if(getSingleParameter("where") != null){
	        final ReconstructedQuery rq = createQuery(getSingleParameter("where"));
	        final Query query = rq.getQuery();
	
	    	queryTwo = query.getQueryString();

    	}
   
    }

    /** TODO comment me. **/
    public SearchResult execute() {
        LOG.info("execute()");
    	LOG.info("Catalogue query is :" + getTransformedQuery());     
    	

    	
        // kør vanligt søk.
        SearchResult result = super.execute();
		
        // konverter til denne.
        List<CatalogueSearchResultItem> nyResultListe = new ArrayList<CatalogueSearchResultItem>();
		
    	//TODO: get all keys to lookup and execute one call instead of iterating like this...
    	Iterator iter = result.getResults().listIterator();
    	
    	
    	while (iter.hasNext()) {
    		BasicSearchResultItem basicResultItem = (BasicSearchResultItem) iter.next();

    		CatalogueSearchResultItem resultItem = new CatalogueSearchResultItem();
    		for(Object o : basicResultItem.getFieldNames()){
    			String s = (String) o;
    			String v = basicResultItem.getField(s);
    			resultItem.addField(s,v);
    		}
    		
    		nyResultListe.add(resultItem);
    	}
    	
    	// fjern de gamle BasicResultItems, og erstatt dem med nye CatalogueResultItems. 
    	result.getResults().clear();
    	result.getResults().addAll(nyResultListe);
    	
    	return result;
    }

    /** TODO comment me. **/
    public String getTransformedQuery() {
    	LOG.info("Catalogue Transformed Query");
    	
    	// hvis det finnes en ekstra query, legg til denne i søket.    	
        return queryTwo!=null ? super.getTransformedQuery()+" iypcfgeo:\""+queryTwo+"\"" : super.getTransformedQuery();
    }
    
    

}

// Copyright (2006) Schibsted Søk AS
package no.schibstedsok.front.searchportal.query.transform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



/**
 * Defines a default weather search pattern.
 * 
 * @author <a href="mailto:larsj@conduct.no">Lars Johansson</a>
 * @version <tt>$Revision: 1 $</tt>
 */
public final class WeatherQueryTransformer extends AbstractQueryTransformer {
    
	private List<String> defaultLocations = new ArrayList<String>();
    
    /**
     * creates a default location filter with major cities as defined in modes.xml
     */
    public String getFilter() {
        
		StringBuilder defaultLocationsFilter = new StringBuilder();
    	final boolean blankQuery = getContext().getQuery().isBlank();

    	if(blankQuery){

        	defaultLocationsFilter.append("+(sgeneric4:By) +(title:");
            for (String location : defaultLocations ) {
                defaultLocationsFilter.append(" ");
                defaultLocationsFilter.append(location);
            }
            defaultLocationsFilter.append(") ");
            
    	}
    	
    	return defaultLocationsFilter.toString();
    }

	public void setDefaultLocations(String[] strings) {
		if(strings.length > 0 && strings[0].trim().length() >0){
			defaultLocations.addAll(Arrays.asList(strings));
        }	
	}

	public List<String> getDefaultLocations() {
		return defaultLocations;
	}

}

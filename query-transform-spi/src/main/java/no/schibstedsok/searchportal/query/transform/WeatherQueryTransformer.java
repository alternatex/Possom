// Copyright (2006) Schibsted Søk AS
package no.schibstedsok.searchportal.query.transform;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Element;



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
    @Override
    public String getFilter() {
        
		StringBuilder defaultLocationsFilter = new StringBuilder();
    	final boolean blankQuery = getContext().getQuery().isBlank();

    	if(blankQuery){

        //    defaultLocationsFilter.append("+(sgeneric4:By) +(title:");
            defaultLocationsFilter.append("+(");
            for (String location : defaultLocations ) {
                defaultLocationsFilter.append(" igeneric1:");
                defaultLocationsFilter.append(location);
            }
            defaultLocationsFilter.append(") ");
            
    	}
    	
    	return defaultLocationsFilter.toString();
    }

    public void setDefaultLocations(String[] strings) {
        if(strings.length > 0 && strings[0].trim().length() >0){
            for (String location : strings) {
                defaultLocations.add(location.trim());
            }
        }	
    }

    public List<String> getDefaultLocations() {
		return defaultLocations;
	}

    @Override
    public Object clone() throws CloneNotSupportedException {

        final WeatherQueryTransformer retValue = (WeatherQueryTransformer)super.clone();
        retValue.defaultLocations = defaultLocations;

        return retValue;
    }
    
    @Override
    public QueryTransformer readQueryTransformer(final Element qt){
        
        super.readQueryTransformer(qt);
        setDefaultLocations(qt.getAttribute("default-locations").split(","));
        return this;
    }
}

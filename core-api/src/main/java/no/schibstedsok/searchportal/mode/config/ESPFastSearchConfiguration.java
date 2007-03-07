// Copyright (2007) Schibsted Søk AS
/*
 * AdvancedFastConfiguration.java
 *
 * Created on May 30, 2006, 4:16 PM
 *
 */

package no.schibstedsok.searchportal.mode.config;

import no.schibstedsok.searchportal.result.Navigator;
import no.schibstedsok.searchportal.InfrastructureException;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.fastsearch.esp.search.view.ISearchView;
import com.fastsearch.esp.search.SearchFactory;
import com.fastsearch.esp.search.ConfigurationException;
import com.fastsearch.esp.search.SearchEngineException;

/**
 *
 * @author maek
 */
public class ESPFastSearchConfiguration extends AbstractSearchConfiguration {

    // Constants -----------------------------------------------------
    
    private String view;
    private String queryServer;
    private String sortBy;
    private boolean collapsingEnabled;
    private boolean expansionEnabled;
    private boolean collapsingRemoves;
    private String qtPipeline;

    public void setCollapsingEnabled(final boolean collapsingEnabled) {
        this.collapsingEnabled = collapsingEnabled;
    }

    public boolean isCollapsingEnabled() {
        return collapsingEnabled;
    }

    /**
     * Returns true if expansion is enabled. Expansion means the possibility 
     * to retrieve all of the documents that has been collapsed for a domain. If
     * this is set to false the templates won't get the information that there
     * are collapsed documents.
     *
     * @return true if expansion is enabled.
     */
    public boolean isExpansionEnabled() {
        return expansionEnabled;
    }

    /**
     * Setter for the expansionEnabled property.
     *
     * @param expansionEnabled 
     */
    public void setExpansionEnabled(final boolean expansionEnabled) {
        this.expansionEnabled = expansionEnabled;
    }

    public boolean isCollapsingRemoves() {
        return collapsingRemoves;
    }

    public void setCollapsingRemoves(final boolean collapsingRemoves) {
        this.collapsingRemoves = collapsingRemoves;
    }

    private final Map<String, Navigator> navigators = new HashMap<String,Navigator>();

    public ESPFastSearchConfiguration(final SearchConfiguration asc){
        super(asc);
    }

    public ESPFastSearchConfiguration() {
        super(null);
    }
    
    public String getView() {
        return view;
    }

    public void setView(final String view) {
        this.view = view;
    }

    public String getQueryServer() {
        return queryServer;
    }

    public void setQueryServer(final String queryServer) {
        this.queryServer = queryServer;
    }

    public String getSortBy() {
        return sortBy;
    }
    
    public void setSortBy(final String sortBy) {
        this.sortBy = sortBy;
    }
    
    public Map<String, Navigator> getNavigators() {
        return navigators;
    }
    public void addNavigator(final Navigator navigator, final String navKey) {
        navigators.put(navKey, navigator);
    }

    public Navigator getNavigator(final String navigatorKey) {
        return navigators.get(navigatorKey);
    }

    public void setQtPipeline(final String qtPipeline) {
        this.qtPipeline = qtPipeline;
    }

    public String getQtPipeline() {
        return qtPipeline;
    }
}
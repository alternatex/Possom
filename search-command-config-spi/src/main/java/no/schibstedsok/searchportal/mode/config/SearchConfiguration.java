/*
 * Copyright (2005-2007) Schibsted Søk AS
 * This file is part of SESAT.
 * You can use, redistribute, and/or modify it, under the terms of the SESAT License.
 * You should have received a copy of the SESAT License along with this program.  
 * If not, see https://dev.schibstedsok.no/confluence/display/SESAT/SESAT+License
 *
 */
package no.schibstedsok.searchportal.mode.config;

import java.io.Serializable;
import no.schibstedsok.searchportal.query.transform.QueryTransformerConfig;
import no.schibstedsok.searchportal.result.handler.ResultHandlerConfig;

import java.util.List;
import java.util.Map;
import org.w3c.dom.Element;

/**
 * @author <a href="mailto:magnus.eklund@schibsted.no">Magnus Eklund</a>
 * @version <tt>$Id$</tt>
 */
public interface SearchConfiguration extends Serializable {
    /**
     * Returns a (defensive copy) list of {@link no.schibstedsok.searchportal.query.transform.QueryTransformer} that should be applied to
     * the query before the query is sent to search indices.
     *
     * @return The list of query.
     */
    List<QueryTransformerConfig> getQueryTransformers();

    /**
     * Adds a {@link no.schibstedsok.searchportal.query.transform.QueryTransformer} to the list of transformeres.
     *
     * @param transformer The query transformer to add.
     */
    void addQueryTransformer(QueryTransformerConfig transformer);

    /**
     * Returns a list of {@link no.schibstedsok.searchportal.result.handler.ResultHandler} that should act on the search
     * result.
     *
     * @return The list of handlers.
     */
    List<ResultHandlerConfig> getResultHandlers();

    /**
     * Adds a {@link no.schibstedsok.searchportal.result.handler.ResultHandler} to the list of handlers.
     *
     * @param handler The handler to add.
     */
    void addResultHandler(ResultHandlerConfig handler);

    /**
     * Returns the name of this configuration.
     *
     * @return the name of the configuration.
     */
    String getName();

    /**
     * Returns the number of results to return.
     *
     * @return
     */
    int getResultsToReturn();

    /**
     * Returns true if paging shoud be enabled to this configuration. This
     * is typically only set to true for one of the configurations in a
     * {@link no.schibstedsok.searchportal.mode.config.SearchMode}
     *
     * @return true if paging is enabled.
     */
    boolean isPaging();

    /**
     * @return
     */

    Map<String,String> getResultFields();

    /**
     * @param resultField
     */
    void addResultField(String... resultField);

    /**
     * Sets the number of results to return. This is typically set to the
     * page size.
     *
     * @param numberOfResults
     */
    void setResultsToReturn(int numberOfResults);

//    /**
//     * @return 
//     */
//    boolean isChild();

    /**
     * @return 
     */
    public String getQueryParameter();

    /**
     * @return 
     */
    boolean isAlwaysRun();
    
    /**
     * @return 
     */
    boolean isRunBlank();
    
    /**
     * @return 
     */
    String getStatisticalName();

    /**
     * Getter for property fieldFilters.
     *
     * @return Value of property fieldFilters.
     */
    Map<String, String> getFieldFilters();

    /***/
    void clearQueryTransformers();

    /***/
    void clearResultHandlers();

    /**
     * Removes all field filters associated with this configuration.
     */
    void clearFieldFilters();
        
    /**
     * 
     * @param element 
     * @param inherit 
     * @return 
     */
    SearchConfiguration readSearchConfiguration(Element element, SearchConfiguration inherit); 
}

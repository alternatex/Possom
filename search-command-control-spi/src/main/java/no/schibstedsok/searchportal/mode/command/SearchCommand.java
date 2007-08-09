/*
* Copyright (2005-2007) Schibsted Søk AS
 * This file is part of SESAT.
 * You can use, redistribute, and/or modify it, under the terms of the SESAT License.
 * You should have received a copy of the SESAT License along with this program.  
 * If not, see https://dev.schibstedsok.no/confluence/display/SESAT/SESAT+License
*
*/
package no.schibstedsok.searchportal.mode.command;

import java.util.concurrent.Callable;
import no.schibstedsok.commons.ioc.BaseContext;
import no.schibstedsok.searchportal.datamodel.DataModelContext;
import no.schibstedsok.searchportal.mode.config.SearchConfiguration;
import no.schibstedsok.searchportal.mode.config.SearchConfigurationContext;
import no.schibstedsok.searchportal.site.config.ResourceContext;
import no.schibstedsok.searchportal.query.token.TokenEvaluationEngineContext;
import no.schibstedsok.searchportal.result.ResultItem;
import no.schibstedsok.searchportal.result.ResultList;

/** Behavour requirements for any implementation Search Command.
 *
 * @author <a href="mailto:magnus.eklund@schibsted.no">Magnus Eklund</a>
 * @version <tt>$Id$</tt>
 */
public interface SearchCommand extends Callable<ResultList<? extends ResultItem>> {

    /** Being a factory for all the commands - it propagates all the contextual needs of the underlying commands it
     * creates.
     */
    public interface Context extends BaseContext, ResourceContext, DataModelContext,
            SearchConfigurationContext, TokenEvaluationEngineContext {
    }

    /**
     * Returns the configuration associated with this search command.
     *
     * @return The search configuration.
     */
    SearchConfiguration getSearchConfiguration();

    /** Allows the SearchCommand to clean itself after a long night out when s/he didn't get home in time.
     * @return if cleaning was actually performed
     **/
    boolean handleCancellation();
}

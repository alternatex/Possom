// Copyright (2006) Schibsted Søk AS
/*
 *
 * Created on March 4, 2006, 2:32 PM
 *
 */

package no.schibstedsok.front.searchportal.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.Callable;
import javax.xml.parsers.DocumentBuilder;
import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import no.schibstedsok.common.ioc.BaseContext;
import no.schibstedsok.common.ioc.ContextWrapper;
import no.schibstedsok.front.searchportal.command.impl.SearchCommandFactory;
import no.schibstedsok.front.searchportal.configuration.FastConfiguration;
import no.schibstedsok.front.searchportal.configuration.SearchConfiguration;
import no.schibstedsok.front.searchportal.configuration.SearchMode;
import no.schibstedsok.front.searchportal.configuration.loader.DocumentLoader;
import no.schibstedsok.front.searchportal.configuration.loader.FileResourceLoader;
import no.schibstedsok.front.searchportal.configuration.loader.PropertiesLoader;
import no.schibstedsok.front.searchportal.query.Query;
import no.schibstedsok.front.searchportal.query.run.RunningQuery;
import no.schibstedsok.front.searchportal.query.run.RunningQueryImpl;
import no.schibstedsok.front.searchportal.result.SearchResult;
import no.schibstedsok.front.searchportal.site.Site;
import no.schibstedsok.front.searchportal.view.config.SearchTab;
import no.schibstedsok.front.searchportal.view.config.SearchTabFactory;
import org.apache.log4j.Logger;


/** Executes all search commands in the given different tabs.
 * Corresponding to that tab's mode.
 *
 * @author <a href="mailto:mick@wever.org">Michael Semb Wever</a>
 * @version $Id$
 */
public final class AllSearchCommandsTest extends AbstractSearchCommandTest {
    
    private static final Logger LOG = Logger.getLogger(AllSearchCommandsTest.class);
    
    private static final String DEBUG_EXECUTE_COMMAND = "Testing command ";

    public AllSearchCommandsTest(final String name) {
        super(name);
    }

    public void testAllNorskNettsøkSearchCommands() {

        executeTestOfQuery("linux", "d");
    }

    public void testAllInternasjonalNettsøkSearchCommands() {

        executeTestOfQuery("linux", "g");
    }
    
    public void testAllWhitepagesSearchCommands() {

        executeTestOfQuery("linux", "w");
    }
    
    public void testAllYellowpagesSearchCommands() {

        executeTestOfQuery("linux", "y");
    }
    
    public void testAllNyheterSearchCommands() {

        executeTestOfQuery("linux", "m");
    }
    
    public void testAllBilderSearchCommands() {

        executeTestOfQuery("linux", "p");
    }
    
    private void executeTestOfQuery(final String query, final String key) {

        // proxy it back to the RunningQuery context.
        final RunningQuery.Context rqCxt = createRunningQueryContext(key);
        
        final Collection<Callable<SearchResult>> commands = new ArrayList<Callable<SearchResult>>();
        
        for( SearchConfiguration conf : rqCxt.getSearchMode().getSearchConfigurations()){
            
            LOG.info(DEBUG_EXECUTE_COMMAND + conf.getName());

            final SearchCommand.Context cxt = createCommandContext(query, rqCxt, conf.getName());
            
            final AbstractSearchCommand cmd 
                    = (AbstractSearchCommand) SearchCommandFactory.createSearchCommand(cxt, Collections.EMPTY_MAP);
            
            commands.add(cmd);
        }
        try {

            rqCxt.getSearchMode().getExecutor().invokeAll(commands, 10000);
        } catch (InterruptedException ex) {
            throw new AssertionError(ex);
        }
    }

}
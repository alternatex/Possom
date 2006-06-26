// Copyright (2006) Schibsted Søk AS
/*
 * SensisSearchCommand.java
 *
 * Created on March 7, 2006, 1:01 PM
 *
 */

package no.schibstedsok.front.searchportal.command;

import java.util.Map;

/**
 *
 * A search command for the web search.
 * @author magnuse
 */
public class SensisSearchCommand extends FastSearchCommand {

    /** Creates a new instance of SensisSearchCommand
     *
     * @param cxt Search command context.
     * @param parameters Search command parameters.
     */
    public SensisSearchCommand(final Context cxt, final Map parameters) {
        super(cxt, parameters);
    }


}

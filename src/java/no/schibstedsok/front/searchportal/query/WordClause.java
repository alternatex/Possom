/* Copyright (2005-2006) Schibsted Søk AS
 * WordClause.java
 *
 * Created on 15 February 2006, 13:55
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package no.schibstedsok.front.searchportal.query;

import no.schibstedsok.front.searchportal.query.parser.*;

/**
 * @version $Id$
 * @author <a href="mailto:mick@wever.org">Michael Semb Wever</a>
 */
public interface WordClause extends LeafClause {
    /**
     * Get the field.
     * 
     * @return the field.
     */
    String getField();
    
}

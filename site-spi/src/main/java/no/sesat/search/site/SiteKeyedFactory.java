/* Copyright (2006-2007) Schibsted Søk AS
 * This file is part of SESAT.
 * You can use, redistribute, and/or modify it, under the terms of the SESAT License.
 * You should have received a copy of the SESAT License along with this program.  
 * If not, see https://dev.sesat.no/confluence/display/SESAT/SESAT+License
 */
/*
 * SiteKeyedFactory.java
 *
 * Created on 5 May 2006, 07:27
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package no.sesat.search.site;

/** Factories that have Site->Factory mappings should implement this interface
 * to ensure general behaviours.
 *
 * @author <a href="mailto:mick@wever.org">Michael Semb Wever</a>
 */
public interface SiteKeyedFactory {
    
    /** Remove the factory the maps to the given site. **/
    boolean remove(Site site);
    
}

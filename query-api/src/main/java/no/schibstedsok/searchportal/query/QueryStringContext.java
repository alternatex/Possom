/* Copyright (2005-2007) Schibsted Søk AS
 * QueryStringContext.java
 *
 * Created on 23 January 2006, 14:02
 *
 */

package no.schibstedsok.searchportal.query;

import no.schibstedsok.commons.ioc.BaseContext;

/** Used when the query string is a requirement of the Context and neither the Query object or datamodel are available.
 * 
 * @version $Id$
 * @author <a href="mailto:mick@wever.org">Michael Semb Wever</a>
 */
public interface QueryStringContext extends BaseContext{
    /** Get the original query string.
     *
     * @return the original query string.
     */
    String getQueryString();
}

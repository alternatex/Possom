/* Copyright (2006-2007) Schibsted Søk AS
 * This file is part of SESAT.
 * You can use, redistribute, and/or modify it, under the terms of the SESAT License.
 * You should have received a copy of the SESAT License along with this program.  
 * If not, see https://dev.sesat.no/confluence/display/SESAT/SESAT+License

 */
package no.sesat.searchportal.query.transform;

import no.sesat.searchportal.query.transform.AbstractQueryTransformerConfig.Controller;

/**
 * Transforms the query into <br/> iypnavnvisningnorm:^"query"$ <br/> Ensures that only an
 * exact match within the titles field is returned.
 *
 * @author <a href="mailto:daniele@conduct.no">Daniel Engfeldt</a>
 * @version <tt>$Revision:$</tt>
 */
@Controller("CatalogueExactTitleMatchQueryTransformer")
public final class CatalogueExactTitleMatchQueryTransformerConfig extends AbstractQueryTransformerConfig {}

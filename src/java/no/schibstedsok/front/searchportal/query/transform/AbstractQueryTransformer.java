// Copyright (2006) Schibsted Søk AS
package no.schibstedsok.front.searchportal.query.transform;


import no.schibstedsok.front.searchportal.query.parser.AbstractReflectionVisitor;
import org.apache.log4j.Logger;

/**
 * AbstractQueryTransformer is part of no.schibstedsok.front.searchportal.query
 *
 * @author Ola Marius Sagli <a href="ola@schibstedsok.no">ola at schibstedsok</a>

 * @vesrion $Revision$, $Author$, $Date$
 */
public abstract class AbstractQueryTransformer extends AbstractReflectionVisitor implements QueryTransformer {
    
    private static final Logger LOG = Logger.getLogger(AbstractQueryTransformer.class);

    private static final String INFO_OLD_IMPLEMENTATION_STILL 
            = "QueryTransformer has not been adapted to Visitor pattern -> ";
    
    private Context context;
    
    public void setContext(final Context cxt){
        context = cxt;
    }
    
    protected Context getContext(){
        return context;
    }

    public String getFilter() {
        return null;
    }

    public String getFilter(final java.util.Map parameters) {
        return null;
    }

    /** @deprecated modify the context's transformedTerms map instead **/
    public String getTransformedQuery() {
        return getContext().getTransformedQuery();
    }

    public void visitImpl(final Object clause) {
        LOG.info(INFO_OLD_IMPLEMENTATION_STILL+getClass().getName());
    }


}

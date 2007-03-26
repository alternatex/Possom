// Copyright (2006) Schibsted Søk AS
package no.schibstedsok.searchportal.query.transform;


import no.schibstedsok.searchportal.query.XorClause;
import no.schibstedsok.searchportal.query.parser.AbstractReflectionVisitor;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;

/**
 * AbstractQueryTransformer is part of no.schibstedsok.searchportal.query
 *
 * @author <a href="ola@schibstedsok.no">ola at schibstedsok</a>

 * @vesrion $Id: AbstractQueryTransformer.java 3359 2006-08-03 08:13:22Z mickw $
 */
public abstract class AbstractQueryTransformer extends AbstractReflectionVisitor implements QueryTransformer {

    private static final Logger LOG = Logger.getLogger(AbstractQueryTransformer.class);

    private static final String INFO_OLD_IMPLEMENTATION_STILL = " has not been adapted to Visitor pattern";

    private Context context;
    
    /** Only to be used by XStream and tests **/
    protected AbstractQueryTransformer(){
    }

    //@Override // TODO uncomment for java 6
    public void setContext(final Context cxt) {
        context = cxt;
    }

    protected Context getContext() {
        return context;
    }

    //@Override // TODO uncomment for java 6
    public String getFilter() {
        return "";
    }

    //@Override // TODO uncomment for java 6
    public String getFilter(final java.util.Map parameters) {
        return "";
    }

    /** @deprecated modify the context's transformedTerms map instead **/
    //@Override // TODO uncomment for java 6
    public String getTransformedQuery() {
        return getContext().getTransformedQuery();
    }

    protected void visitImpl(final Object clause) {
        LOG.info( getClass().getSimpleName() + INFO_OLD_IMPLEMENTATION_STILL);
    }

    /** Callback through getContext().visitXorClause(this, clause). **/
    protected final void visitImpl(final XorClause clause) {
        
        getContext().visitXorClause(this, clause);
    }

    //@Override // TODO uncomment for java 6
    public Object clone() throws CloneNotSupportedException {
        final AbstractQueryTransformer retValue = (AbstractQueryTransformer)super.clone();
        retValue.context = context;
        return retValue;
    }

    //@Override // TODO uncomment for java 6
    public QueryTransformer readQueryTransformer(final Element element){
        
        // Override me to add custom deserialisation
        return this;
    }
}

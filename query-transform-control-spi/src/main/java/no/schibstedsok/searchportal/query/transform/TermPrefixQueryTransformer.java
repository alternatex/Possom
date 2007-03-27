/*
 * Copyright (2005-2007) Schibsted Søk AS
 */
package no.schibstedsok.searchportal.query.transform;

import java.util.Map;
import no.schibstedsok.searchportal.query.AndClause;
import no.schibstedsok.searchportal.query.Clause;
import no.schibstedsok.searchportal.query.DefaultOperatorClause;
import no.schibstedsok.searchportal.query.IntegerClause;
import no.schibstedsok.searchportal.query.LeafClause;
import no.schibstedsok.searchportal.query.OperationClause;
import no.schibstedsok.searchportal.query.OrClause;
import no.schibstedsok.searchportal.query.PhoneNumberClause;
import no.schibstedsok.searchportal.site.config.AbstractDocumentFactory;
import no.schibstedsok.searchportal.site.config.AbstractDocumentFactory.ParseType;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;

/**
 * A transformer to prefix the terms in a query.
 *
 * @version $Id$
 * @author <a href="mailto:magnus.eklund@sesam.no">Magnus Eklund</a>
 *
 */
public final class TermPrefixQueryTransformer extends AbstractQueryTransformer {

    private static final Logger LOG = Logger.getLogger(TermPrefixQueryTransformer.class);

    private final TermPrefixQueryTransformerConfig config;

    /**
     *
     * @param config
     */
    public TermPrefixQueryTransformer(final QueryTransformerConfig config){
        this.config = (TermPrefixQueryTransformerConfig)config;
    }

    /**
     * This is th default fallback. Adds the prefix in the <code>prefix</code>
     * property
     *
     * @param clause The clause to prefix.
     */
     public void visitImpl(final LeafClause clause) {
        if (clause.getField() == null || getContext().getFieldFilter(clause) == null) {
            addPrefix(clause, config.getPrefix());
        }
    }

    /**
     * Add prefix to an integer clause.
     *
     * @param clause The clause to prefix.
     */
    public void visitImpl(final IntegerClause clause) {
        addPrefix(clause, config.getNumberPrefix());
    }

    /**
     * Add prefixes to an or clause. The two operand clauses are prefixed
     * individually.
     *
     * @param clause The clause to prefix.
     */
    public void visitImpl(final OrClause clause) {
        clause.getFirstClause().accept(this);
        clause.getSecondClause().accept(this);
    }

    /**
     * Add prefixes to an default operator clause. The two operand clauses are prefixed
     * individually.
     *
     * @param clause The clause to prefix.
     */
    public void visitImpl(final DefaultOperatorClause clause) {
        clause.getFirstClause().accept(this);
        clause.getSecondClause().accept(this);
    }

    /**
     * Add prefixes to an and operator clause. The two operand clauses are prefixed
     * individually.
     *
     * @param clause The clause to prefix.
     */
    public void visitImpl(final AndClause clause) {
        clause.getFirstClause().accept(this);
        clause.getSecondClause().accept(this);
    }

    /**
     * Add prefixes to an generic operator clause. The child operand clauses is prefixed
     * individually.
     *
     * @param clause The clause to prefix.
     */
    public void visitImpl(final OperationClause clause) {
        clause.getFirstClause().accept(this);
    }

    /**
     * Prefix a phone number clause with the number prefix.
     *
     * @param clause  The clause to prefix.
     */
    public void visitImpl(final PhoneNumberClause clause) {
        addPrefix(clause, config.getNumberPrefix());
    }

    private void addPrefix(final Clause clause, final String prefix) {
        final String term = (String) getTransformedTerms().get(clause);

        if (!(term.equals("") || isAlreadyPrefixed(term, prefix))) {
            getTransformedTerms().put(clause, prefix + ':' + term);
        }
    }

    private static boolean isAlreadyPrefixed(final String term, final String prefix) {
        return term.indexOf(prefix + ':') > -1;
    }

    private Map getTransformedTerms() {
        return getContext().getTransformedTerms();
    }

}

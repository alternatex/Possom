/*
 * Copyright (2005-2006) Schibsted Søk AS
 */
package no.schibstedsok.front.searchportal.query.parser;

import java.util.Collections;
import java.util.Set;

/**
 * @version $Id$
 * @author <a href="mailto:mick@wever.org">Michael Semb Wever</a>
 */
public final class AndClause extends AbstractOperationClause {

    private final Clause firstClause;
    private final Clause secondClause;

    /**
     *
     * @param first
     * @param second
     */
    public AndClause(final Clause first, final Clause second) {
        this.firstClause = first;
        this.secondClause = second;
    }

    /**
     *
     * @param visitor
     */
    public void accept(final Visitor visitor) {
        visitor.visit(this);
    }

    /**
     * Get the firstClause.
     *
     * @return the firstClause.
     */
    public Clause getFirstClause() {
        return firstClause;
    }

    /**
     * Get the secondClause.
     *
     * @return the secondClause.
     */
    public Clause getSecondClause() {
        return secondClause;
    }

}

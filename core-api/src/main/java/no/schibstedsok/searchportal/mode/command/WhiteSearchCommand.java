// Copyright (2006-2007) Schibsted Søk AS
/*
 * WhiteSearchCommand.java
 *
 * Created on March 4, 2006, 1:59 PM
 *
 */

package no.schibstedsok.searchportal.mode.command;

import no.schibstedsok.searchportal.query.IntegerClause;
import no.schibstedsok.searchportal.query.LeafClause;
import no.schibstedsok.searchportal.query.PhoneNumberClause;
import no.schibstedsok.searchportal.query.Visitor;
import no.schibstedsok.searchportal.query.XorClause;
import no.schibstedsok.searchportal.query.token.TokenPredicate;

/**
 *
 * @author magnuse
 */
public class WhiteSearchCommand extends CorrectingFastSearchCommand {

    private static final String PREFIX_INTEGER = "whitepages:";
    private static final String PREFIX_PHONETIC = "whitephon:";

    /**
     *
     * @param cxt The context to execute in.
     */
    public WhiteSearchCommand(final Context cxt) {

        super(cxt);
    }

    /**
     * Adds non phonetic prefix to integer terms.
     *
     * @param clause The clause to prefix.
     */
    protected void visitImpl(final IntegerClause clause) {
        if (! getTransformedTerm(clause).equals("")) {
            appendToQueryRepresentation(PREFIX_INTEGER);
        }

        super.visitImpl(clause);
    }

    /**
     * Adds non phonetic prefix to phone number terms.
     *
     * @param clause The clause to prefix.
     */
    protected void visitImpl(final PhoneNumberClause clause) {
        if (! getTransformedTerm(clause).equals("")) {
            appendToQueryRepresentation(PREFIX_INTEGER);
        }
        super.visitImpl(clause);
    }
    /**
     * Adds phonetic prefix to a leaf clause.
     * Remove dots from words. (people, street, suburb, or city names do not have dots.)
     *
     * @param clause The clause to prefix.
     */
    protected void visitImpl(final LeafClause clause) {

        if (null == clause.getField()) {
            if (!getTransformedTerm(clause).equals("")) {
                appendToQueryRepresentation(PREFIX_PHONETIC
                        + getTransformedTerm(clause).replaceAll("\\.|-", QL_AND + PREFIX_PHONETIC));
            }

        }else if(null == getFieldFilter(clause)){

            if (!getTransformedTerm(clause).equals("")) {
                // we also accept terms with fields that haven't been permitted for the searchConfiguration
                appendToQueryRepresentation(PREFIX_PHONETIC
                        + clause.getField() + "\\:"
                        + getTransformedTerm(clause).replaceAll("\\.|-", QL_AND + PREFIX_PHONETIC));

            }

        }
    }

    /**
     * An implementation that ignores phrase searches.
     *
     * Visits only the left clause, unless that clause is a phrase or organisation clause, in
     * which case only the right clause is visited. Phrase and organisation searches are not
     * possible against the white index.
     */
    protected void visitXorClause(final Visitor visitor, final XorClause clause) {

        // If we have a match on an international phone number, but it is not recognized as
        // a local phone number, force it to use the original number string.
        if (clause.getHint() == XorClause.Hint.PHONE_NUMBER_ON_LEFT
                && !clause.getFirstClause().getKnownPredicates().contains(TokenPredicate.PHONENUMBER)) {

            clause.getSecondClause().accept(visitor);

        } else if (clause.getHint() == XorClause.Hint.PHRASE_ON_LEFT
                || clause.getHint() == XorClause.Hint.NUMBER_GROUP_ON_LEFT) {

            clause.getSecondClause().accept(visitor);

        } else {
            clause.getFirstClause().accept(visitor);
        }
    }
}

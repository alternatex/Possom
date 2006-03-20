/*
 * Copyright (2005-2006) Schibsted Søk AS
 */
package no.schibstedsok.front.searchportal.command;

import java.util.Map;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;

import no.fast.ds.search.BaseParameter;
import no.fast.ds.search.SearchParameter;
import no.schibstedsok.front.searchportal.analyzer.AnalysisRule;
import no.schibstedsok.front.searchportal.query.token.TokenEvaluatorFactory;
import no.schibstedsok.front.searchportal.query.token.TokenPredicate;

/**
 * A YellowSearchCommand.
 *
 * @author <a href="magnus.eklund@sesam.no">Magnus Eklund</a>
 * @version $Revision$
 */
public final class YellowSearchCommand extends FastSearchCommand {

    private static final String YELLOWPAGES3_YPNAVN2 = "+yellowpages3 +ypnavn2";

    private static final int MATCH_SCORE = 10;

    /**
     * Create a new YellowSearchCommand.
     *
     * @param query
     *            the current query.
     * @param config
     *            the configuration for this command.
     * @param parameters
     *            parameters.
     */
    public YellowSearchCommand(
            final SearchCommand.Context cxt,
            final Map parameters) {

        super(cxt, parameters);
    }

    /**
     * Creates a search parameter based on query analysis. If the query matches
     * either a company or a priority company the
     * <code>YELLOWPAGES3_YPNAVN2</code> sorting parameter is chosen. Otherwise
     * the default (as specified by the search configuration) sort order is
     * chosen.
     *
     * [TODO] Refactor this method to a more appropriate place. It doesn't belong here (see metrics report).
     *
     * @return a SearchParameter.
     */
    protected SearchParameter getSortByParameter() {

        final Predicate eitherCompany =
                PredicateUtils.orPredicate(TokenPredicate.EXACTCOMPANYNAME, TokenPredicate.PRIOCOMPANYNAME);

        final AnalysisRule rule = new AnalysisRule();
        rule.addPredicateScore(eitherCompany, MATCH_SCORE);

        final TokenEvaluatorFactory factory = context.getRunningQuery().getTokenEvaluatorFactory();

        final int score = rule.evaluate(context.getRunningQuery().getQuery(), factory);

        if (score > 0) {
            return new SearchParameter(BaseParameter.SORT_BY,
                    YELLOWPAGES3_YPNAVN2);
        } else {
            if (getFastConfiguration().getSortBy() != null) {
                return new SearchParameter(BaseParameter.SORT_BY,
                        getFastConfiguration().getSortBy());
            } else {
                return null;
            }
        }
    }
}

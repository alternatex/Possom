/*
 * Copyright (2005-2006) Schibsted Søk AS
 *
 */
package no.schibstedsok.front.searchportal.query;

import com.thoughtworks.xstream.XStream;
import edu.emory.mathcs.backport.java.util.concurrent.CancellationException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import no.schibstedsok.front.searchportal.QueryTokenizer;
import no.schibstedsok.front.searchportal.analyzer.AnalysisRule;
import no.schibstedsok.front.searchportal.analyzer.AnalysisRules;
import no.schibstedsok.front.searchportal.analyzer.RegExpEvaluatorFactory;
import no.schibstedsok.front.searchportal.analyzer.ReportingTokenEvaluator;
import no.schibstedsok.front.searchportal.analyzer.TokenEvaluatorFactory;
import no.schibstedsok.front.searchportal.analyzer.TokenEvaluatorFactoryImpl;
import no.schibstedsok.front.searchportal.analyzer.TokenPredicate;
import no.schibstedsok.front.searchportal.command.SearchCommand;
import no.schibstedsok.front.searchportal.command.impl.SearchCommandFactory;
import no.schibstedsok.front.searchportal.configuration.SearchConfiguration;
import no.schibstedsok.front.searchportal.configuration.SearchMode;
import no.schibstedsok.front.searchportal.configuration.SearchTabsCreator;
import no.schibstedsok.front.searchportal.configuration.XMLSearchTabsCreator;
import no.schibstedsok.front.searchportal.configuration.loaders.DocumentLoader;
import no.schibstedsok.front.searchportal.configuration.loaders.PropertiesLoader;
import no.schibstedsok.front.searchportal.configuration.loaders.XStreamLoader;
import no.schibstedsok.front.searchportal.executor.SearchTask;
import no.schibstedsok.front.searchportal.i18n.TextMessages;
import no.schibstedsok.front.searchportal.query.parser.AbstractQueryParserContext;
import no.schibstedsok.front.searchportal.query.parser.Query;
import no.schibstedsok.front.searchportal.query.parser.QueryParser;
import no.schibstedsok.front.searchportal.query.parser.QueryParserImpl;
import no.schibstedsok.front.searchportal.query.parser.ParseException;
import no.schibstedsok.front.searchportal.result.Enrichment;
import no.schibstedsok.front.searchportal.result.Modifier;
import no.schibstedsok.front.searchportal.result.SearchResult;
import no.schibstedsok.front.searchportal.site.Site;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * An object representing a running queryStr.
 *
 * XXX Pull out an interface from this. Too much implementation for a base class.
 *
 * @author <a href="mailto:magnus.eklund@schibsted.no">Magnus Eklund</a>
 * @version <tt>$Revision$</tt>
 */
public class RunningQuery {

    public interface Context extends SearchTabsCreator.Context {
        SearchMode getSearchMode();
    }

    private static final Log LOG = LogFactory.getLog(RunningQuery.class);


    private final Context context;
    private final AnalysisRules rules;
    private String queryStr = "";
    private Query queryObj = null;
    private Map parameters;
    private int offset;
    private Locale locale;
    private final List sources = new ArrayList();
    private final TokenEvaluatorFactory tokenEvaluatorFactory;
    private final List enrichments = new ArrayList();
    private final Map hits = new HashMap();
    private Map scores = new HashMap();
    private String strippedQueryString;

    private final Collection removers;
    

    /**
     * Create a new Running Query instance.
     *
     * @param mode
     * @param queryStr
     * @param parameters
     */
    public RunningQuery(final Context cxt, final String query, final Map parameters) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("ENTR: RunningQuery(): Params: " + parameters);
        }

        context = cxt;
        queryStr = AdvancedQueryBuilder.trimDuplicateSpaces(query);
        removers = getStopWordRemovers();

        if (queryStr != null) {
            queryStr = queryStr.trim();
        }

        this.parameters = parameters;
        this.locale = new Locale("no", "NO");

        this.strippedQueryString = removeAllPrefixes(this.getQueryString());
        // This will among other things perform the initial fast search
        // for textual analysis.
        tokenEvaluatorFactory = new TokenEvaluatorFactoryImpl(
                new TokenEvaluatorFactoryImpl.Context() {

                    public PropertiesLoader newPropertiesLoader(final String resource, final Properties properties) {
                        return context.newPropertiesLoader(resource, properties);
                    }

                    public XStreamLoader newXStreamLoader(final String resource, final XStream xstream) {
                        return context.newXStreamLoader(resource, xstream);
                    }

                    public DocumentLoader newDocumentLoader(final String resource, final DocumentBuilder builder) {
                        return context.newDocumentLoader(resource, builder);
                    }

                    public Site getSite() {
                        return context.getSite();
                    }

                    public String getQueryString()  {
                        return RunningQuery.this.getQueryString();
                    }

                    public Properties getApplicationProperties() {
                        return XMLSearchTabsCreator.valueOf(cxt).getProperties();
                    }


                });

        // queryStr parser, avoid parsing an empty queryStr.
        if (queryStr != null && queryStr.length() > 0) {
            final QueryParser parser = new QueryParserImpl(new AbstractQueryParserContext() {

                public TokenEvaluatorFactory getTokenEvaluatorFactory() {
                    return tokenEvaluatorFactory;
                }
            });

            try  {
                queryObj = parser.getQuery();
            } catch (ParseException ex)  {
                LOG.error(ex);
            }
        }
        rules = AnalysisRules.valueOf(new AnalysisRules.Context() {
            public PropertiesLoader newPropertiesLoader(final String resource, final Properties properties) {
                return context.newPropertiesLoader(resource, properties);
            }

            public XStreamLoader newXStreamLoader(final String resource, final XStream xstream) {
                return context.newXStreamLoader(resource, xstream);
            }

            public DocumentLoader newDocumentLoader(final String resource, final DocumentBuilder builder) {
                return context.newDocumentLoader(resource, builder);
            }

            public Site getSite() {
                return context.getSite();
            }
        });
        
    }

    private List getTokenMatches(final String token) {
        final ReportingTokenEvaluator e 
                = (ReportingTokenEvaluator) tokenEvaluatorFactory.getEvaluator(TokenPredicate.valueOf(token));
        return e.reportToken(token, queryStr);
    }
    
    public List getGeographicMatches() {
        List matches = new ArrayList();
        
        matches.addAll(getTokenMatches("geolocal"));
        matches.addAll(getTokenMatches("geoglobal"));
        
        Collections.sort(matches);
        
        return matches;
    }
    
    private Collection getStopWordRemovers() {
        
        final TokenPredicate[] prefixes = { 
            TokenPredicate.SITEPREFIX,
            TokenPredicate.CATALOGUEPREFIX,
            TokenPredicate.PICTUREPREFIX,
            TokenPredicate.NEWSPREFIX,
            TokenPredicate.WIKIPEDIAPREFIX,
            TokenPredicate.TVPREFIX,
            TokenPredicate.WEATHERPREFIX
        } ;
        Collection stopWordRemovers = new ArrayList();
        
        final RegExpEvaluatorFactory factory = RegExpEvaluatorFactory.valueOf(new RegExpEvaluatorFactory.Context(){
            public PropertiesLoader newPropertiesLoader(final String resource, final Properties properties) {
                return context.newPropertiesLoader(resource, properties);
            }

            public XStreamLoader newXStreamLoader(final String resource, final XStream xstream) {
                return context.newXStreamLoader(resource, xstream);
            }

            public DocumentLoader newDocumentLoader(final String resource, final DocumentBuilder builder) {
                return context.newDocumentLoader(resource, builder);
            }

            public Site getSite() {
                return context.getSite();
            }
        });
        
        for (int i = 0; i < prefixes.length; i++) {
            final StopWordRemover remover = factory.getStopWordRemover(prefixes[i]);
            if( remover == null ){
                LOG.error("Failed to add "+prefixes[i]);
            }
            stopWordRemovers.add(remover);
        }
        return stopWordRemovers;
    }


    private String removeAllPrefixes(String queryString) {
        for (Iterator iter = removers.iterator(); iter.hasNext();) {
            final StopWordRemover remover = (StopWordRemover) iter.next();
            queryString = remover.removeStopWords(queryString);
        }
        
        return queryString.trim();
    }

    /**
     * First find out if the user types in an advanced search etc by analyzing the queryStr.
     * Then lookup correct tip using messageresources.
     *
     * @return user tip
     */
    public String getGlobalSearchTips () {
        if (LOG.isDebugEnabled())  {
            LOG.debug("ENTR: getGlobalSearchTips()");
        }
        if (AdvancedQueryBuilder.isAdvancedQuery(queryStr)) {
            return TextMessages.getMessages().getMessage(locale,
                    "searchtip.use+-");
        } else {
            return null;
        }
        // return TextMessages.getMessages().getMessage("searchtip.use+-");
    }


    public Integer getNumberOfHits(final String configName) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("ENTR: getNumberOfHits()");
        }
        Integer i = (Integer) hits.get(configName);
        if (i == null) { i = new Integer(0); }
        return i;
    }

    /**
     * Thread run
     *
     * @throws InterruptedException
     */
    public void run() throws InterruptedException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("ENTR: run()");
        }
        try {

            final Collection commands = new ArrayList();

            for (Iterator iterator = context.getSearchMode().getSearchConfigurations().iterator(); iterator.hasNext();) {
                final SearchConfiguration searchConfiguration = (SearchConfiguration) iterator.next();

                final SearchCommand.Context searchCmdCxt = new SearchCommand.Context() {
                    public SearchConfiguration getSearchConfiguration() {
                        return searchConfiguration;
                    }

                    public RunningQuery getQuery() {
                        return RunningQuery.this;
                    }
                    
                    public Site getSite(){
                        return context.getSite();
                    }

                };

                final AnalysisRule rule = rules.getRule(searchConfiguration.getRule());

                if (rule != null) {

                    //if (context.getSearchMode().getKey().equals("d") && offset == 0) {
                    if (context.getSearchMode().isQueryAnalysisEnabled() && offset == 0) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("run: searchMode.getKey().equals(d) && offset == 0");
                        }
                        final int score = rule.evaluate(queryStr, tokenEvaluatorFactory);
                        final int newScore = rule.evaluate(queryObj, tokenEvaluatorFactory);

                        LOG.info("OldScore: " + score + "; NewScore: " + newScore + ";");
                        assert (score == newScore); // if this fails, goto mick, do not pass go, do not collect $200.
                        if (score != newScore) {
                            LOG.fatal("\n\n!!! Old score does not match new score !!!\n\n");
                        }

                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Score for " + searchConfiguration.getName() + " is " + score);
                        }

                        scores.put(searchConfiguration.getName(), new Integer(score));

                        if (searchConfiguration.isAlwaysRunEnabled() || score >= searchConfiguration.getRuleThreshold()) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Adding " + searchConfiguration.getName());
                            }
                            commands.add(SearchCommandFactory.createSearchCommand(searchCmdCxt, parameters));
                        }

                    } else if (searchConfiguration.isAlwaysRunEnabled()) {
                        commands.add(SearchCommandFactory.createSearchCommand(searchCmdCxt, parameters));
                    }
                } else {
                    // Optimisation. Alternate between the two web searches.
                    if (isNorwegian(searchConfiguration) || isInternational(searchConfiguration)) {
                        final String searchType = getSingleParameter("s");
                        if (searchType != null && searchType.equals("g")) {
                            if (isInternational(searchConfiguration)) {
                                commands.add(SearchCommandFactory.createSearchCommand(searchCmdCxt, parameters));
                            }
                        } else if (isNorwegian(searchConfiguration)) {
                            commands.add(SearchCommandFactory.createSearchCommand(searchCmdCxt, parameters));
                        }
                    } else {
                        commands.add(SearchCommandFactory.createSearchCommand(searchCmdCxt, parameters));
                    }
                }
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("run(): InvokeAll Commands.size=" + commands.size());
            }

            final List results = context.getSearchMode().getExecutor().invokeAll(commands, 30000);

            for (Iterator iterator = results.iterator(); iterator.hasNext();) {
                final SearchTask task = (SearchTask) iterator.next();

                final SearchCommand command = task.getCommand();
                final SearchConfiguration configuration = command.getSearchConfiguration();

                if (task.isDone()) {
                    try {
                        final SearchResult searchResult = (SearchResult) task.get();

                        if (searchResult != null) {

                            hits.put(configuration.getName(), new Integer(searchResult.getHitCount()));

                            final Integer score = (Integer) scores.get(task.getCommand().getSearchConfiguration().getName());

                            if (score != null && configuration.getRule() != null && score.intValue() >= task.getCommand().getSearchConfiguration().getRuleThreshold()) {
                                if (searchResult.getResults().size() > 0 && score.intValue() > 15) {
                                    Enrichment e = new Enrichment(score.intValue(), configuration.getName());
                                    enrichments.add(e);
                                }
                            }
                        }
                    } catch (CancellationException e) {
                        LOG.error("Task was cancelled " + task.getCommand());
                    }
                }
            }
            Collections.sort(enrichments);
            Collections.sort(sources);
        } catch (Exception e) {
            LOG.error("Failure to run query.", e);
        }
    }

    private String getSingleParameter(final String paramName) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("ENTR: getSingleParameter()");
        }
        String[] param = (String[]) parameters.get(paramName);

        if (param != null) {
            return param[0];
        } else {
            return null;
        }
    }

    private boolean isInternational(final SearchConfiguration searchConfiguration) {
        return "globalSearch".equals(searchConfiguration.getName());
    }

    private boolean isNorwegian(final SearchConfiguration searchConfiguration) {
        return "defaultSearch".equals(searchConfiguration.getName());
    }

    protected void addParameter(final String key, final Object obj) {
        parameters.put(key, obj);
    }

    public int getNumberOfTerms() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("ENTR: getNumberOfTerms()");
        }
        return QueryTokenizer.tokenize(queryStr).size();
    }


    public String getQueryString() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("ENTR: getQueryString()");
        }
        return queryStr;
    }

    public int getOffset() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("ENTR: getOffset(): " + offset);
        }
        return offset;
    }

    public void setOffset(final int offset) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("ENTR: setOffset():" + offset);
        }
        this.offset = offset;
    }

    public Locale getLocale() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("ENTR: getLocale()");
        }
        return locale;
    }

    public SearchMode getSearchMode() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("ENTR: getSearchMode()");
        }
        return context.getSearchMode();
    }

    public List getSources() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("ENTR: getSources()");
        }
        return sources;
    }

    public void addSource(final Modifier modifier) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("ENTR: addSource()");
        }
        sources.add(modifier);
    }

    public List getEnrichments() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("ENTR: getEnrichments()");
        }
        return enrichments;
    }

    public TokenEvaluatorFactory getTokenEvaluatorFactory() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("ENTR: getTokenEvaluatorFactory()");
        }
        return tokenEvaluatorFactory;
    }

    // Find some other way to do this. Really do!
    public String getSourceParameters(final String source) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("ENTR: getSourceParameters() Source=" + source);
        }

        if (source.equals("Norske nettsider")) {
            return "c=n";
        } else if (source.startsWith("Nyhets")) {
            return "c=m&amp;nav_sources=contentsourcenavigator";
        } else if (source.startsWith("Bild")) {
            return "c=p";
        } else if (source.startsWith("Person")) {
            return "c=w";
        } else if (source.startsWith("Bedrift")) {
            return "c=y";
        } else if (source.equals("Internasjonale nettsider")) {
            return "c=g";
        } else {
            return "c=d";
        }
    }



    /**
     * Get the strippedQueryString.
     *
     * @return the strippedQueryString.
     */
    public String getStrippedQueryString() {
        return strippedQueryString;
    } 



}

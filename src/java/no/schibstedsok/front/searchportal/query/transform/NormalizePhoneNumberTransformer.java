package no.schibstedsok.front.searchportal.query.transform;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import no.schibstedsok.front.searchportal.query.*;

/**
 * @author <a href="mailto:magnus.eklund@schibsted.no">Magnus Eklund</a>
 * @version <tt>$Revision$</tt>
 */
public class NormalizePhoneNumberTransformer extends AbstractQueryTransformer{

    private transient static Pattern countryPrefix = Pattern.compile("^(\\+|00)47");
    private transient static Pattern phoneNumber = Pattern.compile("(\\d)\\s*(\\d)\\s*(\\d)\\s*(\\d)\\s*(\\d)\\s*(\\d)\\s*(\\d)\\s*(\\d)\\s*");

    public String getTransformedQuery(final Context cxt) {
        
        String originalQuery = cxt.getQueryString();

        Matcher m = countryPrefix.matcher(originalQuery);

        originalQuery = m.replaceAll("");

        m = phoneNumber.matcher(originalQuery);

        if (m.matches()) {
            originalQuery = m.replaceAll("$1$2$3$4$5$6$7$8");
        }

        return originalQuery;
    }
}

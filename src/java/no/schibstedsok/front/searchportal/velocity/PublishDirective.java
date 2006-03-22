// Copyright (2006) Schibsted Søk AS
/*
 * PublishDirective.java
 *
 *
 */

package no.schibstedsok.front.searchportal.velocity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import org.apache.log4j.Logger;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.Token;
import org.apache.velocity.runtime.parser.node.Node;

/**
 *
 * A velocity directive to import page fragments from publishing system.
 *
 * <code>
 * #publish('pages/front.html')
 * </code>
 *
 * The default charset is utf-8.
 *
 * @author mick
 * @version $If$
 */
public final class PublishDirective extends Directive {

    private static final Logger LOG = Logger.getLogger(PublishDirective.class);


    private static final String NAME = "publish";
    private static final String DEFAULT_CHARSET = "utf-8";

    /**
     * returns the name of the directive.
     *
     * @return the name of the directive.
     */
    public String getName() {
        return NAME;
    }

    /**
     * returns the type of the directive. The type is LINE.
     * @return The type == LINE
     */
    public int getType() {
        return LINE;
    }

    /**
     * Renders the publish url.
     *
     * @param context
     * @param writer
     * @param node
     *
     * @throws java.io.IOException
     * @throws org.apache.velocity.exception.ResourceNotFoundException
     * @throws org.apache.velocity.exception.ParseErrorException
     * @throws org.apache.velocity.exception.MethodInvocationException
     * @return the encoded string.
     */
    public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
        if (node.jjtGetNumChildren() < 2) {
            rsvc.error("#" + getName() + " - missing argument");
            return false;
        }

        final String charset = DEFAULT_CHARSET;

        final String url = node.jjtGetChild(0).value(context).toString() + ".html";
        final String header = node.jjtGetChild(1).value(context).toString();
        LOG.debug(url);
        final URLConnection urlConn = new URL(url).openConnection();
        urlConn.addRequestProperty("host", header);

        final BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
        for(String line = reader.readLine();line!=null;line=reader.readLine()){
            writer.write(line);
            writer.write('\n');
        }

        final Token lastToken = node.getLastToken();

        if (lastToken.image.endsWith("\n")) {
            writer.write('\n');
        }

        return true;
    }
}

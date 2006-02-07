/*
 * HtmlEscapeDirective.java
 *
 * Created on February 6, 2006, 5:34 PM
 *
 */
package no.schibstedsok.front.searchportal.velocity;

import java.io.IOException;
import java.io.Writer;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.Token;
import org.apache.velocity.runtime.parser.node.Node;

/**
 *
 * A velocity directive to escape HTML.
 *
 * <code>
 * #htmlescape("<h1>html</h1>')
 * </code>
 *
 * @author magnuse
 */
public class HtmlEscapeDirective extends Directive {
    
    private static String NAME = "htmlescape";
    
    /**
     * 
     * @return returns the type of directive. Always returns <code>LINE</code>.
     */
    public int getType() {
        return LINE;
    }
    
    /**
     * 
     * @return returns the name of the directive. Always returns 'htmlescape'.
     */
    public String getName() {
        return NAME;
    }
    
    /**
     * 
     * Renders the html escaped string.
     *
     * @param context 
     * @param writer 
     * @param node 
     * @throws java.io.IOException 
     * @throws org.apache.velocity.exception.ResourceNotFoundException 
     * @throws org.apache.velocity.exception.ParseErrorException 
     * @throws org.apache.velocity.exception.MethodInvocationException 
     * @return 
     */
    public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
        if (node.jjtGetNumChildren() != 1) {
            rsvc.error("#" + getName() + " - wrong number of argumants");
            return false;
        }
        
        String s = node.jjtGetChild(0).value(context).toString();
        
        writer.write(StringEscapeUtils.escapeHtml(s));
        
        Token lastToken = node.getLastToken();
        
        if (lastToken.image.endsWith("\n")) {
            writer.write("\n");
        }
        
        return true;
    }
}

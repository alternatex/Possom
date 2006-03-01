/* Copyright (2005-2006) Schibsted Søk AS
 *
 * AbstractReflectionVisitor.java
 *
 * Created on 7 January 2006, 16:12
 *
 */

package no.schibstedsok.front.searchportal.query.parser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import no.schibstedsok.front.searchportal.query.Visitor;
import org.apache.log4j.Logger;


/** A helper implementation of the Visitor pattern using java's reflection.
 * This results in not having to add overloaded methods for each subclass of clause as this implementation will
 * automatically find those overloaded methods without explicitly having to call them in each Clause class.
 * This saves alot of work when adding new Clause subclasses.
 *
 * The overloaded method name is specified by VISIT_METHOD_IMPL.
 *
 * See http://www.javaworld.com/javaworld/javatips/jw-javatip98.html
 *
 * @version $Id$
 * @author <a href="mailto:mick@wever.org">Michael Semb Wever</a>
 */
public abstract class AbstractReflectionVisitor implements Visitor {

    /** String specifying name of method used to overload by any class extending this. **/
    public static final String VISIT_METHOD_IMPL = "visitImpl";

    private static final Logger LOG = Logger.getLogger(AbstractReflectionVisitor.class);

    private static final String ERR_CLAUSE_SUBTYPE_NOT_FOUND = "Current visitor implementation does not handle visiting "
            + "non clause subtypes. Tried to visit object ";
    private static final String ERR_FAILED_TO_VISIT = "Failed to visit object ";
    private static final String ERR_FAILED_TO_FIND_VISIT_IMPL_OBJECT = "Failed to find method that exists in this class!!"
            + "Was trying to visit object ";
    private static final String DEBUG_LOOKING_AT = "Looking for method "
            + VISIT_METHOD_IMPL + "(";
    private static final String RB = ")";



    /** Creates a new instance of AbstractReflectionVisitor.
     */
    public AbstractReflectionVisitor() {
    }

    /**
     * Method implementing Visitor interface. Uses reflection to find the method with name VISIT_METHOD_IMPL with the
     * closest match to the clause subclass.
     * @param clause the clause we're visiting.
     */
    public void visit(final Object clause) {
        final Method method = getMethod(clause.getClass());
        try {
            method.setAccessible(true);
            method.invoke(this, new Object[] {clause});

        } catch (IllegalArgumentException ex) {
            LOG.error(ERR_FAILED_TO_VISIT + clause, ex);
        } catch (InvocationTargetException ex) {
            LOG.error(ERR_FAILED_TO_VISIT + clause, ex);
        } catch (IllegalAccessException ex) {
            LOG.error(ERR_FAILED_TO_VISIT + clause, ex);
        }finally{
            method.setAccessible(false);
        }
    }

    /**
     * Final fallback method. This means that the object being visited is not a Clause (or subclass of) object!
     * This behaviour is not intendedly supported and this implementation throws an IllegalArgumentException!
     * @param clause the clause we're visiting (that's not acutally a clause subtype ;)
     */
    public void visitImpl(final Object clause) {
        throw new IllegalArgumentException(ERR_CLAUSE_SUBTYPE_NOT_FOUND + clause.getClass().getName());
    }

    private Method getMethod(final Class clauseClass) {
        final Class me = getClass();
        Method method = null;

        LOG.trace("getMethod(" + clauseClass.getName() + ")");

        // Try the superclasses
        Class currClauseClass = clauseClass;
        while (method == null && currClauseClass != Object.class) {
            LOG.debug(DEBUG_LOOKING_AT + currClauseClass.getName() + RB);
            try {
                method = me.getMethod(VISIT_METHOD_IMPL, new Class[] {currClauseClass});

            } catch (NoSuchMethodException e) {
                currClauseClass = currClauseClass.getSuperclass();
            }
        }

        // Try the interfaces.
        // Gets alittle bit tricky because we must not only search subinterfaces
        //  but search both interfaces and superinterfaces of superclasses...
        currClauseClass = clauseClass;
        while (method == null && currClauseClass != Object.class) {

            method = getMethodFromInterface(currClauseClass);
            currClauseClass = currClauseClass.getSuperclass();
        }

        // fallback to visitImpl(Object)
        if (method == null) {
            try {
                
                method = me.getMethod(VISIT_METHOD_IMPL, new Class[] {Object.class});

            } catch (SecurityException ex) {
                LOG.error(ERR_FAILED_TO_FIND_VISIT_IMPL_OBJECT + clauseClass.getName(), ex);
            } catch (NoSuchMethodException ex) {
                LOG.fatal(ERR_FAILED_TO_FIND_VISIT_IMPL_OBJECT + clauseClass.getName(), ex);
            }

        }
        LOG.trace("end getMethod(" + clauseClass.getName() + ")");
        return method;
    }

    /** The interfaces in this array will already be in a suitable order.
        According to java reflection's getMethod contract this order will match the order listed in the
        implements(/extends) definition of the Clause subclass.
     **/
    private Method getMethodFromInterface(final Class clauseClass) {
        final Class me = getClass();
        Method method = null;

        LOG.trace("getMethodFromInterface(" + clauseClass.getName() + ")");

        final Class[] interfaces = clauseClass.getInterfaces();
        for (int i = 0; i < interfaces.length && method == null; i++) {

            LOG.debug(DEBUG_LOOKING_AT + interfaces[i].getName() + RB);

            try {
                method = me.getMethod(VISIT_METHOD_IMPL, new Class[] {interfaces[i]});
                LOG.debug("Found <" + method + ">");

            } catch (NoSuchMethodException e) {
                // [RECURSION] Look for super interfaces
                method = getMethodFromInterface(interfaces[i]);
                // still null? look at next interface
            }
        }

        LOG.trace("end getMethodFromInterface(" + clauseClass.getName() + ")");
        return method;
    }

}

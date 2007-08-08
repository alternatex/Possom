// Copyright (2006-2007) Schibsted Søk AS
/*
 * SearchCommandFactory.java
 *
 * Created on January 5, 2006, 10:17 AM
 *
 */

package no.schibstedsok.searchportal.mode;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import no.schibstedsok.commons.ioc.BaseContext;
import no.schibstedsok.commons.ioc.ContextWrapper;

import no.schibstedsok.searchportal.mode.command.SearchCommand;
import no.schibstedsok.searchportal.mode.config.CommandConfig.Controller;
import no.schibstedsok.searchportal.mode.config.SearchConfiguration;
import no.schibstedsok.searchportal.site.config.*;
import no.schibstedsok.searchportal.site.SiteContext;


/** This factory creates the appropriate command for a given SearchConfiguration.
 *
 * @author mick
 * @version $Id: SearchCommandFactory.java 3359 2006-08-03 08:13:22Z mickw $
 */
public final class SearchCommandFactory {

    public interface Context extends SiteContext, BytecodeContext {}

    private final Context context;

    /**
     *
     * @param context
     */
    public SearchCommandFactory(final Context context) {
        this.context = context;
    }


    /** Create the appropriate command given the configuration inside the context.
     *
     * @param cxt
     * @return
     */
    public SearchCommand getController(final SearchCommand.Context cxt){

        final SearchConfiguration config = cxt.getSearchConfiguration();

        final String controllerName = "no.schibstedsok.searchportal.mode.command."
                + config.getClass().getAnnotation(Controller.class).value();

        try{

            final SiteClassLoaderFactory.Context classContext = ContextWrapper.wrap(
                    SiteClassLoaderFactory.Context.class,
                    new BaseContext() {
                        public Spi getSpi() {
                            return Spi.SEARCH_COMMAND_CONTROL;
                        }
                    },
                    context
                );

            final SiteClassLoaderFactory loaderFactory = SiteClassLoaderFactory.valueOf(classContext);

            @SuppressWarnings("unchecked")
            final Class<? extends SearchCommand> cls
                    = (Class<? extends SearchCommand>) loaderFactory.getClassLoader().loadClass(controllerName);

            final Constructor<? extends SearchCommand> constructor = cls.getConstructor(SearchCommand.Context.class);

            return constructor.newInstance(cxt);

        } catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException(ex);
        } catch (NoSuchMethodException ex) {
            throw new IllegalArgumentException(ex);
        } catch (InvocationTargetException ex) {
            throw new IllegalArgumentException(ex);
        } catch (InstantiationException ex) {
            throw new IllegalArgumentException(ex);
        } catch (IllegalAccessException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
}

package no.sesat.searchportal.view.navigation;

import java.io.Serializable;
import no.sesat.searchportal.site.config.AbstractDocumentFactory;
import static no.sesat.searchportal.site.config.AbstractDocumentFactory.ParseType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;

/**
 * This is a command to help generating navigation urls in the view. I got tired of all
 * the URL handling velocity code.
 * <p/>
 * This should be a multiResult resulthandler, but right now this just a waiting searchCommand.
 * Usually there will be no real waiting since the calls on the results occur from velocity.
 * <p/>
 * As a bonus from using this, you don't need to data-model the commands that only are
 * there for navigation.
 *
 * @author Geir H. Pettersen(T-Rank)
 * @version $Id$
 */
public final class NavigationConfig implements Serializable {

    private final Map<String, Nav> navMap = new HashMap<String, Nav>();
    private final Map<String, Navigation> navigationMap = new HashMap<String, Navigation>();
    private final List<Navigation> navigationList = new ArrayList<Navigation>();

    public NavigationConfig(final NavigationConfig inherit) {
            
        // inheritence first so that self-configuration can override
        if(null != inherit){
            navMap.putAll(inherit.getNavMap());
            navigationMap.putAll(inherit.getNavigationMap());
            navigationList.addAll(inherit.getNavigationList());
        }
    }

    public Map<String, Nav> getNavMap() {
        return Collections.unmodifiableMap(navMap);
    }

    public Map<String, Navigation> getNavigationMap() {
        return Collections.unmodifiableMap(navigationMap);
    }

    public List<Navigation> getNavigationList() {
        return Collections.unmodifiableList(navigationList);
    }

    public void addNavigation(final Navigation navigation) {

        if (navigation.getId() != null) {
            if (navigationMap.containsKey(navigation.getId())) {
                for (Iterator<Navigation> iterator = navigationList.iterator(); iterator.hasNext();) {
                    final Navigation n = iterator.next();
                    if (navigation.getId().equals(n.getId())) {
                        iterator.remove();
                    }
                }
            }

            navigationMap.put(navigation.getId(), navigation);
        }
        navigationList.add(navigation);
    }

    private static List<Element> getDirectChildren(final Element element, final String elementName) {

        final List<Element> children = new ArrayList<Element>();
        if (element != null) {
            final NodeList childNodes = element.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                final Node childNode = childNodes.item(i);
                if (childNode instanceof Element && childNode.getNodeName().equals(elementName)) {
                    children.add((Element) childNode);
                }
            }
        }
        return children;
    }


    public static final class Navigation implements Serializable {

        private String id;
        private String commandName;
        private String tab;
        private boolean out = false;
        private List<Nav> navList;
        private Map<String, Nav> navMap;
        private Set<String> resetNavSet;

        public Navigation() {
        }

        public Navigation(final Element navigationElement) {

            AbstractDocumentFactory.fillBeanProperty(this, null, "id", ParseType.String, navigationElement, null);
            AbstractDocumentFactory.fillBeanProperty(this, null, "commandName", ParseType.String, navigationElement, null);
            AbstractDocumentFactory.fillBeanProperty(this, null, "tab", ParseType.String, navigationElement, null);
            AbstractDocumentFactory.fillBeanProperty(this, null, "out", ParseType.Boolean, navigationElement, null);

            this.navList = new ArrayList<NavigationConfig.Nav>();
            this.navMap = new HashMap<String, NavigationConfig.Nav>();
            this.resetNavSet = new HashSet<String>();
        }

        public void addReset(final Nav nav) {
            if (nav != null) {
                resetNavSet.add(nav.getField());
                for (Nav childNav : nav.getChildNavs()) {
                    addReset(childNav);
                }
            }
        }

        public void addNav(final Nav nav, final NavigationConfig cfg) {
            navList.add(nav);
            updateNavMap(nav, cfg.navMap);
            updateNavMap(nav, navMap);
        }

        private void updateNavMap(final Nav nav, final Map<String, Nav> navMap) {
            navMap.put(nav.getId(), nav);
            for (Nav subNav : nav.getChildNavs()) {
                updateNavMap(subNav, navMap);
            }
        }

        public String getId() {
            return id;
        }

        public void setId(final String id) {
            this.id = id;
        }

        public Map<String, Nav> getNavMap() {
            return navMap;
        }

        public List<Nav> getNavList() {
            return navList;
        }

        public Set<String> getResetNavSet() {
            return resetNavSet;
        }

        public void setNavList(final List<Nav> navList) {
            this.navList = navList;
        }

        public String getTab() {
            return tab;
        }

        public void setTab(final String tab) {
            this.tab = tab;
        }

        public String getCommandName() {
            return commandName;
        }

        public void setCommandName(final String commandName) {
            this.commandName = commandName;
        }

        public boolean isOut() {
            return out;
        }

        public void setOut(final boolean out) {
            this.out = out;
        }

        @Override
        public String toString() {
            return "\nNavigation{" +
                    "commandName='" + commandName + '\'' +
                    ", tab='" + tab + '\'' +
                    ", out=" + out +
                    ", navList=" + navList +
                    ", resetNavSet=" + resetNavSet +
                    '}';
        }
    }

    @Nav.ControllerFactory("no.sesat.searchportal.view.navigation.NoOpNavigationControllerFactoryImpl")
    public static class Nav implements Serializable {

        private static final String OPTION_ELEMENT = "option";
        private static final String STATIC_PARAMETER_ELEMENT = "static-parameter";
        private String id;
        private String commandName;
        private String field;
        private String tab;
        private String backText;
        private boolean out;
        private boolean realNavigator;

        private Map<String, String> staticParameters;
        private List<Nav> childNavs;
        private final Navigation navigation;
        private final Nav parent;
        private boolean excludeQuery = false;

        public Nav(final Nav parent, final Navigation navigation, final Element navElement) {

            this.navigation = navigation;
            this.parent = parent;
            this.childNavs = new ArrayList<Nav>(1);

            AbstractDocumentFactory.fillBeanProperty(
                    this,
                    null,
                    "commandName",
                    ParseType.String,
                    navElement,
                    navigation.getCommandName());

            AbstractDocumentFactory
                    .fillBeanProperty(this, null, "id", ParseType.String, navElement, null);

            AbstractDocumentFactory
                    .fillBeanProperty(this, null, "field", ParseType.String, navElement, id);

            AbstractDocumentFactory
                    .fillBeanProperty(this, null, "tab", ParseType.String, navElement, navigation.getTab());

            AbstractDocumentFactory.fillBeanProperty(
                    this,
                    null,
                    "out",
                    ParseType.Boolean,
                    navElement,
                    Boolean.toString(navigation.isOut()));

            AbstractDocumentFactory
                    .fillBeanProperty(this, null, "excludeQuery", ParseType.Boolean, navElement, "false");

            AbstractDocumentFactory
                    .fillBeanProperty(this, null, "realNavigator", ParseType.Boolean, navElement, "true");

            AbstractDocumentFactory
                    .fillBeanProperty(this, null, "backText", ParseType.String, navElement, "");


            final List<Element> optionElements = getDirectChildren(navElement, OPTION_ELEMENT);
            final List<Element> staticParamElements = getDirectChildren(navElement, STATIC_PARAMETER_ELEMENT);
            staticParameters = new HashMap<String, String>();
            for (Element staticParamElement : staticParamElements) {
                String name = staticParamElement.getAttribute("name");
                String value = staticParamElement.getAttribute("value");
                if (name != null && value != null) {
                    staticParameters.put(name, value);
                }
            }
        }

        public Nav(final Navigation navigation, final Element navElement) {
            this(null, navigation, navElement);
        }

        public Nav getParent() {
            return parent;
        }

        public Navigation getNavigation() {
            return navigation;
        }

        public void addChild(final Nav nav) {
            childNavs.add(nav);
        }

        public List<Nav> getChildNavs() {
            return childNavs;
        }

        public boolean isRealNavigator() {
            return realNavigator;
        }

        public void setRealNavigator(final boolean realNavigator) {
            this.realNavigator = realNavigator;
        }

        public Map<String, String> getStaticParameters() {
            return staticParameters;
        }

        public void setStaticParameters(final Map<String, String> staticParameters) {
            this.staticParameters = staticParameters;
        }

        public void setExcludeQuery(final boolean excludeQuery) {
            this.excludeQuery = excludeQuery;
        }

        public boolean isExcludeQuery() {
            return excludeQuery;
        }

        public String getId() {
            return id;
        }

        public void setId(final String id) {
            this.id = id;
        }

        public String getTab() {
            return tab;
        }

        public void setTab(final String tab) {
            this.tab = tab;
        }

        public boolean isOut() {
            return out;
        }

        public void setOut(final boolean out) {
            this.out = out;
        }

        public String getCommandName() {
            return commandName;
        }

        public void setCommandName(final String commandName) {
            this.commandName = commandName;
        }

        public String getField() {
            return field;
        }

        public void setField(final String field) {
            this.field = field;
        }

        public String getBackText() {
            return backText;
        }

        public void setBackText(String backText) {
            this.backText = backText;
        }

        @Override
        public String toString() {

            return "Nav{"
                    + "id='" + id + '\''
                    + ", commandName='" + commandName + '\''
                    + ", field='" + field + '\''
                    + ", staticParameters=" + staticParameters
                    + '}';
        }

        /**
         *
         */
        @Documented
        @Retention(RetentionPolicy.RUNTIME)
        @Target({ElementType.TYPE})
        @Inherited
        public @interface ControllerFactory {
            /**
             *
             * @return
             */
            public String value();
        }
    }
}
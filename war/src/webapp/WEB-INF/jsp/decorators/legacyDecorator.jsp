<%-- Copyright (2006-2007) Schibsted Søk AS
  --
  -- XXX Will be removed along with VelocityResultHandler.
  --
  -- @version $Id$
--%>
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ page import="java.net.URLEncoder"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Map"%>
<%@ page import="com.opensymphony.module.sitemesh.Page"%>
<%@ page import="no.schibstedsok.searchportal.datamodel.DataModel"%>
<%@ page import="no.schibstedsok.searchportal.view.config.SearchTab"%>
<%@ page import="com.opensymphony.module.sitemesh.RequestConstants"%>
<%@ page import="no.schibstedsok.searchportal.view.i18n.TextMessages"%>
<%@ page import="no.schibstedsok.searchportal.site.Site"%>
<%@ page import="no.schibstedsok.searchportal.result.Linkpulse"%>
<%@ page import="no.schibstedsok.searchportal.site.config.SiteConfiguration"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/page" prefix="page" %>
<%@ taglib uri="/WEB-INF/SearchPortal.tld" prefix="search" %>
<%
final DataModel datamodel = (DataModel)session.getAttribute(DataModel.KEY);
final TextMessages text = (TextMessages) request.getAttribute("text");
final Site site = datamodel.getSite().getSite();
final SearchTab tab = (SearchTab)request.getAttribute("tab");
String currentC = "d";    //default collection
currentC = (String) request.getAttribute("c");
String q = datamodel.getQuery().getXmlEscaped();
final boolean publish = null != datamodel.getParameters().getValue("page");
final String ss = null == datamodel.getParameters().getValue("ss")
        ? "" : datamodel.getParameters().getValue("ss").getUtf8UrlEncoded();
final String ssr = null == datamodel.getParameters().getValue("ssr")
        ? "" : datamodel.getParameters().getValue("ssr").getUtf8UrlEncoded();
final String vertikal = null == datamodel.getParameters().getValue("vertikal") 
        ? "" : datamodel.getParameters().getValue("vertikal").getUtf8UrlEncoded();
String commandname = "defaultSearch";

final Page siteMeshPage = (Page) request.getAttribute(RequestConstants.PAGE);
pageContext.setAttribute("siteMeshPage", siteMeshPage);

final Linkpulse linkpulse = new Linkpulse(site, SiteConfiguration.valueOf(site).getProperties());

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <search:velocity template="/fragments/head"/>
</head>

<body onload="SesamInit.onLoad('<%= currentC %>', '<%= vertikal %>', '<%= q.trim() %>', <%= publish %>);">

    <%if (q.trim().equals("") && ((currentC.equals("m") && vertikal.equals("m")) || currentC.equals("y") || currentC.equals("yg") || currentC.equals("w") || currentC.equals("p") || currentC.equals("pp") || currentC.equals("sw") || currentC.equals("b") )) {%>
            <decorator:getProperty property="page.newsearch-bar"/>
	<% }else if(q.trim().equals("") && currentC.equals("d") && publish) {%>
	    <decorator:getProperty property="page.omsesam-bar"/>
	<% }else{ %>
	    <decorator:getProperty property="page.search-bar"/>
	<% } %>

        <table cellspacing="0" cellpadding="0" id="body_table">
        <%if(q.trim().equals("") && currentC.equals("d") && publish) {%>
            <tr>
        <%} else if (q.trim().equals("") && !currentC.equals("m") && !currentC.equals("l") && !currentC.equals("t") && !currentC.equals("wt")) {%>
            <tr>
                <td class="cell_one">&nbsp;</td>
                <td class="cell_three">&nbsp;</td>
                <td class="cell_four">&nbsp;</td>
            </tr>
        <% }else{ %>
            <tr>
                <%if (q.trim().equals("") && currentC.equals("m") && vertikal.equals("m")) {%>
                <%}else if ((currentC.equals("b") || currentC.equals("m") || currentC.equals("l") || currentC.equals("d") || currentC.equals("g") || currentC.equals("pss")) || !q.trim().equals("") || currentC.equals("t") || currentC.equals("wt")) {%>
                    <td class="cell_one" valign="top">
                        <%if (q.trim().equals("") && (currentC.equals("t") || currentC.equals("m") || currentC.equals("l"))) { %>
                            <decorator:getProperty property="page.frontMenu"/>
                        <% } else { %>     
                            <search:velocity template="/navigators/navbarMain"/>
                        <% } %>
                        <% if (currentC.equals("m")) { %>
                            <search:velocity template="navigators/newsSearchNavigator" command="newsSearchNavigator"/>
                            <decorator:getProperty property="page.media-collection-nav"/>
                            <search:velocity template="navigators/newsSearchLastNews" command="newsSearchLastNews"/>
                        <%}else {%>
                            <decorator:getProperty property="page.sub-navigator"/>
                        <%}%> 
                        <decorator:getProperty property="page.relevantQueries" />

                        <search:velocity template="navigators/scanpix" command="scanpix"/>
                        <decorator:getProperty property="page.blogDateNavigation"/>

                        <c:if test='${tab.showRss}'>
                            <decorator:getProperty property="page.rss-nav"/>
                        </c:if>
                    </td>
                <% } %>
        <% } %>
        <%if (q.trim().equals("") && !currentC.equals("t") && !currentC.equals("l") && !currentC.equals("m") && !currentC.equals("wt")) {%>
            <td valign="top" colspan="3">
        <%}else if (!currentC.equals("y") && !currentC.equals("yip") && !currentC.equals("w") && !currentC.equals("wip")&& !currentC.equals("swip") && !currentC.equals("wipgift")) {%>
            <td class="cell_three" valign="top">
        <% }else{ %>
            <td class="cell_three" valign="top" colspan="2">
 	<%}%>

        <%if (q.trim().equals("") && !currentC.equals("m") && !currentC.equals("l") && !currentC.equals("t") && !currentC.equals("wt")) {%>
	<%}else {%>
            <%if (currentC.equals("d") || currentC.equals("g") || currentC.equals("p") || currentC.equals("pp") || currentC.equals("pip") || currentC.equals("pipn") || currentC.equals("t") || currentC.equals("wt")) {%>
                <search:velocity template="fragments/middlebar" />
            <% }else if(q.trim().equals("") && currentC.equals("m") && vertikal.equals("m")){ %>	
            <% }else { %>
                <decorator:getProperty property="page.middle-bar"/>
            <% } %>
       	<% } %>

        <decorator:getProperty property="page.publishing_page"/>
        <decorator:getProperty property="page.spellcheck"/>
        <decorator:getProperty property="page.main_ads"/>
        <search:velocity template="fragments/top3AdsTop" command="top3Ads"/>
        <%if (currentC.equals("d") || "g".equals(currentC) ) {%>    
            <search:velocity template="/enrichments/enrichment-handler"/>
        <% } else if (q.trim().equals("") && currentC.equals("m") && vertikal.equals("m")) {%>
        <% } else if (currentC.equals("p") || currentC.equals("pp") || currentC.equals("pip")) {%>
            <div>            
                <% if (currentC.equals("p")) {%>
                    <decorator:getProperty property="page.picsearch-results"/>
                <% } else { %>
                    <search:velocity template="results/scanpix" command="scanpix"/>                    
                    <decorator:getProperty property="page.picsearch-results"/>
                <%}%>
                <div class="clearFloat">&nbsp;</div>
            </div>
        <% } else if (currentC.equals("t")) { %>
            <search:velocity template="results/tvSearch" command="tvSearch"/>
        <% } else { %>
            <decorator:getProperty property="page.search-results"/>
            <search:velocity template="results/whitePages" command="whitePages"/>
            <search:velocity template="results/yellowPages" command="yellowPages"/>
            <search:velocity template="results/giftProviders" command="giftProviders"/>            
        <%}%>      

        </td>
        <td class="cell_four">
            <% if (currentC.equals("p") || currentC.equals("pp") || currentC.equals("pip") ) {%>
                <decorator:getProperty property="page.ads"/>
                <decorator:getProperty property="page.ads-logo"/>
            <%}else if (currentC.equals("b") ) {%>
                <decorator:getProperty property="page.feedback"/>
            <%} else if (currentC.equals("t")) {%>
                <decorator:getProperty property="page.ads"/>
            <%} else {%>
                <decorator:getProperty property="page.ads"/>
            <%}%>
        </td>
    </tr>
    <% if ( currentC.equals("sw") || currentC.equals("swip") ) {%>
        <decorator:getProperty property="page.ads_floating"/>
    <% } %>
    <tr>
        <td>&nbsp;</td>
        <td colspan="2">
          <%--  offset  --%>
          <%if (currentC.equals("pp")) {%>
            <search:velocity template="fragments/offsetPager" command="scanpix"/>
          <% } else { %>
              <decorator:getProperty property="page.offsetPager"/>
          <% } %>
        </td>
    </tr>
    <tr>
        <td>&nbsp;</td>
        <td>
          <search:velocity template="fragments/top3AdsBottom" command="top3Ads"/>
        </td>
        <td>&nbsp;</td>
    </tr>
     
</table>
<decorator:getProperty property="page.verbosePager"/>
<decorator:getProperty property="page.footer"/>

<decorator:getProperty property="page.map-script"/>

<search:velocity template="fragments/gallup" />
</body>
</html>

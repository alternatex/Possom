<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0"
        xmlns:jsp="http://java.sun.com/JSP/Page"
        xmlns:c="http://java.sun.com/jsp/jstl/core"
        xmlns:search="urn:jsptld:/WEB-INF/SearchPortal.tld"><!-- XXX a little awkward since SearchPortal.tld never exists in the skin -->
<!-- 
 * Copyright (2008) Schibsted Søk AS
 *   This file is part of SESAT.
 *
 *   SESAT is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   SESAT is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with SESAT.  If not, see <http://www.gnu.org/licenses/>.
 *
    Author     : mick
    Version    : $Id$
-->
<div id="footer_help">
    <jsp:text><![CDATA[<div id="copyright">&copy;2008</div>]]></jsp:text>
    <div id="builtWithSesat"><search:text key="builtWithSesat"/></div>
    <div id="resultsFromYahoo"><search:text key="resultsFromYahoo"/></div>
</div>

<!-- This is to collect statistics against http://sesam.com -->
<jsp:text><![CDATA[
    <script type='text/javascript' src='/javascript/tmv11.js'></script>
    <script type="text/javascript">
        <!--
        var tmsec = new Array(3);
        tmsec[0]="tmsec=sesam";
        tmsec[1]="tmsec=sesamsok";
        tmsec[2]="tmsec=sesamsok";
        getTMqs('','', 'sesam_no', 'no', 'iso-8859-15', tmsec);
        //-->
    </script>
    <noscript><div>
        <img src="http://statistik-gallup.net/V11***sesam_no/no/iso-8859-15/tmsec=sesam&amp;tmsec=sesamsok&amp;tmsec=sesamsok" alt="" />
    </div></noscript>
]]></jsp:text>

</jsp:root>

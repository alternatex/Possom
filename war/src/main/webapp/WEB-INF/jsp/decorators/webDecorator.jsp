<%-- Copyright (2006-2007) Schibsted S�k AS
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
  -- Handles the logic on which "templating system" will be apropriate for this http request.
  --
  -- @author <a href="mailto:mick@semb.wever.org">Michael Semb Wever</a>
  -- @version $Id$
  --%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="/WEB-INF/SearchPortal.tld" prefix="search" %>

<search:main/>
<c:if test="${!empty Missing_SearchTabMain_Template}">
    <%@ include file="httpDecorator.jsp" %>
</c:if>
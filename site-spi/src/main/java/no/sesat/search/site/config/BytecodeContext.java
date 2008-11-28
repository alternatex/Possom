/* Copyright (2007-2008) Schibsted Søk AS
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
 */
package no.sesat.search.site.config;

import no.sesat.commons.ioc.BaseContext;
import no.sesat.search.site.SiteContext;

/**
 * Interface providing a way to get bytecode resource loaders.
 *
 *
 */
public interface BytecodeContext extends BaseContext {
    /**
     * Returns a loader for the site and class. If a jarFileName is supplied only that jar file will be used to find the
     * class.
     *
     * @param siteContext the site to load bytecode for
     * @param className the class to load.
     * @return byte code for class.
     * @param jarFileName optional jar file to restrict loader to.
     */
    public BytecodeLoader newBytecodeLoader(SiteContext siteContext, String className, String jarFileName);
}

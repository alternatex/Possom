/* Copyright (2006-2012) Schibsted ASA
 * This file is part of Possom.
 *
 *   Possom is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Possom is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with Possom.  If not, see <http://www.gnu.org/licenses/>.

 */
/*
 * AbstractYahooSearchConfiguration.java
 *
 * Created on June 12, 2006, 10:58 AM
 *
 */

package no.sesat.search.mode.config;

import no.sesat.search.mode.SearchModeFactory.Context;
import no.sesat.search.mode.config.querybuilder.PrefixQueryBuilderConfig;
import no.sesat.search.site.config.AbstractDocumentFactory;
import no.sesat.search.site.config.AbstractDocumentFactory.ParseType;
import org.w3c.dom.Element;

/**
 *
 *
 * @version $Id$
 */
public abstract class AbstractYahooSearchConfiguration extends AbstractXmlSearchConfiguration {


    // Constants -----------------------------------------------------

    // Attributes ----------------------------------------------------

    private String partnerId = "";

    // Static --------------------------------------------------------

    // Constructors --------------------------------------------------

    public AbstractYahooSearchConfiguration(){

        setQueryBuilder(new PrefixQueryBuilderConfig());
    }

    // Public --------------------------------------------------------

    /**
     *
     * @return
     */
    public String getPartnerId() {
        return partnerId;
    }


    /**
     * Setter for property partnerId.
     * @param partner New value of property partnerId.
     */
    public void setPartnerId(final String partner) {
        partnerId = partner;
    }

    // Z implementation ----------------------------------------------

    // Y overrides ---------------------------------------------------

    // Package protected ---------------------------------------------

    // Protected -----------------------------------------------------

    // Private -------------------------------------------------------

    // Inner classes -------------------------------------------------

}

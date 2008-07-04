/*
 * Copyright (2008) Schibsted Søk AS
 * This file is part of SESAT.
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

package no.sesat.search.run.handler;

import java.util.List;
import no.sesat.search.datamodel.DataModel;
import no.sesat.search.datamodel.search.SearchDataObject;
import no.sesat.search.result.ResultItem;
import no.sesat.search.result.ResultList;



/**
 * The handler is responsible for merging (or copying over) the results together.
 *
 * @version $Id$
 *
 */
public final class FederatorRunHandler implements RunHandler{

    private final FederatorRunHandlerConfig config;


    public FederatorRunHandler(final RunHandlerConfig rhc) {
    	config = (FederatorRunHandlerConfig) rhc;
    }


    public void handleRunningQuery(final Context context) {

        final DataModel datamodel = context.getDataModel();

        final SearchDataObject commandTo = datamodel.getSearch(config.getTo());

        switch(config.getBlend()){
            case SEQUEL:

            for(String commandFrom : config.getFrom()){

                final SearchDataObject command = datamodel.getSearch(commandFrom);

                for(int i = 0 ; i < config.getInsertCount(); ++i){

                    if(i < command.getResults().getResults().size()){

                        final ResultItem result = command.getResults().getResults().get(i);
                        insertResult(result, commandTo.getResults(), config.getInsertPosition());
                    }
                }
            }
            break;

            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    private void insertResult(
            final ResultItem result,
            final ResultList<ResultItem> results,
            final int insertPosition) {

        final List<ResultItem> original = results.getResults();

        // duplicate the last result
        results.addResult(original.get(original.size()-1));

        // now shuffle back everything between insertPosition and the last result
        for(int i = results.getResults().size()-2; i > insertPosition; --i){
            results.replaceResult(original.get(i), original.get(i-1));
        }

        // now add the new result in
        results.replaceResult(original.get(insertPosition), result);

    }
}

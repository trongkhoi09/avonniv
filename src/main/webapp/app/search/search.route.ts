import {Route} from '@angular/router';

import {SearchComponent} from './';
import {GrantsResolvePagingParams} from '../grants/grants.route';

export const SEARCH_ROUTE: Route = {
    path: 'search/:search',
    component: SearchComponent,
    data: {
        authorities: [],
        pageTitle: 'search.title'
    },
    resolve: {
        'pagingParams': GrantsResolvePagingParams
    }
};

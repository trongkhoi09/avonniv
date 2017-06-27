import {ActivatedRouteSnapshot, Resolve, Route, RouterStateSnapshot} from '@angular/router';

import {GrantssComponent} from './grants.component';
import {Injectable} from '@angular/core';
import {PaginationUtil} from 'ng-jhipster';

@Injectable()
export class GrantssResolvePagingParams implements Resolve<any> {

    constructor(private paginationUtil: PaginationUtil) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const page = route.queryParams['page'] ? route.queryParams['page'] : '1';
        const sort = route.queryParams['sort'] ? route.queryParams['sort'] : 'id,asc';
        return {
            page: this.paginationUtil.parsePage(page),
            predicate: this.paginationUtil.parsePredicate(sort),
            ascending: this.paginationUtil.parseAscending(sort)
        };
    }
}
export const grantsRoute: Route = {
    path: 'grants',
    component: GrantssComponent,
    data: {
        pageTitle: 'grants.title'
    },
    resolve: {
        'pagingParams': GrantssResolvePagingParams
    },
};

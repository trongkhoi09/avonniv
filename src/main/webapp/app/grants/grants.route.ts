import {ActivatedRouteSnapshot, Resolve, Route, RouterStateSnapshot, Routes} from '@angular/router';

import {GrantsComponent} from './grants.component';
import {Injectable} from '@angular/core';
import {PaginationUtil} from 'ng-jhipster';
import {GrantDialogComponent} from './grant-dialog.component';

@Injectable()
export class GrantsResolvePagingParams implements Resolve<any> {

    constructor(private paginationUtil: PaginationUtil) {
    }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const page = route.queryParams['page'] ? route.queryParams['page'] : '1';
        const sort = route.queryParams['sort'] ? route.queryParams['sort'] : 'openDate,desc';
        return {
            page: this.paginationUtil.parsePage(page),
            predicate: this.paginationUtil.parsePredicate(sort),
            ascending: this.paginationUtil.parseAscending(sort)
        };
    }
}

export const grantsRoute: Routes = [{
    path: 'grants',
    component: GrantsComponent,
    data: {
        pageTitle: 'grants.title'
    },
    resolve: {
        'pagingParams': GrantsResolvePagingParams
    }
}, {
    path: 'grant-management/:grantId/edit',
    component: GrantDialogComponent,
    outlet: 'popup'
}];

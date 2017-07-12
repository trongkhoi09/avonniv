import {Injectable} from '@angular/core';
import {Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes, CanActivate} from '@angular/router';

import {PaginationUtil} from 'ng-jhipster';

import {Principal} from '../../shared';
import {AreaMgmtComponent} from './area-management.component';
import {AreaDialogComponent} from './area-management-dialog.component';
import {AreaDeleteDialogComponent} from './area-management-delete-dialog.component';

@Injectable()
export class AreaResolve implements CanActivate {

    constructor(private principal: Principal) {
    }

    canActivate() {
        return this.principal.identity().then((account) => this.principal.hasAnyAuthority(['ROLE_ADMIN']));
    }
}

@Injectable()
export class AreaResolvePagingParams implements Resolve<any> {

    constructor(private paginationUtil: PaginationUtil) {
    }

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

export const areaMgmtRoute: Routes = [
    {
        path: 'area-management',
        component: AreaMgmtComponent,
        resolve: {
            'pagingParams': AreaResolvePagingParams
        },
        data: {
            pageTitle: 'areaManagement.home.title'
        }
    }
];

export const areaDialogRoute: Routes = [
    {
        path: 'area-management-new',
        component: AreaDialogComponent,
        outlet: 'popup'
    },
    {
        path: 'area-management/:name/edit',
        component: AreaDialogComponent,
        outlet: 'popup'
    },
    {
        path: 'area-management/:name/delete',
        component: AreaDeleteDialogComponent,
        outlet: 'popup'
    }
];

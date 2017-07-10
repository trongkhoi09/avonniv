import {Injectable} from '@angular/core';
import {Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes, CanActivate} from '@angular/router';

import {PaginationUtil} from 'ng-jhipster';

import {Principal} from '../../shared';
import {PublisherMgmtComponent} from './publisher-management.component';
import {PublisherMgmtDetailComponent} from './publisher-management-detail.component';
import {PublisherDialogComponent} from './publisher-management-dialog.component';
import {PublisherDeleteDialogComponent} from './publisher-management-delete-dialog.component';

@Injectable()
export class PublisherResolve implements CanActivate {

    constructor(private principal: Principal) {
    }

    canActivate() {
        return this.principal.identity().then((account) => this.principal.hasAnyAuthority(['ROLE_ADMIN']));
    }
}

@Injectable()
export class PublisherResolvePagingParams implements Resolve<any> {

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

export const publisherMgmtRoute: Routes = [
    {
        path: 'publisher-management',
        component: PublisherMgmtComponent,
        resolve: {
            'pagingParams': PublisherResolvePagingParams
        },
        data: {
            pageTitle: 'publisherManagement.home.title'
        }
    },
    {
        path: 'publisher-management/:name',
        component: PublisherMgmtDetailComponent,
        data: {
            pageTitle: 'publisherManagement.home.title'
        }
    }
];

export const publisherDialogRoute: Routes = [
    {
        path: 'publisher-management-new',
        component: PublisherDialogComponent,
        outlet: 'popup'
    },
    {
        path: 'publisher-management/:name/edit',
        component: PublisherDialogComponent,
        outlet: 'popup'
    },
    {
        path: 'publisher-management/:name/delete',
        component: PublisherDeleteDialogComponent,
        outlet: 'popup'
    }
];

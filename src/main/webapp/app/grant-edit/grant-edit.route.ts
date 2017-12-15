import {Route} from '@angular/router';

import {GrantEditComponent} from './';
import {UserRouteAccessService} from '../shared';

export const GRANT_EDIT_ROUTE: Route = {
    path: 'grant-edit/:grantId',
    component: GrantEditComponent,
    data: {
        authorities: ['ROLE_ADMIN'],
        pageTitle: 'grant.edit'
    },
    canActivate: [UserRouteAccessService]
};

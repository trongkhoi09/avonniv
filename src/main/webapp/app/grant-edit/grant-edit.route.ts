import {Route} from '@angular/router';

import {GrantEditComponent} from './';

export const GRANT_EDIT_ROUTE: Route = {
    path: 'grant-edit/:grantId',
    component: GrantEditComponent,
    data: {
        authorities: ['ROLE_ADMIN'],
        pageTitle: 'grant.edit'
    }
};

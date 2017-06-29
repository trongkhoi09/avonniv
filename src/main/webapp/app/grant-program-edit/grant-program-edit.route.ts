import {Route} from '@angular/router';

import {GrantProgramEditComponent} from './';

export const GRANT_PROGRAM_EDIT_ROUTE: Route = {
    path: 'grant-program-edit/:grantProgramId',
    component: GrantProgramEditComponent,
    data: {
        authorities: ['ROLE_ADMIN'],
        pageTitle: 'grantProgram.edit'
    }
};

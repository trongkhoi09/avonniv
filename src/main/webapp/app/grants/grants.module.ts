import {NgModule, CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import {RouterModule} from '@angular/router';

import {AvonnivSharedModule} from '../shared';

import {grantsRoute, GrantModalService, GrantsComponent, GrantMgmtDialogComponent} from './';
import {GrantsResolvePagingParams} from './grants.route';
import {GrantDialogComponent} from './grant-dialog.component';

@NgModule({
    imports: [
        AvonnivSharedModule,
        RouterModule.forRoot(grantsRoute, {useHash: true})
    ],
    declarations: [
        GrantsComponent,
        GrantDialogComponent,
        GrantMgmtDialogComponent
    ],
    entryComponents: [
        GrantMgmtDialogComponent
    ],
    providers: [
        GrantsResolvePagingParams,
        GrantModalService
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AvonnivGrantsModule {
}

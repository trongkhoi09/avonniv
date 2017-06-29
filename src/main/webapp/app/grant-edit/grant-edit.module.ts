import {NgModule, CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import {RouterModule} from '@angular/router';

import {AvonnivSharedModule} from '../shared';

import {GRANT_EDIT_ROUTE, GrantEditComponent} from './';

@NgModule({
    imports: [
        AvonnivSharedModule,
        RouterModule.forRoot([GRANT_EDIT_ROUTE], {useHash: true})
    ],
    declarations: [
        GrantEditComponent
    ],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AvonnivGrantEditModule {
}

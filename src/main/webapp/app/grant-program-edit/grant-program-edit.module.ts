import {NgModule, CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import {RouterModule} from '@angular/router';

import {AvonnivSharedModule} from '../shared';

import {GRANT_PROGRAM_EDIT_ROUTE, GrantProgramEditComponent} from './';

@NgModule({
    imports: [
        AvonnivSharedModule,
        RouterModule.forRoot([GRANT_PROGRAM_EDIT_ROUTE], {useHash: true})
    ],
    declarations: [
        GrantProgramEditComponent
    ],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AvonnivGrantProgramEditModule {
}

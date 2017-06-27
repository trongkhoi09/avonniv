import {NgModule, CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import {RouterModule} from '@angular/router';

import {AvonnivSharedModule} from '../shared';

import {grantsRoute, GrantssComponent} from './';
import {GrantssResolvePagingParams} from './grants.route';

@NgModule({
    imports: [
        AvonnivSharedModule,
        RouterModule.forRoot([grantsRoute], {useHash: true})
    ],
    declarations: [
        GrantssComponent,
    ],
    entryComponents: [],
    providers: [
        GrantssResolvePagingParams
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AvonnivGrantssModule {
}

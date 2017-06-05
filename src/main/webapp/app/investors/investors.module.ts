import {NgModule, CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import {RouterModule} from '@angular/router';

import {AvonnivSharedModule} from '../shared';

import {investorsRoute, InvestorsComponent} from './';
import {InvestorsResolvePagingParams} from './investors.route';

@NgModule({
    imports: [
        AvonnivSharedModule,
        RouterModule.forRoot([investorsRoute], {useHash: true})
    ],
    declarations: [
        InvestorsComponent,
    ],
    entryComponents: [],
    providers: [
        InvestorsResolvePagingParams
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AvonnivInvestorsModule {
}

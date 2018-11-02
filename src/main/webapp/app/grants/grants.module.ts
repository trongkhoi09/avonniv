import {NgModule, CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import {RouterModule} from '@angular/router';

import {AvonnivSharedModule} from '../shared';
import {TypeaheadModule} from 'ngx-bootstrap/typeahead';

import {
    grantsRoute,
    OtherPublisherModalService,
    GrantsComponent,
    JhiOtherPublisherModalComponent,
    OtherPublisherDialogComponent,
} from './';
import {GrantsResolvePagingParams} from './grants.route';
import {ListGrantComponent} from './list-grant/list-grant.component';

@NgModule({
    imports: [
        AvonnivSharedModule,
        TypeaheadModule.forRoot(),
        RouterModule.forRoot(grantsRoute, {useHash: true})
    ],
    declarations: [
        GrantsComponent,
        JhiOtherPublisherModalComponent,
        OtherPublisherDialogComponent,
        ListGrantComponent
    ],
    entryComponents: [
        JhiOtherPublisherModalComponent,
    ],
    providers: [
        GrantsResolvePagingParams,
        OtherPublisherModalService,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA],
    exports: [
        ListGrantComponent
    ]
})
export class AvonnivGrantsModule {
}

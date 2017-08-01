import {NgModule, CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import {RouterModule} from '@angular/router';

import {AvonnivSharedModule} from '../shared';

import {
    grantsRoute,
    GrantsComponent,
    OtherPublisherModalService,
    JhiOtherPublisherModalComponent,
    JhiDescriptionGrantModalComponent,
    DescriptionGrantDialogComponent,
    OtherPublisherDialogComponent,
    DescriptionGrantModalService
} from './';
import {GrantsResolvePagingParams} from './grants.route';
import {ListGrantComponent} from './list-grant/list-grant.component';

@NgModule({
    imports: [
        AvonnivSharedModule,
        RouterModule.forRoot(grantsRoute, {useHash: true})
    ],
    declarations: [
        GrantsComponent,
        JhiOtherPublisherModalComponent,
        JhiDescriptionGrantModalComponent,
        OtherPublisherDialogComponent,
        DescriptionGrantDialogComponent,
        ListGrantComponent
    ],
    entryComponents: [
        JhiOtherPublisherModalComponent,
        JhiDescriptionGrantModalComponent
    ],
    providers: [
        GrantsResolvePagingParams,
        OtherPublisherModalService,
        DescriptionGrantModalService
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA],
    exports: [
        ListGrantComponent
    ]
})
export class AvonnivGrantsModule {
}

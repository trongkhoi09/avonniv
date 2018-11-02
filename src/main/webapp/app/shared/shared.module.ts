import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { DatePipe } from '@angular/common';

import { CookieService } from 'angular2-cookie/services/cookies.service';
import {
    AvonnivSharedLibsModule,
    AvonnivSharedCommonModule,
    CSRFService,
    AuthServerProvider,
    AccountService,
    UserService,
    PreferencesService,
    PublisherService,
    StateStorageService,
    LoginService,
    LoginModalService,
    Principal,
    JhiTrackerService,
    HasAnyAuthorityDirective,
    JhiLoginModalComponent,
    GrantService,
    GrantProgramService,
    AreaService
} from './';
import {OauthClientDetailService} from './oauth-client-detail/oauth-client-detail.service';
import {GrantDetailComponent} from './grant-share/grant-detail.component';
import {EllipsisDirective} from './ellipsis/ellipsis.directive';
import {JhiDescriptionGrantModalComponent} from './description-grant/description-grant.component';
import {DescriptionGrantModalService} from './description-grant/description-grant-modal.service';

@NgModule({
    imports: [
        AvonnivSharedLibsModule,
        AvonnivSharedCommonModule
    ],
    declarations: [
        JhiLoginModalComponent,
        HasAnyAuthorityDirective,
        EllipsisDirective,
        GrantDetailComponent,
        JhiDescriptionGrantModalComponent
    ],
    providers: [
        CookieService,
        LoginService,
        LoginModalService,
        AccountService,
        StateStorageService,
        Principal,
        CSRFService,
        JhiTrackerService,
        AuthServerProvider,
        UserService,
        PublisherService,
        DatePipe,
        GrantService,
        PreferencesService,
        GrantProgramService,
        AreaService,
        OauthClientDetailService,
        DescriptionGrantModalService
    ],
    entryComponents: [JhiLoginModalComponent, JhiDescriptionGrantModalComponent],
    exports: [
        AvonnivSharedCommonModule,
        JhiLoginModalComponent,
        HasAnyAuthorityDirective,
        DatePipe,
        GrantDetailComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]

})
export class AvonnivSharedModule {}

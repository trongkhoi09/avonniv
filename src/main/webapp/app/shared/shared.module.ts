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

@NgModule({
    imports: [
        AvonnivSharedLibsModule,
        AvonnivSharedCommonModule
    ],
    declarations: [
        JhiLoginModalComponent,
        HasAnyAuthorityDirective
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
        AreaService
    ],
    entryComponents: [JhiLoginModalComponent],
    exports: [
        AvonnivSharedCommonModule,
        JhiLoginModalComponent,
        HasAnyAuthorityDirective,
        DatePipe
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]

})
export class AvonnivSharedModule {}

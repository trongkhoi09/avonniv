import './vendor.ts';

import { NgModule } from '@angular/core';
import {FormsModule} from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { Ng2Webstorage } from 'ng2-webstorage';

import { AvonnivSharedModule, UserRouteAccessService } from './shared';
import { AvonnivHomeModule } from './home/home.module';
import { AvonnivAdminModule } from './admin/admin.module';
import { AvonnivAccountModule } from './account/account.module';
import { AvonnivEntityModule } from './entities/entity.module';
import { AvonnivGrantsModule } from './grants/grants.module';
import {AvonnivGrantEditModule} from './grant-edit/grant-edit.module';
import {AvonnivGrantProgramEditModule} from './grant-program-edit/grant-program-edit.module';

import { customHttpProvider } from './blocks/interceptor/http.provider';
import { PaginationConfig } from './blocks/config/uib-pagination.config';

import {
    JhiMainComponent,
    LayoutRoutingModule,
    NavbarComponent,
    FooterComponent,
    ProfileService,
    PageRibbonComponent,
    ActiveMenuDirective,
    ErrorComponent,
    NavbarSecondComponent
} from './layouts';

@NgModule({
    imports: [
        BrowserModule,
        LayoutRoutingModule,
        Ng2Webstorage.forRoot({ prefix: 'jhi', separator: '-'}),
        AvonnivSharedModule,
        AvonnivHomeModule,
        AvonnivAdminModule,
        AvonnivAccountModule,
        AvonnivEntityModule,
        AvonnivGrantsModule,
        AvonnivGrantEditModule,
        AvonnivGrantProgramEditModule,
        FormsModule
    ],
    declarations: [
        JhiMainComponent,
        NavbarComponent,
        ErrorComponent,
        PageRibbonComponent,
        ActiveMenuDirective,
        FooterComponent,
        NavbarSecondComponent
    ],
    providers: [
        ProfileService,
        customHttpProvider(),
        PaginationConfig,
        UserRouteAccessService
    ],
    bootstrap: [ JhiMainComponent ]
})
export class AvonnivAppModule {}

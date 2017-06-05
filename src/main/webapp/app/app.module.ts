import './vendor.ts';

import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { Ng2Webstorage } from 'ng2-webstorage';

import { AvonnivSharedModule, UserRouteAccessService } from './shared';
import { AvonnivHomeModule } from './home/home.module';
import { AvonnivAdminModule } from './admin/admin.module';
import { AvonnivAccountModule } from './account/account.module';
import { AvonnivEntityModule } from './entities/entity.module';
import { AvonnivInvestorsModule } from './investors/investors.module';

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
import {InvestorService} from './shared/investor/investor.service';

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
        AvonnivInvestorsModule
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
        UserRouteAccessService,
        InvestorService
    ],
    bootstrap: [ JhiMainComponent ]
})
export class AvonnivAppModule {}

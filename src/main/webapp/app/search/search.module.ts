import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AvonnivSharedModule } from '../shared';

import { SEARCH_ROUTE, SearchComponent } from './';
import {AvonnivGrantsModule} from '../grants/grants.module';

@NgModule({
    imports: [
        AvonnivSharedModule,
        AvonnivGrantsModule,
        RouterModule.forRoot([ SEARCH_ROUTE ], { useHash: true })
    ],
    declarations: [
        SearchComponent,
    ],
    entryComponents: [
    ],
    providers: [
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AvonnivSearchModule {}

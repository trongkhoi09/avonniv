import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import {ProfilesModalService} from './profiles/profiles-modal.service';
import { AvonnivSharedModule } from '../shared';

import {
    Register,
    Activate,
    Password,
    PasswordResetInit,
    PasswordResetFinish,
    PasswordStrengthBarComponent,
    RegisterComponent,
    ActivateComponent,
    PasswordComponent,
    PasswordResetInitComponent,
    PasswordResetFinishComponent,
    SettingsComponent,
    ProfilesComponent,
    PasswordResetModalService,
    accountState
} from './';
import {ConfilmDeleteModalService} from './profiles/confilm-delete-dialog.service';
import {ModalDeleteDialogComponent} from './profiles/confilm-delete-dialog.component';
@NgModule({
    imports: [
        AvonnivSharedModule,
        RouterModule.forRoot(accountState, { useHash: true })
    ],
    declarations: [
        ActivateComponent,
        RegisterComponent,
        PasswordComponent,
        PasswordStrengthBarComponent,
        PasswordResetInitComponent,
        PasswordResetFinishComponent,
        SettingsComponent,
        ProfilesComponent,
        ModalDeleteDialogComponent
    ],
    providers: [
        Register,
        Activate,
        Password,
        PasswordResetInit,
        PasswordResetFinish,
        ProfilesModalService,
        PasswordResetModalService,
        ConfilmDeleteModalService,
    ],
    entryComponents: [
        ProfilesComponent,
        ModalDeleteDialogComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AvonnivAccountModule {}

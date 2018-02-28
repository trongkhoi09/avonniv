import {Component, OnInit} from '@angular/core';
import {JhiLanguageService} from 'ng-jhipster';

import {
    AccountService,
    JhiLanguageHelper,
    PreferencesService,
    Principal,
    PublisherDTO,
    PublisherService,
    User
} from '../../shared';
import {Password} from '../password/password.service';
import {NgbActiveModal, NgbModalRef} from '@ng-bootstrap/ng-bootstrap';
import {ConfilmDeleteModalService} from './confilm-delete-dialog.service';

@Component({
    selector: 'jhi-profiles',
    templateUrl: './profiles.component.html',
    styleUrls: [
        'profiles.scss'
    ]
})
export class ProfilesComponent implements OnInit {
    errorPassword: string;
    errorOldPassword: string;
    successPassword: string;
    errorProfile: string;
    errorNotification: string;
    successProfile: string;
    error: string;
    success: string;
    account: User = new User();
    settingsAccount: any;
    languages: any[];
    publisherCrawled: PublisherDTO[] = [];
    doNotMatch: string;
    password: string;
    oldPassword: string;
    confirmPassword: string;
    authorities: string[];
    collapse = {
        password: true,
        profile: true
    };

    constructor(private accountService: AccountService,
                private passwordService: Password,
                private activeModal: NgbActiveModal,
                private principal: Principal,
                private publisherService: PublisherService,
                private preferencesService: PreferencesService,
                private languageService: JhiLanguageService,
                private languageHelper: JhiLanguageHelper,
                private confimDelete: ConfilmDeleteModalService) {
    }

    ngOnInit() {
        this.principal.identity().then((account) => {
            this.settingsAccount = this.copyAccount(account);
            this.account = account;
            this.authorities = account.authorities;
        });
        this.languageHelper.getAll().then((languages) => {
            this.languages = languages;
        });

        this.publisherService.getAllByCrawled(true).subscribe((publisherCrawled) => {
            this.preferencesService.getAll().subscribe((preferencesDTOs) => {
                for (let i = 0; i < publisherCrawled.length; i++) {
                    for (let j = 0; j < preferencesDTOs.length; j++) {
                        if (publisherCrawled[i].id === preferencesDTOs[j].publisherDTO.id) {
                            publisherCrawled[i].check = preferencesDTOs[j].notification;
                        }
                    }
                }
                this.publisherCrawled = publisherCrawled;
            });
        });
    }

    refreshData(passwordForm) {
        this.settingsAccount = this.copyAccount(this.account);
        passwordForm.reset();
        this.errorOldPassword = null;
        this.errorPassword = null;
        this.errorProfile = null;
        this.successProfile = null;
        this.successPassword = null;
    }

    save() {
        this.accountService.save(this.settingsAccount).subscribe(() => {
            this.errorProfile = null;
            this.successProfile = 'OK';
            this.principal.identity(true).then((account) => {
                this.account = account;
                this.settingsAccount = this.copyAccount(account);
            });
            this.languageService.getCurrent().then((current) => {
                if (this.settingsAccount.langKey !== current) {
                    this.languageService.changeLanguage(this.settingsAccount.langKey);
                }
            });
        }, () => {
            this.collapse.profile = true;
            this.successProfile = null;
            this.errorProfile = 'ERROR';
        });
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    copyAccount(account) {
        return {
            notification: account.notification,
            activated: account.activated,
            email: account.email,
            firstName: account.firstName,
            langKey: account.langKey,
            lastName: account.lastName,
            login: account.login,
            imageUrl: account.imageUrl,
            authorities: account.authorities
        };
    }

    updateNotification() {
        const notification = this.account.notification;
        this.accountService.updateNotification(!notification).subscribe(() => {
            this.account.notification = !notification;
            this.errorNotification = null;
        }, () => {
            this.errorNotification = 'ERROR';
        });
    }

    updateCheckPublisher(index) {
        const publisher = this.publisherCrawled[index];
        this.preferencesService.update(publisher.id, !publisher.check).subscribe(() => {
            this.publisherCrawled[index].check = !publisher.check;
            this.errorNotification = null;
        }, () => {
            this.errorNotification = 'ERROR';
        });
    }

    changePassword(passwordForm) {
        if (this.password !== this.confirmPassword) {
            this.errorPassword = null;
            this.errorOldPassword = null;
            this.successPassword = null;
            this.doNotMatch = 'ERROR';
        } else {
            this.doNotMatch = null;
            this.passwordService.saveNewPassword(this.password, this.oldPassword).subscribe(() => {
                passwordForm.reset();
                this.errorOldPassword = null;
                this.errorPassword = null;
                this.successPassword = 'OK';
            }, (res) => {
                if (res.status === 304) {
                    this.errorOldPassword = 'ERROR';
                    this.errorPassword = null;
                    this.successPassword = null;
                } else {
                    this.successPassword = null;
                    this.errorPassword = 'ERROR';
                    this.errorOldPassword = null;
                }
            });
        }
    }

    openModalConfilm() {
        this.clear();
        this.confimDelete.open();
    }

    checkAdmin() {
        for (let i = 0; i < this.authorities.length ; i ++) {
            if (this.authorities[i] === 'ROLE_ADMIN') {
                return true;
            }
        }
        return false;
    }

}

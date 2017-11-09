import {Component, OnInit} from '@angular/core';
import {JhiLanguageService} from 'ng-jhipster';

import {AccountService, JhiLanguageHelper, Principal, PublisherDTO, PublisherService, User} from '../../shared';
import {Password} from '../password/password.service';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

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
    notification: false;
    collapse = {
        password: true,
        profile: true
    };

    constructor(private accountService: AccountService,
                private passwordService: Password,
                private activeModal: NgbActiveModal,
                private principal: Principal,
                private publisherService: PublisherService,
                private languageService: JhiLanguageService,
                private languageHelper: JhiLanguageHelper) {
    }

    ngOnInit() {
        this.principal.identity().then((account) => {
            this.settingsAccount = this.copyAccount(account);
            this.account = account;
        });
        this.languageHelper.getAll().then((languages) => {
            this.languages = languages;
        });

        this.publisherService.getAllByCrawled(true).subscribe((publisherCrawled) => {
            this.publisherCrawled = publisherCrawled;
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
            activated: account.activated,
            email: account.email,
            firstName: account.firstName,
            langKey: account.langKey,
            lastName: account.lastName,
            login: account.login,
            imageUrl: account.imageUrl
        };
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
}

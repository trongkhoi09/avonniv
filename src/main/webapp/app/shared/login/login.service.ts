import {Injectable} from '@angular/core';
import {JhiLanguageService} from 'ng-jhipster';

import {Principal} from '../auth/principal.service';
import {AuthServerProvider} from '../auth/auth-oauth2.service';
import {JhiTrackerService} from '../tracker/tracker.service';
import {Observable} from 'rxjs/Observable';

@Injectable()
export class LoginService {

    constructor(
        private languageService: JhiLanguageService,
        private principal: Principal,
        private trackerService: JhiTrackerService,
        private authServerProvider: AuthServerProvider
    ) {}

    login(credentials, callback?) {
        const cb = callback || function() {};

        return new Promise((resolve, reject) => {
            this.authServerProvider.login(credentials).then((observable: Observable<any>) => {
                observable.subscribe((data) => {
                    this.principal.identity(true).then((account) => {
                        // After the login the language will be changed to
                        // the language selected by the user during his registration
                        if (account !== null) {
                            if (window.innerWidth < 768) {
                                if (window.navigator.language.indexOf('sv') !== -1) {
                                    this.languageService.changeLanguage('sv');
                                } else {
                                    this.languageService.changeLanguage('en');
                                }
                            }else {
                                this.languageService.changeLanguage(account.langKey);
                            }
                        }
                        this.trackerService.sendActivity();
                        resolve(data);
                    });
                    return cb();
                }, (err) => {
                    this.logout();
                    reject(err);
                    return cb(err);
                });
            });
        });
    }

    loginByRefreshToken(refresh_token, callback?) {
        const cb = callback || function() {};

        return new Promise((resolve, reject) => {
            this.authServerProvider.loginByRefreshToken(refresh_token).then((observable: Observable<any>) => {
                observable.subscribe((data) => {
                    this.principal.identity(true).then((account) => {
                        // After the login the language will be changed to
                        // the language selected by the user during his registration
                        if (account !== null) {
                            if (window.innerWidth < 768) {
                                if (window.navigator.language.indexOf('sv') !== -1) {
                                    this.languageService.changeLanguage('sv');
                                } else {
                                    this.languageService.changeLanguage('en');
                                }
                            } else {
                                this.languageService.changeLanguage(account.langKey);
                            }
                        }
                        this.trackerService.sendActivity();
                        resolve(data);
                    });
                    return cb();
                }, (err) => {
                    this.logout();
                    reject(err);
                    return cb(err);
                });
            });
        });
    }

    logout() {
        this.authServerProvider.logout().subscribe();
        this.principal.authenticate(null);
    }
}

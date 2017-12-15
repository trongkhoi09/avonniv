import {EventManager, HttpInterceptor} from 'ng-jhipster';
import { RequestOptionsArgs, Response } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import { Injector } from '@angular/core';
import { LoginService } from '../../shared';
import {LocalStorageService, SessionStorageService} from 'ng2-webstorage';
import { TranslateService } from 'ng2-translate/ng2-translate';

export class AuthExpiredInterceptor extends HttpInterceptor {

    constructor(private injector: Injector) {
        super();
    }

    requestIntercept(options?: RequestOptionsArgs): RequestOptionsArgs {
        return options;
    }

    responseIntercept(observable: Observable<Response>): Observable<Response> {
        return <Observable<Response>> observable.catch((error, source) => {
            if (error.status === 401) {
                const loginService: LoginService = this.injector.get(LoginService);
                const localStorage: LocalStorageService = this.injector.get(LocalStorageService);
                const sessionStorage: SessionStorageService = this.injector.get(SessionStorageService);
                const token = localStorage.retrieve('authenticationToken') || sessionStorage.retrieve('authenticationToken');
                if (token && token.remember_me && token.refresh_token) {
                    token.remember_me = false;
                    localStorage.store('authenticationToken', token);
                    loginService.loginByRefreshToken(token.refresh_token).then(() => {
                        const translateService: TranslateService = this.injector.get(TranslateService);
                        translateService.get('global.messages.error.sessionTimeout').subscribe((messages) => {
                            alert(messages);
                            location.reload();
                        });
                    }).catch(() => {
                        loginService.logout();
                    });
                }else {
                    loginService.logout();
                }
            }
            return Observable.throw(error);
        });
    }
}

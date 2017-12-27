import { Injectable, Inject } from '@angular/core';
import { Http, Response, Headers } from '@angular/http';
import { Observable } from 'rxjs/Rx';
import { LocalStorageService } from 'ng2-webstorage';

import {Base64, EventManager} from 'ng-jhipster';

@Injectable()
export class AuthServerProvider {

    constructor(
        private eventManager: EventManager,
        private http: Http,
        private base64: Base64,
        private $localStorage: LocalStorageService
    ) {}

    getToken() {
        return this.$localStorage.retrieve('authenticationToken');
    }

    login(credentials): Observable<any> {
        const data = 'username=' +  encodeURIComponent(credentials.username) + '&password=' +
            encodeURIComponent(credentials.password) + '&grant_type=password&scope=read%20write&' +
            'client_secret=my-secret-token-to-change-in-production&client_id=Avonnivapp';
        const headers = new Headers ({
            'Content-Type': 'application/x-www-form-urlencoded',
            'Accept': 'application/json',
            'Authorization': 'Basic ' + this.base64.encode('Avonnivapp' + ':' + 'my-secret-token-to-change-in-production')
        });

        return this.http.post('oauth/token', data, {
            headers
        }).map(authSuccess.bind(this));

        function authSuccess(resp) {
            const response = resp.json();
            const expiredAt = new Date();
            expiredAt.setSeconds(expiredAt.getSeconds() + response.expires_in);
            response.expires_at = expiredAt.getTime();
            response.remember_me = credentials.rememberMe;
            this.$localStorage.store('authenticationToken', response);
            return response;
        }
    }

    loginByRefreshToken(refresh_token): Observable<any> {
        const data = 'refresh_token=' +  encodeURIComponent(refresh_token) +
            '&grant_type=refresh_token&scope=read%20write&' +
            'client_secret=my-secret-token-to-change-in-production&client_id=Avonnivapp';
        const headers = new Headers ({
            'Content-Type': 'application/x-www-form-urlencoded',
            'Accept': 'application/json',
            'Authorization': 'Basic ' + this.base64.encode('Avonnivapp' + ':' + 'my-secret-token-to-change-in-production')
        });

        return this.http.post('oauth/token', data, {
            headers
        }).map(authSuccess.bind(this));

        function authSuccess(resp) {
            const response = resp.json();
            const expiredAt = new Date();
            expiredAt.setSeconds(expiredAt.getSeconds() + response.expires_in);
            response.expires_at = expiredAt.getTime();
            response.remember_me = true;
            this.$localStorage.store('authenticationToken', response);
            this.eventManager.broadcast({
                name: 'authenticationSuccess',
                content: 'Sending Authentication Success'
            });
            return response;
        }
    }

    logout(): Observable<any> {
        return new Observable((observer) => {
            this.http.post('api/logout', {});
            this.$localStorage.clear('authenticationToken');
            observer.complete();
        });
    }
}

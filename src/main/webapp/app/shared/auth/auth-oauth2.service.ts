import {Injectable} from '@angular/core';
import {Headers, Http} from '@angular/http';
import {Observable} from 'rxjs/Rx';
import {LocalStorageService} from 'ng2-webstorage';

import {Base64, EventManager} from 'ng-jhipster';
import {OauthClientDetailService} from '../oauth-client-detail/oauth-client-detail.service';

@Injectable()
export class AuthServerProvider {

    private clientId: string;
    private secret: string;

    constructor(private eventManager: EventManager,
                private http: Http,
                private base64: Base64,
                private $localStorage: LocalStorageService,
                private oauth: OauthClientDetailService) {
    }

    handleClientId(res) {
        let loop = true;
        res.forEach((auth) => {
            if (loop) {
                const additionalInfomation = JSON.parse(auth.additionalInformation);
                if (additionalInfomation['web'] === true) {
                    this.clientId = (auth.clientId);
                    this.secret = (auth.clientSecret);
                    loop = false;
                }
            }
        });

        if (loop) {
            this.clientId = (res[0].clientId);
            this.secret = (res[0].clientSecret);
        }
    }

    getToken() {
        return this.$localStorage.retrieve('authenticationToken');
    }

    login(credentials) {
        return new Promise((resolve, reject) => {
            if (this.clientId == null || this.secret == null) {
                this.oauth.findAll().subscribe(
                    (res) => {
                        this.handleClientId(res);
                        resolve(this.loginFunction(credentials));
                    });
            } else {
                resolve(this.loginFunction(credentials));
            }
        });
    }

    loginFunction(credentials) {
        console.log('ClientId: ' + this.clientId);
        console.log('Secret: ' + this.secret);
        const data = 'username=' + encodeURIComponent(credentials.username) + '&password=' +
            encodeURIComponent(credentials.password) + '&grant_type=password&scope=read%20write&' +
            `client_secret=${encodeURIComponent(this.secret)}&client_id=${encodeURIComponent(this.clientId)}`;
        const headers = new Headers({
            'Content-Type': 'application/x-www-form-urlencoded',
            'Accept': 'application/json',
            'Authorization': 'Basic ' + this.base64.encode(`${this.clientId}` + ':' + `${this.secret}`)
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

    loginByRefreshToken(refresh_token) {
        return new Promise((resolve, reject) => {
            if (this.clientId == null || this.secret == null) {
                this.oauth.findAll().subscribe(
                    (res) => {
                        this.handleClientId(res);
                        resolve(this.handleTokenFunction(refresh_token));
                    });
            } else {
                resolve(this.handleTokenFunction(refresh_token));
            }
        });
    }

    handleTokenFunction(refresh_token) {
        const data = 'refresh_token=' + encodeURIComponent(refresh_token) +
            '&grant_type=refresh_token&scope=read%20write&' +
            `client_secret=${encodeURIComponent(this.secret)}&client_id=${encodeURIComponent(this.clientId)}`;
        const headers = new Headers({
            'Content-Type': 'application/x-www-form-urlencoded',
            'Accept': 'application/json',
            'Authorization': 'Basic ' + this.base64.encode(`${this.clientId}` + ':' + `${this.secret}`)
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

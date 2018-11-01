import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { Observable } from 'rxjs/Rx';

@Injectable()
export class Register {

    constructor(private http: Http) {}

    save(account: any): Observable<any> {
        return this.http.post('api/register', account);
    }

    reset(login: string): Observable<any> {
        return this.http.get('api/register/resubmit?login=' + login);
    }
}

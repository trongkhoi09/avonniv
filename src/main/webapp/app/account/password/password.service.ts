import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { Observable } from 'rxjs/Rx';

@Injectable()
export class Password {

    constructor(private http: Http) {}

    save(newPassword: string): Observable<any> {
        return this.http.post('api/account/change_password', newPassword);
    }

    saveNewPassword(newPassword: string, oldPassword: string): Observable<any> {
        const options = {
            newPassword: '',
            oldPassword: ''
        };
        options.newPassword = newPassword;
        options.oldPassword = oldPassword;
        return this.http.post('api/account/change_new_password', options);
    }
}

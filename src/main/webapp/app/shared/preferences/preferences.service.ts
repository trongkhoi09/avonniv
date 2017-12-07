import {Injectable} from '@angular/core';
import {Http, Response} from '@angular/http';
import {Observable} from 'rxjs/Rx';

import {PreferencesDTO} from './preferences.model';
import {ResponseWrapper} from '../model/response-wrapper.model';

@Injectable()
export class PreferencesService {
    private resourceUrl = 'api/preferences';

    constructor(private http: Http) {
    }

    getAll(): Observable<PreferencesDTO[]> {
        return this.http.get(this.resourceUrl)
            .map((res: Response) => res.json());
    }

    update(publisherId: number, notification: Boolean): Observable<Response> {
        return this.http.post(`${this.resourceUrl}?publisherId=${publisherId}&notification=${notification}`, {});
    }
}

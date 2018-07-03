import {Injectable} from '@angular/core';
import {Observable} from '../../../../../../node_modules/rxjs';
import {ResponseWrapper} from '../model/response-wrapper.model';
import {Http, Response} from '@angular/http';

@Injectable()
export class OauthClientDetailService {
    private resourceUrl = 'api/users/oauth';

    constructor(private http: Http) { }

    findAll(): Observable<ResponseWrapper> {
        return this.http.get(this.resourceUrl)
            .map((res: Response) => res.json());
    }

}

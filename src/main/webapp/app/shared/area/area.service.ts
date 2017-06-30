import {Injectable} from '@angular/core';
import {Http, Response} from '@angular/http';
import {Observable} from 'rxjs/Rx';

import {AreaDTO} from './area.model';

@Injectable()
export class AreaService {
    private resourceUrl = 'api/area';

    constructor(private http: Http) {
    }

    getAll(): Observable<AreaDTO[]> {
        return this.http.get(this.resourceUrl)
            .map((res: Response) => res.json());
    }
}

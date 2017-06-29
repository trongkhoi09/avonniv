import {Injectable} from '@angular/core';
import {Http, Response} from '@angular/http';
import {Observable} from 'rxjs/Rx';

import {GrantProgramDTO} from './grant-program.model';
import {ResponseWrapper} from '../model/response-wrapper.model';
import {createRequestOption} from '../model/request-util';

@Injectable()
export class GrantProgramService {
    private resourceUrl = 'api/grantProgram';

    constructor(private http: Http) {
    }

    query(req?: any): Observable<ResponseWrapper> {
        const options = createRequestOption(req);
        return this.http.get(this.resourceUrl, options)
            .map((res: Response) => this.convertResponse(res));
    }

    private convertResponse(res: Response): ResponseWrapper {
        const jsonResponse = res.json();
        return new ResponseWrapper(res.headers, jsonResponse, res.status);
    }

    find(grantProgramId: string): Observable<GrantProgramDTO> {
        return this.http.get(`${this.resourceUrl}/${grantProgramId}`).map((res: Response) => res.json());
    }

    update(grantProgram: GrantProgramDTO): Observable<ResponseWrapper> {
        return this.http.put(this.resourceUrl, grantProgram)
            .map((res: Response) => this.convertResponse(res));
    }
}

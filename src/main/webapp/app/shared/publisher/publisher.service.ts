import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable } from 'rxjs/Rx';

import { PublisherDTO } from './publisher.model';
import { ResponseWrapper } from '../model/response-wrapper.model';
import { createRequestOption } from '../model/request-util';

@Injectable()
export class PublisherService {
    private resourceUrl = 'api/publishers';

    constructor(private http: Http) { }

    create(publisher: PublisherDTO): Observable<ResponseWrapper> {
        return this.http.post(this.resourceUrl, publisher)
            .map((res: Response) => this.convertResponse(res));
    }

    update(publisher: PublisherDTO): Observable<ResponseWrapper> {
        return this.http.put(this.resourceUrl, publisher)
            .map((res: Response) => this.convertResponse(res));
    }

    find(name: string): Observable<PublisherDTO> {
        return this.http.get(`${this.resourceUrl}/${name}`).map((res: Response) => res.json());
    }

    getAllByCrawled(crawled: boolean): Observable<PublisherDTO[]> {
        return this.http.get(`${this.resourceUrl}/all?crawled=${crawled}`).map((res: Response) => res.json());
    }

    query(req?: any): Observable<ResponseWrapper> {
        const options = createRequestOption(req);
        return this.http.get(this.resourceUrl, options)
            .map((res: Response) => this.convertResponse(res));
    }

    delete(name: string): Observable<Response> {
        return this.http.delete(`${this.resourceUrl}/${name}`);
    }

    private convertResponse(res: Response): ResponseWrapper {
        const jsonResponse = res.json();
        return new ResponseWrapper(res.headers, jsonResponse, res.status);
    }
}

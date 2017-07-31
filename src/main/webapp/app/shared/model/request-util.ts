import { URLSearchParams, BaseRequestOptions } from '@angular/http';

export const createRequestOption = (req?: any): BaseRequestOptions => {
    const options: BaseRequestOptions = new BaseRequestOptions();
    if (req) {
        const params: URLSearchParams = new URLSearchParams();
        params.set('page', req.page);
        params.set('size', req.size);
        if (req.sort) {
            params.paramsMap.set('sort', req.sort);
        }
        params.set('query', req.query);

        options.search = params;
    }
    return options;
};

export const createRequestGrantOption = (req?: any): BaseRequestOptions => {
    const options: BaseRequestOptions = new BaseRequestOptions();
    if (req) {
        const params: URLSearchParams = new URLSearchParams();
        params.set('page', req.page);
        params.set('size', req.size);
        if (req.search) {
            params.set('search', req.search);
        }
        params.set('openGrant', req.openGrant);
        params.set('comingGrant', req.comingGrant);
        if (req.areaDTOs) {
            params.set('areaDTOs', req.areaDTOs);
        }
        if (req.publisherDTOs) {
            params.set('publisherDTOs', req.publisherDTOs);
        }
        if (req.sort) {
            params.paramsMap.set('sort', req.sort);
        }
        params.set('query', req.query);

        options.search = params;
    }
    return options;
};

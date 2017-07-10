import {Component, OnInit, OnDestroy} from '@angular/core';
import {Response} from '@angular/http';
import {ActivatedRoute, Router} from '@angular/router';
import {EventManager, PaginationUtil, ParseLinks, AlertService} from 'ng-jhipster';

import {ITEMS_PER_PAGE, Principal, PublisherDTO, PublisherService, ResponseWrapper} from '../../shared';
import {PaginationConfig} from '../../blocks/config/uib-pagination.config';

@Component({
    selector: 'jhi-publisher-mgmt',
    templateUrl: './publisher-management.component.html'
})
export class PublisherMgmtComponent implements OnInit, OnDestroy {

    publishers: PublisherDTO[];
    error: any;
    success: any;
    routeData: any;
    links: any;
    totalItems: any;
    queryCount: any;
    itemsPerPage: any;
    page: any;
    predicate: any;
    previousPage: any;
    reverse: any;

    constructor(private publisherService: PublisherService,
                private parseLinks: ParseLinks,
                private alertService: AlertService,
                private principal: Principal,
                private eventManager: EventManager,
                private paginationUtil: PaginationUtil,
                private paginationConfig: PaginationConfig,
                private activatedRoute: ActivatedRoute,
                private router: Router) {
        this.itemsPerPage = ITEMS_PER_PAGE;
        this.routeData = this.activatedRoute.data.subscribe((data) => {
            this.page = data['pagingParams'].page;
            this.previousPage = data['pagingParams'].page;
            this.reverse = data['pagingParams'].ascending;
            this.predicate = data['pagingParams'].predicate;
        });
    }

    ngOnInit() {
        this.principal.identity().then((account) => {
            this.loadAll();
            this.registerChangeInPublishers();
        });
    }

    ngOnDestroy() {
        this.routeData.unsubscribe();
    }

    registerChangeInPublishers() {
        this.eventManager.subscribe('publisherListModification', (response) => this.loadAll());
    }

    setCrawled(publisher, isActivated) {
        publisher.crawled = isActivated;

        this.publisherService.update(publisher).subscribe(
            (response) => {
                if (response.status === 200) {
                    this.error = null;
                    this.success = 'OK';
                    this.loadAll();
                } else {
                    this.success = null;
                    this.error = 'ERROR';
                }
            });
    }

    loadAll() {
        this.publisherService.query({
            page: this.page - 1,
            size: this.itemsPerPage,
            sort: this.sort()
        }).subscribe(
            (res: ResponseWrapper) => this.onSuccess(res.json, res.headers),
            (res: ResponseWrapper) => this.onError(res.json)
        );
    }

    sort() {
        const result = [this.predicate + ',' + (this.reverse ? 'asc' : 'desc')];
        if (this.predicate !== 'id') {
            result.push('id');
        }
        return result;
    }

    loadPage(page: number) {
        if (page !== this.previousPage) {
            this.previousPage = page;
            this.transition();
        }
    }

    transition() {
        this.router.navigate(['/publisher-management'], {
            queryParams: {
                page: this.page,
                sort: this.predicate + ',' + (this.reverse ? 'asc' : 'desc')
            }
        });
        this.loadAll();
    }

    private onSuccess(data, headers) {
        this.links = this.parseLinks.parse(headers.get('link'));
        this.totalItems = headers.get('X-Total-Count');
        this.queryCount = this.totalItems;
        this.publishers = data;
    }

    private onError(error) {
        this.alertService.error(error.error, error.message, null);
    }
}

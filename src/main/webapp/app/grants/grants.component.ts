import {Component, OnDestroy, OnInit} from '@angular/core';
import {AlertService, ParseLinks} from 'ng-jhipster';
import {ResponseWrapper} from '../shared/model/response-wrapper.model';
import {ActivatedRoute, Router} from '@angular/router';
import {GrantService} from '../shared/grant/grant.service';
import {ITEMS_PER_PAGE} from '../shared/constants/pagination.constants';
import {Principal, AreaDTO, GrantDTO, PublisherDTO, PublisherService, AreaService} from '../shared';
import {Observable} from 'rxjs/Observable';
@Component({
    selector: 'jhi-grants',
    templateUrl: './grants.component.html',
    styleUrls: [
        'grants.scss'
    ]
})
export class GrantsComponent implements OnInit, OnDestroy {
    routeData: any;
    links: any;
    totalItems: any;
    itemsPerPage: any;
    page: any;
    predicate: any;
    previousPage: any;
    reverse: any;
    currentAccount: any;
    area: AreaDTO;
    grantDTOs: GrantDTO[];
    publisherCrawled: PublisherDTO[] = [];
    publisherNotCrawled: PublisherDTO[] = [];
    areaDTOs: AreaDTO[] = [];
    grantFilter = {
        publicGrant: true,
        privateGrant: false,
        areaDTOs: [],
        publisherDTOs: []
    };

    constructor(private alertService: AlertService,
                private parseLinks: ParseLinks,
                private grantService: GrantService,
                private areaService: AreaService,
                private publisherService: PublisherService,
                private principal: Principal,
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
            this.currentAccount = account;
        });
        this.loadAll();
        this.loadArea();
        this.loadPublisher();
    }

    ngOnDestroy() {
        this.routeData.unsubscribe();
    }

    onFiltering() {
    }

    isAdmin() {
        return this.principal.hasAnyAuthorityDirect(['ROLE_ADMIN']);
    }

    loadAll() {
        this.grantService.query({
            page: this.page - 1,
            size: this.itemsPerPage,
            sort: this.sort()
        }).subscribe(
            (res: ResponseWrapper) => this.onSuccess(res.json, res.headers),
            (res: ResponseWrapper) => this.onError(res.json)
        );
    }

    inputFormatter = (x: { name: string }) => x.name;

    resultFormatter = (result: AreaDTO) => result.name.toUpperCase();

    searchArea = (text$: Observable<string>) =>
        text$
            .debounceTime(200)
            .map((term) => term === '' ? []
                : this.areaDTOs.filter((v) => v.name.toLowerCase().indexOf(term.toLowerCase()) > -1
                && this.grantFilter.areaDTOs.indexOf(v) === -1).slice(0, 10));

    loadArea() {
        this.areaService.getAll().subscribe((areaDTOs) => this.areaDTOs = areaDTOs);
    }

    loadPublisher() {
        this.publisherService.getAllByCrawled(true).subscribe((publisherCrawled) => {
            this.publisherCrawled = publisherCrawled;
            for (let i = 0; i < publisherCrawled.length; i++) {
                publisherCrawled[i].check = true;
            }
        });
        this.publisherService.getAllByCrawled(false).subscribe((publisherNotCrawled) => this.publisherNotCrawled = publisherNotCrawled);
    }

    onChangeArea() {
        if (this.area && this.area.name) {
            if (this.grantFilter.areaDTOs.indexOf(this.area) === -1) {
                this.grantFilter.areaDTOs.unshift(this.area);
            }
            this.area = null;
        }
    }

    removeArea(area: AreaDTO) {
        const index: number = this.grantFilter.areaDTOs.indexOf(area);
        if (index !== -1) {
            this.grantFilter.areaDTOs.splice(index, 1);
        }
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
        this.router.navigate(['/grants'], {
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
        this.grantDTOs = data;
    }

    private onError(error) {
        this.alertService.error(error.error, error.message, null);
    }
}

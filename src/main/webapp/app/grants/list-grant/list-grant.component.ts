import {Component, Input, OnChanges, OnDestroy, OnInit, SimpleChanges} from '@angular/core';
import {AlertService, ParseLinks, PaginationUtil} from 'ng-jhipster';
import {ResponseWrapper} from '../../shared/model/response-wrapper.model';
import {ActivatedRoute, Router} from '@angular/router';
import {GrantService} from '../../shared/grant/grant.service';
import {ITEMS_PER_PAGE} from '../../shared/constants/pagination.constants';
import {Principal, GrantDTO} from '../../shared';

@Component({
    selector: 'jhi-list-grant',
    templateUrl: './list-grant.component.html',
    styleUrls: [
        'list-grant.scss'
    ]
})
export class ListGrantComponent implements OnInit, OnDestroy, OnChanges {

    @Input() data: any = {
        navigate: '/grants',
        search: ''
    };

    routeData: any;
    links: any;
    totalItems: any;
    itemsPerPage: any;
    page: any;
    previousPage: any;
    predicate: any;
    reverse: any;
    grantDTOs: GrantDTO[];

    grantFilter = {
        page: 0,
        size: ITEMS_PER_PAGE,
        sort: this.sort(),
        search: '',
        openGrant: true,
        comingGrant: true,
        areaDTOs: [],
        publisherDTOs: []
    };

    constructor(private alertService: AlertService,
                private parseLinks: ParseLinks,
                private grantService: GrantService,
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
        this.page = 0;
        this.grantFilter.search = this.data.search;
        this.grantFilter.comingGrant = this.data.comingGrant;
        this.grantFilter.openGrant = this.data.openGrant;
        this.grantFilter.areaDTOs = this.data.areaDTOs ? this.data.areaDTOs : [];
        this.grantFilter.publisherDTOs = this.data.publisherDTOs ? this.data.publisherDTOs : [];
        this.loadAll();
    }

    ngOnDestroy() {
        this.routeData.unsubscribe();
    }

    ngOnChanges(changes: SimpleChanges): void {
        this.ngOnInit();
    }

    isAdmin() {
        return this.principal.hasAnyAuthorityDirect(['ROLE_ADMIN']);
    }

    loadAll() {
        this.grantFilter.page = this.page - 1;
        this.grantFilter.size = this.itemsPerPage;
        this.grantFilter.sort = this.sort();
        this.grantService.query(this.grantFilter).subscribe(
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
        this.router.navigate([this.data.search ? this.data.navigate + '/' + this.data.search : this.data.navigate], {
            queryParams: {
                comingGrant: this.grantFilter.comingGrant,
                openGrant: this.grantFilter.openGrant,
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

    sliceDescription(grantDTO) {
        const description = grantDTO.description;
        if (!description) {
            return 'Not available';
        }
        let index = 200;
        if (description != null && description.length > index) {
            while (description[index] !== ' ') {
                index--;
            }
            return description.substring(0, index);
        }
        return description;
    }
    isSliceDescription(grantDTO) {
        const description = grantDTO.description;
        const index = 200;
        return (description != null && description.length > index);
    }
}

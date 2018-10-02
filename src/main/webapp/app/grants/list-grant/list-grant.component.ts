import {
    Component,
    EventEmitter,
    HostListener,
    Input,
    OnChanges,
    OnDestroy,
    OnInit,
    Output,
    SimpleChanges
} from '@angular/core';
import {AlertService, EventManager, JhiLanguageService, ParseLinks} from 'ng-jhipster';
import {ResponseWrapper} from '../../shared/model/response-wrapper.model';
import {ActivatedRoute, Router} from '@angular/router';
import {GrantService} from '../../shared/grant/grant.service';
import {ITEMS_PER_PAGE} from '../../shared/constants/pagination.constants';
import {GrantDTO, Principal} from '../../shared';
import {sliceString} from '../grants.constance';

declare const ga: Function;

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
    @Output() fnCallBack = new EventEmitter();
    @Input() viewList = true;

    viewListOld = this.viewList;
    routeData: any;
    links: any;
    totalItems: any;
    itemsPerPage: any;
    page: number;
    previousPage: any;
    predicate: any;
    reverse: any;
    grantDTOs: GrantDTO[];
    listPage = [];
    maxSize = 7;
    formatDate = 'MM/dd/yyyy';

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
    oldGrantFilter = {};

    constructor(private eventManager: EventManager,
                private alertService: AlertService,
                private parseLinks: ParseLinks,
                private grantService: GrantService,
                private principal: Principal,
                private activatedRoute: ActivatedRoute,
                private languageService: JhiLanguageService,
                private router: Router) {
        this.itemsPerPage = ITEMS_PER_PAGE;
    }

    ngOnInit() {
        this.languageService.getCurrent().then((current) => {
            if (current === 'en') {
                this.formatDate = 'MM/dd/yyyy';
            } else if (current === 'sv') {
                this.formatDate = 'yyyy/MM/dd';
            }
        });
        this.eventManager.subscribe('changeLanguage', (response) => {
            if (response.content === 'en') {
                this.formatDate = 'MM/dd/yyyy';
            } else if (response.content === 'sv') {
                this.formatDate = 'yyyy/MM/dd';
            }
        });
        this.page = 0;
        this.listPage = [];
        if (this.data.itemsPerPage) {
            this.itemsPerPage = this.data.itemsPerPage;
        }
        this.grantFilter.search = this.data.search;
        this.grantFilter.comingGrant = this.data.comingGrant;
        this.grantFilter.openGrant = this.data.openGrant;
        this.grantFilter.areaDTOs = this.data.areaDTOs ? this.data.areaDTOs : [];
        this.grantFilter.publisherDTOs = this.data.publisherDTOs ? this.data.publisherDTOs : [];
        this.routeData = this.activatedRoute.data.subscribe((data) => {
            this.page = data['pagingParams'].page;
            this.previousPage = data['pagingParams'].page;
            this.reverse = data['pagingParams'].ascending;
            this.predicate = data['pagingParams'].predicate;
            this.loadAll();
        });
    }

    ngOnDestroy() {
        this.routeData.unsubscribe();
    }

    ngOnChanges(changes: SimpleChanges): void {
        this.checkIsMobile();
        if (this.viewList === this.viewListOld) {
            this.ngOnInit();
        } else {
            this.viewListOld = this.viewList;
        }
    }

    isAdmin() {
        return this.principal.hasAnyAuthorityDirect(['ROLE_ADMIN']);
    }

    loadAll() {
        this.grantFilter.page = this.page - 1;
        this.grantFilter.size = this.itemsPerPage;
        this.grantFilter.sort = this.sort();
        if (JSON.stringify(this.grantFilter) !== JSON.stringify(this.oldGrantFilter)) {
            this.grantService.query(this.grantFilter).subscribe(
                (res: ResponseWrapper) => this.onSuccess(res.json, res.headers),
                (res: ResponseWrapper) => this.onError(res.json)
            );
        }
    }

    filterAll(search) {
        console.log('data', this.data);
        this.grantFilter.search = search;
        this.grantFilter.page = this.page - 1;
        this.grantFilter.size = this.itemsPerPage;
        this.grantFilter.sort = this.sort();
        console.log('grantFilter', this.grantFilter);
        if (JSON.stringify(this.grantFilter) !== JSON.stringify(this.oldGrantFilter)) {
            this.grantService.querySearch(this.grantFilter).subscribe(
                (res: ResponseWrapper) => this.onSuccess(res.json, res.headers),
                (res: ResponseWrapper) => this.onError(res.json)
            );
        }
    }

    sort() {
        const result = [this.predicate + ',' + (this.reverse ? 'asc' : 'desc')];
        if (this.predicate !== 'createdDate') {
            result.push('createdDate');
        }
        return result;
    }

    showDescription(grantDTO) {
        this.router.navigate(['/', {outlets: {popup: 'description-grant/' + grantDTO.id}}]);
        ga('send', 'event', 'grantlist', 'open dialog ' + grantDTO.title);
    }

    loadPage(page: number) {
        if (page !== this.previousPage) {
            this.page = page;
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
        this.oldGrantFilter = Object.assign({}, this.grantFilter);
        this.links = this.parseLinks.parse(headers.get('link'));
        this.totalItems = headers.get('X-Total-Count');
        this.fnCallBack.emit(this.totalItems);
        this.grantDTOs = data;
        this.listPage = [];
        const totalPage = Math.ceil(parseInt(this.totalItems, 10) / parseInt(this.itemsPerPage, 10));
        if (totalPage > 0) {
            if (this.page === 0) {
                this.page = 1;
            }
            for (let i = this.page - 3; i <= this.page + 3; i++) {
                if (i > 0 && i <= totalPage) {
                    this.listPage.push(i);
                }
            }
            let count = 0;
            while (true) {
                if (this.listPage.length === 7 || this.listPage.length === totalPage) {
                    break;
                }
                if (this.listPage[0] > 1) {
                    this.listPage.unshift(this.listPage[0] - 1);
                }
                if (this.listPage[this.listPage.length - 1] < totalPage && this.listPage.length < 7) {
                    this.listPage.push(this.listPage[this.listPage.length - 1] + 1);
                }
                count++;
                if (count > 50) {
                    break;
                }
            }
            if (this.listPage[0] > 2) {
                this.listPage.unshift('...');
                this.listPage.unshift(1);
            } else if (this.listPage[0] > 1) {
                this.listPage.unshift(1);
            }
            if (this.listPage[this.listPage.length - 1] < totalPage - 1) {
                this.listPage.push('...');
                this.listPage.push(totalPage);
            } else if (this.listPage[this.listPage.length - 1] < totalPage) {
                this.listPage.push(totalPage);
            }
        }
        window.scrollTo(0, 0);
    }

    private onError(error) {
        this.alertService.error(error.error, error.message, null);
    }

    sliceDescription(grantDTO) {
        const description = grantDTO.description;
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

    sliceText(text: string, fontSize: number, numberLine: number, width: number) {
       return sliceString(text, fontSize, numberLine, width)[0];
    }

    getNumberLineText(text: string, fontSize: number, numberLine: number, width: number) {
        return sliceString(text, fontSize, numberLine, width)[1];
    }

    getFinanceDescription(grantDTO) {
        if (grantDTO.grantProgramDTO.publisherDTO.id !== 9) {
            return grantDTO.financeDescription;
        } else {
            if (isNaN(grantDTO.financeDescription)) {
                return grantDTO.financeDescription;
            } else {
                return Number(Number(grantDTO.financeDescription).toFixed(1)).toLocaleString() + ' EUR';
            }
        }
    }

    checkIsMobile(resize = false) {
        if (window.innerWidth < 992) {
            this.viewList = true;
        } else {
            if (resize) {
                this.viewList = this.viewListOld;
            }

        }
    }

    @HostListener('window:resize', ['$event'])
    onResize(event) {
        this.checkIsMobile(true);
    }
}

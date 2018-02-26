import {Component, OnDestroy, OnInit, Renderer2} from '@angular/core';
import {
    AreaDTO, AreaService, ITEMS_PER_PAGE, PreferencesService, Principal, PublisherDTO,
    PublisherService
} from '../shared';
import {Observable} from 'rxjs/Observable';
import {ActivatedRoute} from '@angular/router';
import {EventManager} from 'ng-jhipster';
import {Subscription} from 'rxjs/Rx';

@Component({
    selector: 'jhi-grants',
    templateUrl: './grants.component.html',
    styleUrls: [
        'grants.scss'
    ]
})
export class GrantsComponent implements OnInit, OnDestroy {
    routeSub: any;
    currentAccount: any;
    numberItem = 0;
    loadPublisherFinished = false;
    area: AreaDTO;
    publisherCrawled: PublisherDTO[] = [];
    publisherNotCrawled: PublisherDTO[] = [];
    areaDTOs: AreaDTO[] = [];
    grantFilter = {
        navigate: '/grants',
        search: '',
        openGrant: true,
        comingGrant: true,
        areaDTOs: [],
        publisherDTOs: [],
        itemsPerPage: ITEMS_PER_PAGE
    };
    data = this.grantFilter;
    authenticationSuccess: Subscription;

    constructor(private areaService: AreaService,
                private eventManager: EventManager,
                private route: ActivatedRoute,
                private publisherService: PublisherService,
                private preferencesService: PreferencesService,
                private principal: Principal,
                private ref: Renderer2) {
    }

    ngOnInit() {
        this.setActivateClicking();
        this.principal.setGrantpage(true);
        this.routeSub = this.route.queryParams.subscribe((params) => {
            this.grantFilter.openGrant = !('false' === params['openGrant']);
            this.grantFilter.comingGrant = !('false' === params['comingGrant']);
        });
        this.principal.identity().then((account) => {
            this.currentAccount = account;
            if (account) {
                this.loadPublisher(true);
            } else {
                this.loadPublisher(false);
            }
        });
        this.loadArea();
        this.authenticationSuccess = this.eventManager.subscribe('authenticationSuccess', (message) => {
            this.loadPublisher(true);
        });
    }

    ngOnDestroy(): void {
        this.principal.setGrantpage(false);
        this.routeSub.unsubscribe();
        if (this.authenticationSuccess !== undefined && this.authenticationSuccess !== null) {
            this.eventManager.destroy(this.authenticationSuccess);
        }
    }

    handleUpdated(number) {
        this.numberItem = number;
    }

    onFiltering() {
        const publisherDTOs = [];
        for (let i = 0; i < this.publisherCrawled.length; i++) {
            if (this.publisherCrawled[i].check) {
                publisherDTOs.push(this.publisherCrawled[i].name);
            }
        }
        const areaDTOs = [];
        for (let i = 0; i < this.grantFilter.areaDTOs.length; i++) {
            areaDTOs.push(this.grantFilter.areaDTOs[i].name);
        }
        const data = Object.assign({}, this.grantFilter);
        data.publisherDTOs = publisherDTOs;
        data.areaDTOs = areaDTOs;
        this.data = Object.assign({}, data);
    }

    onChangeItemsPerPage(number: number) {
        this.grantFilter.itemsPerPage = number;
        this.onFiltering();
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

    loadPublisher(login) {
        this.publisherService.getAllByCrawled(true).subscribe((publisherCrawled) => {
            if (login) {
                this.preferencesService.getAll().subscribe((preferencesDTOs) => {
                    for (let i = 0; i < publisherCrawled.length; i++) {
                        publisherCrawled[i].check = false;
                        for (let j = 0; j < preferencesDTOs.length; j++) {
                            if (publisherCrawled[i].id === preferencesDTOs[j].publisherDTO.id) {
                                publisherCrawled[i].check = preferencesDTOs[j].notification;
                            }
                        }
                    }
                    this.publisherCrawled = publisherCrawled;
                    this.loadPublisherFinished = true;
                    this.onFiltering();
                });
            } else {
                this.publisherCrawled = publisherCrawled;
                for (let i = 0; i < publisherCrawled.length; i++) {
                    publisherCrawled[i].check = true;
                }
                this.loadPublisherFinished = true;
                this.onFiltering();
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
            this.onFiltering();
        }
    }

    removeArea(area: AreaDTO) {
        const index: number = this.grantFilter.areaDTOs.indexOf(area);
        if (index !== -1) {
            this.grantFilter.areaDTOs.splice(index, 1);
            this.onFiltering();
        }
    }

    isShowFilter() {
        return this.principal.isShowFilter();
    }

    setShowFilter() {
        this.principal.setShowFilter();
        this.setActivateClicking();
    }

    isGrantpage() {
        return this.principal.isGrantpage();
    }

    setActivateClicking() {
        if (this.principal.isShowFilter()) {
            console.log('opent');
            this.ref.addClass(document.getElementById('trans-parent'), 'trans-parent');
        } else {
            this.ref.removeClass(document.getElementById('trans-parent'), 'trans-parent');
        }
    }

}

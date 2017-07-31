import {Component, OnDestroy, OnInit} from '@angular/core';
import {Principal, AreaDTO, PublisherDTO, PublisherService, AreaService} from '../shared';
import {Observable} from 'rxjs/Observable';
import {ActivatedRoute} from '@angular/router';
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
        publisherDTOs: []
    };
    data = this.grantFilter;

    constructor(private areaService: AreaService,
                private route: ActivatedRoute,
                private publisherService: PublisherService,
                private principal: Principal) {
    }

    ngOnInit() {
        this.routeSub = this.route.queryParams.subscribe((params) => {
            this.grantFilter.openGrant = !('false' === params['openGrant']);
            this.grantFilter.comingGrant = !('false' === params['comingGrant']);
        });
        this.principal.identity().then((account) => {
            this.currentAccount = account;
        });
        this.loadArea();
        this.loadPublisher();
    }

    ngOnDestroy(): void {
        this.routeSub.unsubscribe();
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
            this.loadPublisherFinished = true;
            this.onFiltering();
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
}

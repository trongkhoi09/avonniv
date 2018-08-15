import {Component, HostListener, Input, OnDestroy, OnInit} from '@angular/core';
import {NgbActiveModal, NgbModal, NgbModalRef} from '@ng-bootstrap/ng-bootstrap';
import {AlertService, EventManager} from 'ng-jhipster';

import {Account, GrantDTO, GrantService, LoginModalService, Principal, ResponseWrapper} from '../shared';
import {Router} from '@angular/router';

import {GrantSchoolDescriptionModalComponent} from './grant-school/grant-school.component';

declare const ga: Function;

@Component({
    selector: 'jhi-home',
    templateUrl: './home.component.html',
    styleUrls: [
        'home.scss'
    ]

})
export class HomeComponent implements OnInit, OnDestroy {
    account: Account;
    modalRef: NgbModalRef;
    grantDTOs: GrantDTO[] = [];
    isMobile: boolean;

    constructor(private principal: Principal,
                private loginModalService: LoginModalService,
                private grantService: GrantService,
                private alertService: AlertService,
                private eventManager: EventManager,
                private modalService: NgbModal,
                private router: Router) {
    }

    ngOnInit() {
        this.principal.setHomepage(true);
        this.principal.identity().then((account) => {
            this.account = account;
        });
        this.registerAuthenticationSuccess();
        this.grantService.recentGrants().subscribe(
            (res: ResponseWrapper) => this.onSuccess(res.json, res.headers),
            (res: ResponseWrapper) => this.onError(res.json)
        );
        window.scrollTo(0, 0);
        this.checkMobile();
    }

    private onSuccess(data, headers) {
        this.grantDTOs = data;
    }

    private onError(error) {
        this.alertService.error(error.error, error.message, null);
    }

    ngOnDestroy(): void {
        this.principal.setHomepage(false);
    }

    registerAuthenticationSuccess() {
        this.eventManager.subscribe('authenticationSuccess', (message) => {
            this.principal.identity().then((account) => {
                this.account = account;
            });
        });
    }

    isAuthenticated() {
        return this.principal.isAuthenticated();
    }

    signUp() {
        this.modalRef = this.loginModalService.open(true);
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

    open(grantSchoolDescription) {
        const modalRef = this.modalService.open(GrantSchoolDescriptionModalComponent);
        modalRef.componentInstance.grantSchoolDescription = grantSchoolDescription;
        ga('send', 'event', 'Grant School', 'view ' +  grantSchoolDescription);

    }

    showDescription(grantDTO) {
        this.router.navigate(['/', {outlets: {popup: 'description-grant/' + grantDTO.id}}]);
        ga('send', 'event', 'mostRecent', 'open dialog ' + grantDTO.title);
    }

    gotoExternalUrl(externalUrl) {
        if (!this.isMobile) {
            window.open(externalUrl, '_blank');
            ga('send', 'event', 'mostRecent', 'go to ' +  externalUrl);
        }
    }

    gotoGrants() {
        this.router.navigate(['grants']);
        ga('send', 'event', 'grants page', 'go to grantspage');
    }

    openMostRecent(grantDTO) {
        this.router.navigate([ '/', { outlets: { popup: 'description-grant/' + grantDTO.id } }]);
        ga('send', 'event', 'mostRecent', 'show dialog ' + grantDTO.title );
    }
    checkMobile() {
        if (window.innerWidth >= 992) {
            this.isMobile = false;
        } else {
            this.isMobile = true;
        }
    }

    @HostListener('window:resize', ['$event'])
    onResize(event) {
       this.checkMobile();
    }
}

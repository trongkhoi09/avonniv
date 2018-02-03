import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {NgbActiveModal, NgbModal, NgbModalRef} from '@ng-bootstrap/ng-bootstrap';
import {AlertService, EventManager} from 'ng-jhipster';

import {Account, GrantDTO, GrantService, LoginModalService, Principal, ResponseWrapper} from '../shared';
import {Router} from '@angular/router';

@Component({
    selector: 'jhi-description-grant-school-modal-component',
    template: `
        <div class="modal-body">
            <span jhiTranslate="{{descriptionGrantSchool}}"></span>
        </div>`
})
export class DescriptionGrantSchoolModalComponent {
    @Input() descriptionGrantSchool;

    constructor(public activeModal: NgbActiveModal) {
    }
}

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

    open(descriptionGrantSchool) {
        const modalRef = this.modalService.open(DescriptionGrantSchoolModalComponent);
        modalRef.componentInstance.descriptionGrantSchool = descriptionGrantSchool;
    }

    showDescription(id) {
        if (window.innerWidth < 992) {
            this.router.navigate(['/', {outlets: {popup: 'description-grant/' + id}}]);
        }
    }

    gotoExternalUrl(externalUrl) {
        if (window.innerWidth >= 992) {
            window.open(externalUrl, '_blank');
        }
    }
}

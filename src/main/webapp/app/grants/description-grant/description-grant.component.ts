import {Component, OnDestroy, OnInit} from '@angular/core';
import {NgbActiveModal, NgbModalRef} from '@ng-bootstrap/ng-bootstrap';
import {ActivatedRoute} from '@angular/router';
import {DescriptionGrantModalService} from './description-grant-modal.service';
import {GrantDTO} from '../../shared';
import {EventManager, JhiLanguageService} from 'ng-jhipster';

@Component({
    selector: 'jhi-description-grant-modal',
    templateUrl: './description-grant.component.html',
    styleUrls: [
        'description-grant.scss'
    ]
})
export class JhiDescriptionGrantModalComponent implements OnInit {
    grantDTO: GrantDTO;
    formatDate = 'MM/dd/yyyy';

    constructor(public activeModal: NgbActiveModal,
                private languageService: JhiLanguageService,
                private eventManager: EventManager) {
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
    }

    openNewTab() {
        window.open(this.grantDTO.externalUrl, '_blank');
        this.activeModal.dismiss('cancel');
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    getFinanceDescription(grantDTO) {
        if (grantDTO.grantProgramDTO.publisherDTO.id !== 9) {
            return grantDTO.financeDescription.replace(/<[^>]*>/g, '');
        } else {
            if (isNaN(grantDTO.financeDescription)) {
                return grantDTO.financeDescription.replace(/<[^>]*>/g, '');
            } else {
                return Number(Number(grantDTO.financeDescription).toFixed(1)).toLocaleString() + ' EUR';
            }
        }
    }

    isEnergimyndigheten() {
        if (this.grantDTO.grantProgramDTO.publisherDTO.name === 'Energimyndigheten') {
            return true;
        }
        return false;
    }
}

@Component({
    selector: 'jhi-description-grant-dialog',
    template: ''
})
export class DescriptionGrantDialogComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(private route: ActivatedRoute,
                private descriptionGrantModalService: DescriptionGrantModalService) {
    }

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if (params['id']) {
                this.modalRef = this.descriptionGrantModalService.open(JhiDescriptionGrantModalComponent, params['id']);
            } else {
                this.modalRef = this.descriptionGrantModalService.open(JhiDescriptionGrantModalComponent);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}

import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { AlertService, EventManager } from 'ng-jhipster';

import { PublisherModalService } from './publisher-modal.service';
import { PublisherDTO, PublisherService } from '../../shared';

@Component({
    selector: 'jhi-publisher-mgmt-dialog',
    templateUrl: './publisher-management-dialog.component.html'
})
export class PublisherMgmtDialogComponent implements OnInit {

    publisher: PublisherDTO;
    isSaving: Boolean;

    constructor(
        public activeModal: NgbActiveModal,
        private publisherService: PublisherService,
        private alertService: AlertService,
        private eventManager: EventManager
    ) {}

    ngOnInit() {
        this.isSaving = false;
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.publisher.id !== null) {
            this.publisherService.update(this.publisher).subscribe((response) => this.onSaveSuccess(response, false), () => this.onSaveError());
        } else {
            this.publisherService.create(this.publisher).subscribe((response) => this.onSaveSuccess(response, true), () => this.onSaveError());
        }
    }

    private onSaveSuccess(result, isCreated: boolean) {
        this.alertService.success(
            isCreated ? 'publisherManagement.created'
            : 'publisherManagement.updated',
            { param : result.json.name }, null);
        this.eventManager.broadcast({ name: 'publisherListModification', content: 'OK' });
        this.isSaving = false;
        this.activeModal.dismiss(result);
    }

    private onSaveError() {
        this.isSaving = false;
    }
}

@Component({
    selector: 'jhi-publisher-dialog',
    template: ''
})
export class PublisherDialogComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private publisherModalService: PublisherModalService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['name'] ) {
                this.modalRef = this.publisherModalService.open(PublisherMgmtDialogComponent, params['name']);
            } else {
                this.modalRef = this.publisherModalService.open(PublisherMgmtDialogComponent);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}

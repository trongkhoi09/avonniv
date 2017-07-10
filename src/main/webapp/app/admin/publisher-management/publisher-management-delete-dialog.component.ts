import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { AlertService, EventManager } from 'ng-jhipster';

import { PublisherDTO, PublisherService } from '../../shared';
import { PublisherModalService } from './publisher-modal.service';

@Component({
    selector: 'jhi-publisher-mgmt-delete-dialog',
    templateUrl: './publisher-management-delete-dialog.component.html'
})
export class PublisherMgmtDeleteDialogComponent {

    publisher: PublisherDTO;

    constructor(
        private publisherService: PublisherService,
        public activeModal: NgbActiveModal,
        private alertService: AlertService,
        private eventManager: EventManager
    ) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(name) {
        this.publisherService.delete(name).subscribe((response) => {
            this.eventManager.broadcast({ name: 'publisherListModification',
                content: 'Deleted a publisher'});
            this.activeModal.dismiss(true);
        });
        this.alertService.success('publisherManagement.deleted', { param : name }, null);
    }
}

@Component({
    selector: 'jhi-publisher-delete-dialog',
    template: ''
})
export class PublisherDeleteDialogComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private publisherModalService: PublisherModalService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.modalRef = this.publisherModalService.open(PublisherMgmtDeleteDialogComponent, params['name']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}

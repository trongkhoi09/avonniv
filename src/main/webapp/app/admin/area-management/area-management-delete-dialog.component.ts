import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { AlertService, EventManager } from 'ng-jhipster';

import { AreaDTO, AreaService } from '../../shared';
import { AreaModalService } from './area-modal.service';

@Component({
    selector: 'jhi-area-mgmt-delete-dialog',
    templateUrl: './area-management-delete-dialog.component.html'
})
export class AreaMgmtDeleteDialogComponent {

    area: AreaDTO;

    constructor(
        private areaService: AreaService,
        public activeModal: NgbActiveModal,
        private alertService: AlertService,
        private eventManager: EventManager
    ) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(name) {
        this.areaService.delete(name).subscribe((response) => {
            this.eventManager.broadcast({ name: 'areaListModification',
                content: 'Deleted a area'});
            this.activeModal.dismiss(true);
        });
        this.alertService.success('areaManagement.deleted', { param : name }, null);
    }
}

@Component({
    selector: 'jhi-area-delete-dialog',
    template: ''
})
export class AreaDeleteDialogComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private areaModalService: AreaModalService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.modalRef = this.areaModalService.open(AreaMgmtDeleteDialogComponent, params['name']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}

import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { AlertService, EventManager } from 'ng-jhipster';

import { AreaModalService } from './area-modal.service';
import { AreaDTO, AreaService } from '../../shared';

@Component({
    selector: 'jhi-area-mgmt-dialog',
    templateUrl: './area-management-dialog.component.html'
})
export class AreaMgmtDialogComponent implements OnInit {

    area: AreaDTO;
    isSaving: Boolean;

    constructor(
        public activeModal: NgbActiveModal,
        private areaService: AreaService,
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
        if (this.area.id !== null) {
            this.areaService.update(this.area).subscribe((response) => this.onSaveSuccess(response, false), () => this.onSaveError());
        } else {
            this.areaService.create(this.area).subscribe((response) => this.onSaveSuccess(response, true), () => this.onSaveError());
        }
    }

    private onSaveSuccess(result, isCreated: boolean) {
        this.alertService.success(
            isCreated ? 'areaManagement.created'
            : 'areaManagement.updated',
            { param : result.json.name }, null);
        this.eventManager.broadcast({ name: 'areaListModification', content: 'OK' });
        this.isSaving = false;
        this.activeModal.dismiss(result);
    }

    private onSaveError() {
        this.isSaving = false;
    }
}

@Component({
    selector: 'jhi-area-dialog',
    template: ''
})
export class AreaDialogComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private areaModalService: AreaModalService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['name'] ) {
                this.modalRef = this.areaModalService.open(AreaMgmtDialogComponent, params['name']);
            } else {
                this.modalRef = this.areaModalService.open(AreaMgmtDialogComponent);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}

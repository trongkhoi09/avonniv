import {Component, OnInit, OnDestroy} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

import {NgbActiveModal, NgbModalRef} from '@ng-bootstrap/ng-bootstrap';
import {AlertService, EventManager} from 'ng-jhipster';

import {GrantModalService} from './grant-modal.service';
import {GrantDTO, Grantservice} from '../shared';

@Component({
    selector: 'jhi-grant-mgmt-dialog',
    templateUrl: './grant-dialog.component.html'
})
export class GrantMgmtDialogComponent implements OnInit {

    grant: GrantDTO;
    isSaving: Boolean;

    constructor(public activeModal: NgbActiveModal,
                private Grantservice: Grantservice,
                private alertService: AlertService,
                private eventManager: EventManager) {
    }

    ngOnInit() {
        this.isSaving = false;
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        this.Grantservice.update(this.grant).subscribe((response) => this.onSaveSuccess(response), () => this.onSaveError());
    }

    private onSaveSuccess(result) {
        this.alertService.success('grantDialog.updated',
            {param: result.json.title}, null);
        this.eventManager.broadcast({name: 'grantListModification', content: 'OK'});
        this.isSaving = false;
        this.activeModal.dismiss(result);
    }

    private onSaveError() {
        this.isSaving = false;
    }
}

@Component({
    selector: 'jhi-grant-dialog',
    template: ''
})
export class GrantDialogComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(private route: ActivatedRoute,
                private grantModalService: GrantModalService) {
    }

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if (params['grantId']) {
                this.modalRef = this.grantModalService.open(GrantMgmtDialogComponent, params['grantId']);
            } else {
                this.modalRef = this.grantModalService.open(GrantMgmtDialogComponent);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}

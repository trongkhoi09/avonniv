import {Component, OnInit} from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { AlertService, EventManager } from 'ng-jhipster';
import {AccountService , LoginService} from '../../shared';

@Component({
    selector: 'jhi-user-delete-dialog',
    templateUrl: './confilm-delete-dialog.component.html'
})
export class ModalDeleteDialogComponent implements OnInit {
    ngOnInit(): void {
    }
    constructor(
        public activeModal: NgbActiveModal,
        private alertService: AlertService,
        private eventManager: EventManager,
        private accountService: AccountService,
        private loginService: LoginService
    ) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    deleteUser() {
        this.accountService.delete().subscribe((response) => {
            this.eventManager.broadcast({ name: 'areaListModification',
                content: 'Deleted a user'});
            this.activeModal.dismiss(true);
            this.loginService.logout();
            window.location.replace('http://avonniv.courzecorner.com/#/');
        });
        this.alertService.success('areaManagement.deleted', { param : name }, null);
    }
}

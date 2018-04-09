import {Component, OnInit} from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { AlertService, EventManager } from 'ng-jhipster';
import {AccountService , LoginService} from '../../shared';
import { Router } from '@angular/router';

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
        private loginService: LoginService,
        private router: Router
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
            this.router.navigate([ '/']);
        });
        this.alertService.success('areaManagement.deleted', { param : name }, null);
    }
}

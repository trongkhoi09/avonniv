import { Component, OnInit } from '@angular/core';
import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import {ActivatedRoute, Router} from '@angular/router';

import { Activate } from './activate.service';
import { LoginModalService } from '../../shared';

@Component({
    selector: 'jhi-activate',
    templateUrl: './activate.component.html'
})
export class ActivateComponent implements OnInit {
    error: string;
    success: string;
    modalRef: NgbModalRef;

    constructor(
        private activate: Activate,
        private loginModalService: LoginModalService,
        private route: ActivatedRoute,
        private router: Router,
    ) {
    }

    ngOnInit() {
        this.route.queryParams.subscribe((params) => {
            this.activate.get(params['key']).subscribe(() => {
                this.error = null;
                this.success = 'OK';
                this.router.navigate(['']);
                this.login();
            }, () => {
                this.success = null;
                this.error = 'ERROR';
            });
        });
    }

    login() {
        if (this.loginModalService !== null) {
            this.modalRef = this.loginModalService.open();
        }
    }
}

import {Injectable, Component} from '@angular/core';
import {Router} from '@angular/router';
import {NgbModal, NgbModalRef} from '@ng-bootstrap/ng-bootstrap';

import {GrantDTO, Grantservice} from '../shared';

@Injectable()
export class GrantModalService {
    private isOpen = false;

    constructor(private modalService: NgbModal,
                private router: Router,
                private Grantservice: Grantservice) {
    }

    open(component: Component, grantId?: string): NgbModalRef {
        if (this.isOpen) {
            return;
        }
        this.isOpen = true;
        console.log(grantId);
        if (grantId) {
            this.Grantservice.find(grantId).subscribe((grant) => this.grantModalRef(component, grant));
        } else {
            return this.grantModalRef(component, new GrantDTO());
        }
    }

    grantModalRef(component: Component, grant: GrantDTO): NgbModalRef {
        const modalRef = this.modalService.open(component, {size: 'lg', backdrop: 'static'});
        modalRef.componentInstance.grant = grant;
        modalRef.result.then((result) => {
            this.router.navigate([{outlets: {popup: null}}], {replaceUrl: true});
            this.isOpen = false;
        }, (reason) => {
            this.router.navigate([{outlets: {popup: null}}], {replaceUrl: true});
            this.isOpen = false;
        });
        return modalRef;
    }
}

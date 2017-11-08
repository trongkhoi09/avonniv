import {Component, Injectable} from '@angular/core';
import {NgbModal, NgbModalRef} from '@ng-bootstrap/ng-bootstrap';

import {Router} from '@angular/router';
import {GrantDTO, GrantService} from '../../shared';

@Injectable()
export class DescriptionGrantModalService {
    private isOpen = false;

    constructor(private modalService: NgbModal,
                private router: Router,
                private grantService: GrantService) {
    }

    open(component: Component, grantId?: string): NgbModalRef {
        if (this.isOpen) {
            return;
        }
        this.isOpen = true;

        if (grantId) {
            this.grantService.find(grantId).subscribe((grantDTO) => this.descriptionGrantModalRef(component, grantDTO));
        } else {
            return this.descriptionGrantModalRef(component, new GrantDTO());
        }
    }

    descriptionGrantModalRef(component: Component, grantDTO: GrantDTO): NgbModalRef {
        const modalRef = this.modalService.open(component, {windowClass: 'description-grant'});
        modalRef.componentInstance.grantDTO = grantDTO;
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

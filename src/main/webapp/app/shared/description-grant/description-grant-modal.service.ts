import {Component, Injectable} from '@angular/core';
import {NgbModal, NgbModalRef} from '@ng-bootstrap/ng-bootstrap';

import {GrantDTO, GrantService} from '../index';
import {JhiDescriptionGrantModalComponent} from './description-grant.component';

@Injectable()
export class DescriptionGrantModalService {
    private isOpen = false;

    constructor(private modalService: NgbModal,
                private grantService: GrantService) {
    }

    open(grant: GrantDTO): NgbModalRef {
        if (this.isOpen) {
            return;
        }
        this.isOpen = true;

        if (grant) {
            this.descriptionGrantModalRef(JhiDescriptionGrantModalComponent, grant);
        } else {
            return this.descriptionGrantModalRef(JhiDescriptionGrantModalComponent, new GrantDTO());
        }
    }

    openWithGrantId(grantId) {
        if (grantId) {
              this.grantService.find(grantId).subscribe((grantDTO) => this.descriptionGrantModalRef(JhiDescriptionGrantModalComponent, grantDTO));

            } else {
            return this.descriptionGrantModalRef(JhiDescriptionGrantModalComponent, new GrantDTO());
        }
    }

    descriptionGrantModalRef(component: Component, grantDTO: GrantDTO): NgbModalRef {
        const modalRef = this.modalService.open(component, {windowClass: 'description-grant'});
        modalRef.componentInstance.grantDTO = grantDTO;
        modalRef.result.then((result) => {
            this.isOpen = false;
        }, (reason) => {
            this.isOpen = false;
        });
        return modalRef;
    }
}

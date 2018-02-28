import {Injectable} from '@angular/core';
import {NgbModal, NgbModalRef} from '@ng-bootstrap/ng-bootstrap';
import {ModalDeleteDialogComponent} from './confilm-delete-dialog.component';

@Injectable()
export class ConfilmDeleteModalService {
    private isOpen = false;

    constructor(private modalService: NgbModal) {
    }

    open(): NgbModalRef {
        if (this.isOpen) {
            return;
        }
        this.isOpen = true;
        const modalRef = this.modalService.open(ModalDeleteDialogComponent);
        modalRef.result.then((result) => {
            this.isOpen = false;
        }, (reason) => {
            this.isOpen = false;
        });
        return modalRef;
    }
}

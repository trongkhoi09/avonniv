import {Component} from '@angular/core';
import {NgbActiveModal, NgbModal, NgbModalRef} from '@ng-bootstrap/ng-bootstrap';
import {JhiLoginModalComponent} from '../login.component';

@Component({
    selector: 'jhi-terms-component',
    templateUrl: './terms.component.html',
    styleUrls: ['terms.scss']
})
export class TermsModalComponent {

    modalRef: NgbModalRef;

    constructor(private activeModal: NgbActiveModal,
                private modalService: NgbModal) {
    }

    dismis() {
        this.activeModal.dismiss('close terms&conditoins');
    }
}

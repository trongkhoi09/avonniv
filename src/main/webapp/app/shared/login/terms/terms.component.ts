import {Component, Input} from '@angular/core';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'jhi-terms-component',
    templateUrl: './terms.component.html',
    styleUrls: ['terms.scss']
})
export class TermsModalComponent {

    constructor(public activeModal: NgbActiveModal) {
    }

    dismis() {
        this.activeModal.dismiss('close terms&conditoins');
    }
}

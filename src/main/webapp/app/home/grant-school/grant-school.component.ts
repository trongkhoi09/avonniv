import {Component, Input} from '@angular/core';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'jhi-ngbd-content-modal-component',
    templateUrl: './grant-school.component.html',
    styleUrls: ['grant-school.scss']
})
export class NgbdContentModalComponent {
    @Input() descrGrantSchool;

    constructor(public activeModal: NgbActiveModal) {}
}

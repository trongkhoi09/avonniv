import {Component, Input} from '@angular/core';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'jhi-grant-school-description-modal-component',
    templateUrl: './grant-school.component.html',
    styleUrls: ['grant-school.scss']
})
export class GrantSchoolDescriptionModalComponent {
    @Input() grantSchoolDescription;

    constructor(public activeModal: NgbActiveModal) {}
}

import {Component, OnDestroy, OnInit} from '@angular/core';
import {NgbActiveModal, NgbModalRef} from '@ng-bootstrap/ng-bootstrap';
import {ActivatedRoute} from '@angular/router';
import {DescriptionGrantModalService} from './description-grant-modal.service';
import {GrantDTO} from '../../shared';

@Component({
    selector: 'jhi-description-grant-modal',
    templateUrl: './description-grant.component.html',
    styleUrls: [
        'description-grant.scss'
    ]
})
export class JhiDescriptionGrantModalComponent implements OnInit {
    grantDTO: GrantDTO;

    constructor(public activeModal: NgbActiveModal) {
    }

    ngOnInit() {
    }
}

@Component({
    selector: 'jhi-description-grant-dialog',
    template: ''
})
export class DescriptionGrantDialogComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(private route: ActivatedRoute,
                private descriptionGrantModalService: DescriptionGrantModalService) {
    }

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if (params['id']) {
                this.modalRef = this.descriptionGrantModalService.open(JhiDescriptionGrantModalComponent, params['id']);
            } else {
                this.modalRef = this.descriptionGrantModalService.open(JhiDescriptionGrantModalComponent);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}

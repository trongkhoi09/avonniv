import {Component, OnDestroy, OnInit} from '@angular/core';
import {DescriptionGrantModalService} from '../../shared/description-grant/description-grant-modal.service';
import {ActivatedRoute} from '@angular/router';
import {NgbModalRef} from '@ng-bootstrap/ng-bootstrap';
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
            this.modalRef = this.descriptionGrantModalService.openWithGrantId( params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}

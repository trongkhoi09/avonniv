import {Component, OnDestroy, OnInit} from '@angular/core';
import {NgbActiveModal, NgbModalRef} from '@ng-bootstrap/ng-bootstrap';
import {ActivatedRoute} from '@angular/router';
import {OtherPublisherModalService} from './other-publisher-modal.service';
import {PublisherDTO} from '../../shared';

@Component({
    selector: 'jhi-other-publisher-modal',
    templateUrl: './other-publisher.component.html'
})
export class JhiOtherPublisherModalComponent implements OnInit {
    otherPublisher: PublisherDTO;

    constructor(public activeModal: NgbActiveModal) {
    }

    ngOnInit() {
    }

    openNewTab() {
        window.open(this.otherPublisher.url, '_blank');
        this.activeModal.dismiss('cancel');
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }
}

@Component({
    selector: 'jhi-other-publisher-dialog',
    template: ''
})
export class OtherPublisherDialogComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(private route: ActivatedRoute,
                private otherPublisherModalService: OtherPublisherModalService) {
    }

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if (params['name']) {
                this.modalRef = this.otherPublisherModalService.open(JhiOtherPublisherModalComponent, params['name']);
            } else {
                this.modalRef = this.otherPublisherModalService.open(JhiOtherPublisherModalComponent);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}

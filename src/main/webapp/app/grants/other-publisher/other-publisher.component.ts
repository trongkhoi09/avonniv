import {Component, OnDestroy, OnInit, ViewEncapsulation} from '@angular/core';
import {NgbActiveModal, NgbModalRef} from '@ng-bootstrap/ng-bootstrap';
import {ActivatedRoute} from '@angular/router';
import {OtherPublisherModalService} from './other-publisher-modal.service';
import {PublisherDTO} from '../../shared';
import {JhiLanguageService} from 'ng-jhipster';

@Component({
    selector: 'jhi-other-publisher-modal',
    templateUrl: './other-publisher.component.html',
    encapsulation: ViewEncapsulation.None,
    styleUrls: [
        'other-publisher.scss'
    ]
})
export class JhiOtherPublisherModalComponent implements OnInit {
    otherPublisher: PublisherDTO;
    description: string;

    constructor(public activeModal: NgbActiveModal,
                public languageService: JhiLanguageService) {
    }

    ngOnInit() {
        this.languageService.getCurrent().then((current) => {
            if (current === 'en') {
                this.description = this.otherPublisher.descriptionEN;
            } else if (current === 'sv') {
                this.description = this.otherPublisher.descriptionSWE;
            }
        });

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

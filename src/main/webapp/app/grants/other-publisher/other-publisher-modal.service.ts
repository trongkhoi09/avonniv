import {Component, Injectable} from '@angular/core';
import {NgbModal, NgbModalRef} from '@ng-bootstrap/ng-bootstrap';

import {Router} from '@angular/router';
import {PublisherDTO, PublisherService} from '../../shared';

@Injectable()
export class OtherPublisherModalService {
    private isOpen = false;

    constructor(private modalService: NgbModal,
                private router: Router,
                private publisherService: PublisherService) {
    }

    open(component: Component, name?: string): NgbModalRef {
        if (this.isOpen) {
            return;
        }
        this.isOpen = true;

        if (name) {
            this.publisherService.find(name).subscribe((otherPublisher) => this.otherPublisherModalRef(component, otherPublisher));
        } else {
            return this.otherPublisherModalRef(component, new PublisherDTO());
        }
    }

    otherPublisherModalRef(component: Component, otherPublisher: PublisherDTO): NgbModalRef {
        const modalRef = this.modalService.open(component, {windowClass: 'other-publisher'});
        modalRef.componentInstance.otherPublisher = otherPublisher;
        modalRef.result.then((result) => {
            this.router.navigate([{outlets: {popup: null}}], {replaceUrl: true});
            this.isOpen = false;
        }, (reason) => {
            this.router.navigate([{outlets: {popup: null}}], {replaceUrl: true});
            this.isOpen = false;
        });
        return modalRef;
    }
}

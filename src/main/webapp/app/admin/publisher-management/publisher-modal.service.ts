import { Injectable, Component } from '@angular/core';
import { Router } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';

import { PublisherDTO, PublisherService } from '../../shared';

@Injectable()
export class PublisherModalService {
    private isOpen = false;
    constructor(
        private modalService: NgbModal,
        private router: Router,
        private publisherService: PublisherService
    ) {}

    open(component: Component, name?: string): NgbModalRef {
        if (this.isOpen) {
            return;
        }
        this.isOpen = true;

        if (name) {
            this.publisherService.find(name).subscribe((publisher) => this.publisherModalRef(component, publisher));
        } else {
            return this.publisherModalRef(component, new PublisherDTO());
        }
    }

    publisherModalRef(component: Component, publisher: PublisherDTO): NgbModalRef {
        const modalRef = this.modalService.open(component, { size: 'lg', backdrop: 'static'});
        modalRef.componentInstance.publisher = publisher;
        modalRef.result.then((result) => {
            this.router.navigate([{ outlets: { popup: null }}], { replaceUrl: true });
            this.isOpen = false;
        }, (reason) => {
            this.router.navigate([{ outlets: { popup: null }}], { replaceUrl: true });
            this.isOpen = false;
        });
        return modalRef;
    }
}

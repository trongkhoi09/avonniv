import { Injectable, Component } from '@angular/core';
import { Router } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';

import { AreaDTO, AreaService } from '../../shared';

@Injectable()
export class AreaModalService {
    private isOpen = false;
    constructor(
        private modalService: NgbModal,
        private router: Router,
        private areaService: AreaService
    ) {}

    open(component: Component, name?: string): NgbModalRef {
        if (this.isOpen) {
            return;
        }
        this.isOpen = true;

        if (name) {
            this.areaService.find(name).subscribe((area) => this.areaModalRef(component, area));
        } else {
            return this.areaModalRef(component, new AreaDTO());
        }
    }

    areaModalRef(component: Component, area: AreaDTO): NgbModalRef {
        const modalRef = this.modalService.open(component, { size: 'lg', backdrop: 'static'});
        modalRef.componentInstance.area = area;
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

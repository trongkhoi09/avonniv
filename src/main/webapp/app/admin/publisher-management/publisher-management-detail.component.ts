import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/Rx';

import { PublisherDTO, PublisherService } from '../../shared';

@Component({
    selector: 'jhi-publisher-mgmt-detail',
    templateUrl: './publisher-management-detail.component.html'
})
export class PublisherMgmtDetailComponent implements OnInit, OnDestroy {

    publisher: PublisherDTO;
    private subscription: Subscription;

    constructor(
        private publisherService: PublisherService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['name']);
        });
    }

    load(name) {
        this.publisherService.find(name).subscribe((publisher) => {
            this.publisher = publisher;
        });
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

}

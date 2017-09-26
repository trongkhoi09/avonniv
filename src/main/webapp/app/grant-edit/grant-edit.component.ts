import {Component, OnInit, OnDestroy} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import { DatePipe } from '@angular/common';

import {AlertService} from 'ng-jhipster';

import {GrantDTO, GrantService} from '../shared';

@Component({
    selector: 'jhi-grant-edit',
    templateUrl: './grant-edit.component.html'
})
export class GrantEditComponent implements OnInit, OnDestroy {

    grant: GrantDTO;
    isSaving: Boolean;
    routeSub: any;

    constructor(private route: ActivatedRoute,
                private grantservice: GrantService,
                private alertService: AlertService,
                private datePipe: DatePipe) {
    }

    ngOnInit() {
        this.isSaving = false;
        this.routeSub = this.route.params.subscribe((params) => {
            if (params['grantId']) {
                const grantId = params['grantId'];
                if (grantId) {
                    this.grantservice.find(grantId).subscribe((grant) => this.grant = this.parser(grant));
                }
            }
        });
    }

    parser(grant: GrantDTO) {
        grant.openDate = this.parserDate(grant.openDate);
        grant.closeDate = this.parserDate(grant.closeDate);
        grant.announcedDate = this.parserDate(grant.announcedDate);
        grant.projectStartDate = this.parserDate(grant.projectStartDate);
        return grant;
    }

    parserDate(date) {
        if (date) {
            return this.datePipe.transform(new Date(date), 'yyyy-MM-dd');
        }
        return date;
    }

    format(grant: GrantDTO) {
        grant.openDate = this.formatDate(grant.openDate);
        grant.closeDate = this.formatDate(grant.closeDate);
        grant.announcedDate = this.formatDate(grant.announcedDate);
        grant.projectStartDate = this.formatDate(grant.projectStartDate);
        return grant;
    }

    formatDate(date) {
        if (date) {
            const dateConvert = new Date(date);
            return new Date(dateConvert.getTime() + dateConvert.getTimezoneOffset() * 60000).toISOString();
        }
        return date;
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }

    save() {
        this.isSaving = true;
        this.grantservice.update(this.format(this.grant)).subscribe((response) => this.onSaveSuccess(response), () => this.onSaveError());
        this.grant = this.parser(this.grant);
    }

    private onSaveSuccess(result) {
        this.alertService.success('grantEdit.updated', {}, null);
        this.isSaving = false;
        window.scrollTo(0, 0);
    }

    private onSaveError() {
        this.isSaving = false;
        window.scrollTo(0, 0);
    }
}

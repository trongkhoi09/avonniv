import {Component, OnInit, OnDestroy} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

import {AlertService} from 'ng-jhipster';

import {GrantProgramDTO, GrantProgramService} from '../shared';

@Component({
    selector: 'jhi-grant-program-edit',
    templateUrl: './grant-program-edit.component.html'
})
export class GrantProgramEditComponent implements OnInit, OnDestroy {

    grantProgram: GrantProgramDTO;
    isSaving: Boolean;
    routeSub: any;

    constructor(private route: ActivatedRoute,
                private grantProgramService: GrantProgramService,
                private alertService: AlertService) {
    }

    ngOnInit() {
        this.isSaving = false;
        this.routeSub = this.route.params.subscribe((params) => {
            if (params['grantProgramId']) {
                const grantId = params['grantProgramId'];
                if (grantId) {
                    this.grantProgramService.find(grantId).subscribe((grantProgram) => this.grantProgram = this.parser(grantProgram));
                }
            }
        });
    }

    parser(grantProgram: GrantProgramDTO) {
        grantProgram.releaseDate = this.parserDate(grantProgram.releaseDate);
        return grantProgram;
    }

    parserDate(date) {
        if (date) {
            const dateParts = date.trim().split('T');
            if (dateParts.length === 2) {
                return dateParts[0];
            }
        }
        return date;
    }

    format(grantProgram: GrantProgramDTO) {
        grantProgram.releaseDate = this.formatDate(grantProgram.releaseDate);
        return grantProgram;
    }

    formatDate(date) {
        if (date) {
            const dateParts = date.trim().split('T');
            if (dateParts.length === 1) {
                return date + 'T00:00:00Z';
            }
        }
        return date;
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }

    save() {
        this.isSaving = true;
        this.grantProgramService.update(this.format(this.grantProgram)).subscribe((response) => this.onSaveSuccess(response), () => this.onSaveError());
        this.grantProgram = this.parser(this.grantProgram);
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

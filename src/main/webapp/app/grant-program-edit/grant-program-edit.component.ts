import {Component, OnInit, OnDestroy} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

import {AlertService} from 'ng-jhipster';

import {GrantProgramDTO, GrantProgramService} from '../shared';
import {Observable} from 'rxjs/Observable';
import {AreaDTO, AreaService} from '../shared';

@Component({
    selector: 'jhi-grant-program-edit',
    templateUrl: './grant-program-edit.component.html',
    styleUrls: [
        'grant-program-edit.scss'
    ]
})
export class GrantProgramEditComponent implements OnInit, OnDestroy {

    grantProgram: GrantProgramDTO;
    isSaving: Boolean;
    routeSub: any;
    area: AreaDTO;
    areaDTOs: AreaDTO[] = [];

    constructor(private route: ActivatedRoute,
                private grantProgramService: GrantProgramService,
                private areaService: AreaService,
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
        this.loadArea();
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

    inputFormatter = (x: { name: string }) => x.name;

    resultFormatter = (result: AreaDTO) => result.name.toUpperCase();

    searchArea = (text$: Observable<string>) =>
        text$
            .debounceTime(200)
            .map((term) => term === '' ? []
                : this.areaDTOs.filter((v) => v.name.toLowerCase().indexOf(term.toLowerCase()) > -1
                && this.getIndexOf(v) === -1).slice(0, 10));

    loadArea() {
        this.areaService.getAll().subscribe((areaDTOs) => this.areaDTOs = areaDTOs);
    }

    getIndexOf(area: AreaDTO) {
        for (let i = 0; i < this.grantProgram.areaDTOs.length; i++) {
            if (this.grantProgram.areaDTOs[i].name === area.name) {
                return i;
            }
        }
        return -1;
    }

    onChangeArea() {
        if (this.area && this.area.name) {
            if (this.getIndexOf(this.area) === -1) {
                this.grantProgram.areaDTOs.unshift(this.area);
            }
            this.area = null;
        }
    }

    removeArea(area: AreaDTO) {
        const index: number = this.getIndexOf(area);
        if (index !== -1) {
            this.grantProgram.areaDTOs.splice(index, 1);
        }
    }
}

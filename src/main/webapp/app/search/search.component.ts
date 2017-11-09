import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Location} from '@angular/common';

@Component({
    selector: 'jhi-search',
    templateUrl: './search.component.html',
    styleUrls: [
        'search.scss'
    ]
})
export class SearchComponent implements OnInit {
    routeSub: any;
    data: any = {
        navigate: '/search',
        search: ''
    };

    constructor(private route: ActivatedRoute,
                private _location: Location) {
    }

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if (params['search']) {
                this.data = {
                    navigate: '/search',
                    search: params['search']
                };
            }
        });
    }

    goBack() {
        this._location.back();
    }
}

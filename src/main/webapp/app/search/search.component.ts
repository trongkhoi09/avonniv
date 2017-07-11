import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

@Component({
    selector: 'jhi-search',
    templateUrl: './search.component.html',
    styles: []
})
export class SearchComponent implements OnInit {
    routeSub: any;
    data: any = {
        navigate : '/search',
        search: ''
    };

    constructor(private route: ActivatedRoute) {
    }

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if (params['search']) {
                this.data = {
                    navigate : '/search',
                    search: params['search']
                };
            }
        });
    }

}

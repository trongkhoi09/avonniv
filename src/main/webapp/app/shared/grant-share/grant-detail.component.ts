import {AfterContentInit, Component, Input, ViewChild, ElementRef, Renderer2} from '@angular/core';
import {GrantDTO} from '../index';
import {Router} from '@angular/router';
import {DescriptionGrantModalService, JhiDescriptionGrantModalComponent} from '../description-grant/';
declare const ga: Function;

@Component({
    selector: 'jhi-grant',
    templateUrl: './grant-detail.component.html',
    styleUrls: [
        'grant-detail.scss'
    ]
})
export class GrantDetailComponent implements AfterContentInit {
    @Input()
    grantDTO: GrantDTO;

    @ViewChild('title')
    private  title: ElementRef;

    @ViewChild('description')
    private  description: ElementRef;

    ngAfterContentInit(): void {
        const thisFn = this;
        setTimeout( () => {
            const heightTitle =  thisFn.title.nativeElement.offsetHeight;
            const heightDescription = thisFn.description.nativeElement.offsetHeight;
            if (heightTitle >= 100) {
                thisFn.render2.setStyle(thisFn.description.nativeElement,  'height', '130px');
            } else {
                if (heightTitle >= 60) {
                    thisFn.render2.setStyle(thisFn.description.nativeElement, 'height', '160px');
                } else {
                    thisFn.render2.setStyle(thisFn.description.nativeElement, 'height', '200px');
                }
            }
        }, 10);
    }

    constructor(
        private router: Router,
        private render2: Renderer2,
        private descriptionGrantModalService: DescriptionGrantModalService
    ) {
    }

    showDescription(grantDTO) {
        this.descriptionGrantModalService.open(grantDTO);
    }
}

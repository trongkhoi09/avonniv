import {Routes} from '@angular/router';
import {OtherPublisherDialogComponent} from './other-publisher.component';

export const otherPublisherDialogRoute: Routes = [{
    path: 'other-publisher/:name',
    component: OtherPublisherDialogComponent,
    outlet: 'popup'
}];

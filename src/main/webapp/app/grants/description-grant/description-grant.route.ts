import {Routes} from '@angular/router';
import {DescriptionGrantDialogComponent} from './description-grant-dialog';

export const descriptionGrantDialogRoute: Routes = [{
    path: 'description-grant/:id',
    component: DescriptionGrantDialogComponent,
    outlet: 'popup'
}];

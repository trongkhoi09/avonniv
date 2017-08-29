import {GrantProgramDTO} from '../grant-program/grant-program.model';

export class GrantDTO {
    public id?: any;
    public title?: string;
    public excerpt?: string;
    public description?: string;
    public status?: number;
    public openDate?: any;
    public closeDate?: any;
    public announcedDate?: any;
    public projectStartDate?: any;
    public externalId?: string;
    public externalUrl?: string;
    public financeDescription?: string;
    public dataFromUrl?: string;
    public grantProgramDTO: GrantProgramDTO;
}

import {GrantsProgramDTO} from '../grant-program/grant-program.model';
export class GrantDTO {
    public id?: any;
    public title?: string;
    public excerpt?: string;
    public description?: string;
    public openDate?: any;
    public status?: number;
    public closeDate?: any;
    public announcedDate?: any;
    public projectStartDate?: any;
    public externalId?: string;
    public externalUrl?: string;
    public financeDescription?: string;
    public grantProgramDTO: GrantsProgramDTO;
}

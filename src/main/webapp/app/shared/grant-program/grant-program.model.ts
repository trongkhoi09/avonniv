import {PublisherDTO} from '../publisher/publisher.model';
import {AreaDTO} from '../area/area.model';
export class GrantProgramDTO {
    public id?: any;
    public name?: string;
    public description?: string;
    public type?: number;
    public releaseDate?: any;
    public externalId?: string;
    public externalUrl?: string;
    public publisherDTO?: PublisherDTO;
    public areaDTOs?: AreaDTO[] = [];
}

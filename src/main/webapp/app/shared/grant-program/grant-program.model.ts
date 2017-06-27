import {PublisherDTO} from '../publisher/publisher.model';
export class GrantsProgramDTO {
    public id?: any;
    public name?: string;
    public description?: string;
    public type?: number;
    public releaseDate?: any;
    public externalId?: string;
    public externalUrl?: string;
    public publisherDTO?: PublisherDTO;
    public areas?: any;
}

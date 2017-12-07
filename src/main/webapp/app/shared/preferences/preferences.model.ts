import {PublisherDTO, User} from '../index';

export class PreferencesDTO {
    public id?: any = null;
    public notification?: Boolean;
    public publisherDTO?: PublisherDTO;
    public userDTO?: User;
    public status?: string;
    public createdBy?: string;
    public createdDate?: Date;
    public lastModifiedBy?: string;
    public lastModifiedDate?: Date;
}

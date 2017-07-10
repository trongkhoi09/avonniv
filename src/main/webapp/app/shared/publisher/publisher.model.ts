export class PublisherDTO {
    public id?: any;
    public name?: string;
    public description?: string;
    public address?: string;
    public email?: string;
    public phone?: string;
    public url?: string;
    public crawled?: Boolean;
    public createdDate?: Date;
    public lastModifiedDate?: Date;

    constructor(id?: any,
                name?: string,
                description?: string,
                address?: string,
                email?: string,
                crawled?: Boolean,
                phone?: string,
                url?: string,
                createdDate?: Date,
                lastModifiedDate?: Date) {
        this.id = id ? id : null;
        this.name = name ? name : null;
        this.description = description ? description : null;
        this.address = address ? address : null;
        this.email = email ? email : null;
        this.crawled = crawled ? crawled : false;
        this.phone = phone ? phone : null;
        this.url = url ? url : null;
        this.createdDate = createdDate ? createdDate : null;
        this.lastModifiedDate = lastModifiedDate ? lastModifiedDate : null;
    }
}

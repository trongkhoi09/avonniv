export class Investor {
    public id?: any;
    public value?: string;
    public title?: string;
    public description?: string;
    public financing?: string;
    public applyBy?: string;
    public status?: string;

    constructor(id?: any,
                value?: string,
                title?: string,
                description?: string,
                financing?: string,
                applyBy?: string,
                status?: string) {
        this.id = id ? id : null;
        this.value = value ? value : null;
        this.title = title ? title : null;
        this.description = description ? description : null;
        this.financing = financing ? financing : null;
        this.applyBy = applyBy ? applyBy : null;
        this.status = status ? status : null;
    }
}

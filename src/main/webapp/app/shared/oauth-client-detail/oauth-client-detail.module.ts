export class OauthClientDetailModule {
    clientId?: string;
    resourceIds?: string;
    clientSecret?: string;
    scope?: string;
    authorizedGrantTypes?: string;
    webServerRedirectUri?: string;
    authorities?: string;
    accessTokenValidity?: number;
    refreshTokenValidity?: number;
    additionalInformation?: string;
    autoapprove?: string;


    constructor(clientId?: string,
                resourceIds?: string,
                clientSecret?: string,
                scope?: string,
                authorizedGrantTypes?: string,
                webServerRedirectUri?: string,
                authorities?: string,
                accessTokenValidity?: number,
                refreshTokenValidity?: number,
                additionalInformation?: string,
                autoapprove?: string) {
        this.clientId = clientId ? clientId : null;
        this.resourceIds = resourceIds ? resourceIds: null;
        this.clientSecret = clientSecret ? clientSecret: null;
        this.scope = scope ? scope : null;
        this.authorizedGrantTypes = authorizedGrantTypes ? authorizedGrantTypes: null;
        this.webServerRedirectUri = webServerRedirectUri ? webServerRedirectUri: null;
        this.authorities = authorities ? authorities : null;
        this.accessTokenValidity = accessTokenValidity ? accessTokenValidity: null;
        this.refreshTokenValidity = refreshTokenValidity ? refreshTokenValidity: null;
        this.additionalInformation = additionalInformation ? additionalInformation: null;
        this.autoapprove = autoapprove ? autoapprove: null;
    }
}

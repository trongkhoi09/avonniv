package com.avonniv.service.dto;

import com.avonniv.domain.OauthClientDetail;

import javax.validation.constraints.Size;
import java.io.Serializable;

public class OauthClientDetailDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Size(max = 255)
    private String clientId;

    @Size(max = 255)
    private String resourceIds;

    @Size(max = 255)
    private String clientSecret;

    @Size(max = 255)
    private String scope;

    @Size(max = 255)
    private String authorizedGrantTypes;

    @Size(max = 255)
    private String webServerRedirectUri;

    @Size(max = 255)
    private String authorities;

    private int accessTokenValidity;

    private int refreshTokenValidity;

    @Size(max = 4000)
    private String additionalInformation;

    @Size(max = 4000)
    private String autoapprove;

    public OauthClientDetailDTO(OauthClientDetail oauthClientDetail) {
        this(oauthClientDetail.getClientId(),
            oauthClientDetail.getResourceIds(),
            oauthClientDetail.getClientSecret(),
            oauthClientDetail.getScope(),
            oauthClientDetail.getAuthorizedGrantTypes(),
            oauthClientDetail.getWebServerRedirectUri(),
            oauthClientDetail.getAuthorities(),
            oauthClientDetail.getAccessTokenValidity(),
            oauthClientDetail.getRefreshTokenValidity(),
            oauthClientDetail.getAdditionalInformation(),
            oauthClientDetail.getAutoapprove()
            );
    }

    public OauthClientDetailDTO(String clientId, String resourceIds, String clientSecret, String scope, String authorizedGrantTypes, String webServerRedirectUri, String authorities, int accessTokenValidity, int refreshTokenValidity, String additionalInformation, String autoapprove) {
        this.clientId = clientId;
        this.resourceIds = resourceIds;
        this.clientSecret = clientSecret;
        this.scope = scope;
        this.authorizedGrantTypes = authorizedGrantTypes;
        this.webServerRedirectUri = webServerRedirectUri;
        this.authorities = authorities;
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
        this.additionalInformation = additionalInformation;
        this.autoapprove = autoapprove;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getResourceIds() {
        return resourceIds;
    }

    public void setResourceIds(String resourceIds) {
        this.resourceIds = resourceIds;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getAuthorizedGrantTypes() {
        return authorizedGrantTypes;
    }

    public void setAuthorizedGrantTypes(String authorizedGrantTypes) {
        this.authorizedGrantTypes = authorizedGrantTypes;
    }

    public String getWebServerRedirectUri() {
        return webServerRedirectUri;
    }

    public void setWebServerRedirectUri(String webServerRedirectUri) {
        this.webServerRedirectUri = webServerRedirectUri;
    }

    public String getAuthorities() {
        return authorities;
    }

    public void setAuthorities(String authorities) {
        this.authorities = authorities;
    }

    public int getAccessTokenValidity() {
        return accessTokenValidity;
    }

    public void setAccessTokenValidity(int accessTokenValidity) {
        this.accessTokenValidity = accessTokenValidity;
    }

    public int getRefreshTokenValidity() {
        return refreshTokenValidity;
    }

    public void setRefreshTokenValidity(int refreshTokenValidity) {
        this.refreshTokenValidity = refreshTokenValidity;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public String getAutoapprove() {
        return autoapprove;
    }

    public void setAutoapprove(String autoapprove) {
        this.autoapprove = autoapprove;
    }

}

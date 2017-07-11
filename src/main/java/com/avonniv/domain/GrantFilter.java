package com.avonniv.domain;

import java.util.HashSet;
import java.util.Set;

public class GrantFilter {
    private String search = null;
    private boolean publicGrant = true;
    private boolean privateGrant = true;
    private Set<String> areaDTOs = new HashSet<>();
    private Set<String> publisherDTOs = new HashSet<>();

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public boolean isPublicGrant() {
        return publicGrant;
    }

    public void setPublicGrant(boolean publicGrant) {
        this.publicGrant = publicGrant;
    }

    public boolean isPrivateGrant() {
        return privateGrant;
    }

    public void setPrivateGrant(boolean privateGrant) {
        this.privateGrant = privateGrant;
    }

    public Set<String> getAreaDTOs() {
        return areaDTOs;
    }

    public void setAreaDTOs(Set<String> areaDTOs) {
        this.areaDTOs = areaDTOs;
    }

    public Set<String> getPublisherDTOs() {
        return publisherDTOs;
    }

    public void setPublisherDTOs(Set<String> publisherDTOs) {
        this.publisherDTOs = publisherDTOs;
    }
}

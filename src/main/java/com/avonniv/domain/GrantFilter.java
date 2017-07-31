package com.avonniv.domain;

import java.util.HashSet;
import java.util.Set;

public class GrantFilter {
    private String search = null;
    private boolean openGrant = true;
    private boolean comingGrant = true;
    private Set<String> areaDTOs = new HashSet<>();
    private Set<String> publisherDTOs = new HashSet<>();

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public boolean isOpenGrant() {
        return openGrant;
    }

    public void setOpenGrant(boolean openGrant) {
        this.openGrant = openGrant;
    }

    public boolean isComingGrant() {
        return comingGrant;
    }

    public void setComingGrant(boolean comingGrant) {
        this.comingGrant = comingGrant;
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

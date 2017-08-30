package com.conference.company;

public class Booth {
    private String companyId;
    private String name;
    private String type;


    public Booth(String companyId, String name, String type) {
        this.companyId = companyId;
        this.name = name;
        this.type = "Booth";
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

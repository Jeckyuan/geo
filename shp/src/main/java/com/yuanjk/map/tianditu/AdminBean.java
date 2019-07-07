package com.yuanjk.map.tianditu;

public class AdminBean {
    private String name;
    private String cityCode;
    private String adminType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getAdminType() {
        return adminType;
    }

    public void setAdminType(String adminType) {
        this.adminType = adminType;
    }

    @Override
    public String toString() {
        return "AdminBean{" +
                "name='" + name + '\'' +
                ", cityCode='" + cityCode + '\'' +
                ", adminType='" + adminType + '\'' +
                '}';
    }
}

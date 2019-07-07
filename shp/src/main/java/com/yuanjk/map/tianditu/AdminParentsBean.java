package com.yuanjk.map.tianditu;

public class AdminParentsBean {

    private AdminBean country;
    private AdminBean province;
    private AdminBean city;

    public AdminBean getCountry() {
        return country;
    }

    public void setCountry(AdminBean country) {
        this.country = country;
    }

    public AdminBean getProvince() {
        return province;
    }

    public void setProvince(AdminBean province) {
        this.province = province;
    }

    public AdminBean getCity() {
        return city;
    }

    public void setCity(AdminBean city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "AdminParentsBean{" +
                "country=" + country +
                ", province=" + province +
                ", city=" + city +
                '}';
    }
}

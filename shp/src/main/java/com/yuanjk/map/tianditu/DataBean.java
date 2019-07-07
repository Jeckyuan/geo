package com.yuanjk.map.tianditu;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DataBean {
    private String cityCode;//行政区划码
    private String level;//行政区划等级
    private String adminType;//行政区划类别(省市县)
    private String name;//行政区划名称
    private String english;//行政区划英文名称
    private Double lnt;//显示经度
    private Double lat;//显示纬度
    private String bound;//四角点坐标
    private List<String> points;//行政区划范围面
    private String region;//行政区划范围
    private String englishabbrevation;//行政区划英文简称
    private String nameabbrevation;//行政区划简称
    private AdminParentsBean parents;//上级行政区划信息

    private List<DataBean> child;//sub admin


    public List<String> adminValues() {
        return Arrays.asList(cityCode, level, adminType, name, english, lnt.toString(), lat.toString(), StringUtils.join(points), englishabbrevation, name);
    }

    public Double getLnt() {
        return lnt;
    }

    public void setLnt(Double lnt) {
        this.lnt = lnt;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public String getBound() {
        return bound;
    }

    public void setBound(String bound) {
        this.bound = bound;
    }

    public List<String> getPoints() {
        return points;
    }

    public void setPoints(List<String> points) {
        this.points = points;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
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

    public String getEnglishabbrevation() {
        return englishabbrevation;
    }

    public void setEnglishabbrevation(String englishabbrevation) {
        this.englishabbrevation = englishabbrevation;
    }

    public String getNameabbrevation() {
        return nameabbrevation;
    }

    public void setNameabbrevation(String nameabbrevation) {
        this.nameabbrevation = nameabbrevation;
    }

    public AdminParentsBean getParents() {
        return parents;
    }

    public void setParents(AdminParentsBean parents) {
        this.parents = parents;
    }

    public List<DataBean> getChild() {
        return child;
    }

    public void setChild(List<DataBean> child) {
        this.child = child;
    }


    @Override
    public String toString() {
        return "DataBean{" +
                "lnt=" + lnt +
                ", lat=" + lat +
                ", level='" + level + '\'' +
                ", name='" + name + '\'' +
                ", english='" + english + '\'' +
                ", bound='" + bound + '\'' +
                ", points=" + points +
                ", region='" + region + '\'' +
                ", cityCode='" + cityCode + '\'' +
                ", adminType='" + adminType + '\'' +
                ", englishabbrevation='" + englishabbrevation + '\'' +
                ", nameabbrevation='" + nameabbrevation + '\'' +
                ", parents=" + parents +
                ", child=" + child +
                '}';
    }
}

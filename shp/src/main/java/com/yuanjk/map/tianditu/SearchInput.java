package com.yuanjk.map.tianditu;

public class SearchInput {
    private String searchWord;//查询关键字
    private Integer searchType;//查询类型(0：根据code查询，1：根据名称) 0
    private Boolean needSubInfo;//是否需要下一级信息 false
    private Boolean needAll;//是否需要所有子节点(包括孙子节点) false
    private Boolean needPolygon;//是否需要行政区划范围 false
    private Boolean needPre;//是否需要上一级所有信息 false

    public String getSearchWord() {
        return searchWord;
    }

    public void setSearchWord(String searchWord) {
        this.searchWord = searchWord;
    }

    public Integer getSearchType() {
        return searchType;
    }

    public void setSearchType(Integer searchType) {
        this.searchType = searchType;
    }

    public Boolean getNeedSubInfo() {
        return needSubInfo;
    }

    public void setNeedSubInfo(Boolean needSubInfo) {
        this.needSubInfo = needSubInfo;
    }

    public Boolean getNeedAll() {
        return needAll;
    }

    public void setNeedAll(Boolean needAll) {
        this.needAll = needAll;
    }

    public Boolean getNeedPolygon() {
        return needPolygon;
    }

    public void setNeedPolygon(Boolean needPolygon) {
        this.needPolygon = needPolygon;
    }

    public Boolean getNeedPre() {
        return needPre;
    }

    public void setNeedPre(Boolean needPre) {
        this.needPre = needPre;
    }
}

package com.yuanjk.map.tianditu;

import java.util.List;

public class ResponseBean {

    private String msg;//返回消息
    private String dataversion;//数据版本(只返回最新数据版本日期)
    private String returncode;//100 正常 ； 101 没有查到结果 ；其他异常请看描述
    private List<DataBean> data;//返回的行政区划信息

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getDataversion() {
        return dataversion;
    }

    public void setDataversion(String dataversion) {
        this.dataversion = dataversion;
    }

    public String getReturncode() {
        return returncode;
    }

    public void setReturncode(String returncode) {
        this.returncode = returncode;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResponseBean{" +
                "msg='" + msg + '\'' +
                ", dataversion='" + dataversion + '\'' +
                ", returncode='" + returncode + '\'' +
                ", data=" + data +
                '}';
    }
}

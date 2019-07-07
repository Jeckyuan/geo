package com.yuanjk.map.tianditu;

import com.alibaba.fastjson.JSON;
import com.yuanjk.util.HttpCLientUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TiandituAdminClawer {
    private static final Logger log = LoggerFactory.getLogger(TiandituAdminClawer.class);

    public static final String TOKEN = "c479c3f49f028252896530ee09fca4cc";

    public URI getUri(List<NameValuePair> parameters) throws URISyntaxException {
        URI uri = new URIBuilder()
                .setScheme("http")
                .setHost("api.tianditu.gov.cn")
                .setPath("/administrative")
                .setParameters(parameters)
                .build();
        log.info("get request uri: {}", uri.toString());
        return uri;
    }

    public void allAdmin(String filePath) throws URISyntaxException, IOException, InterruptedException {
        String cityCode = "156000000";//中华人民共和国
        String content = HttpCLientUtil.getContentAsString(getUri(getQueryParameters(cityCode)));
        log.info("response content: {}", content);
        ResponseBean responseBean = JSON.parseObject(content, ResponseBean.class);
        saveToFile(filePath, responseBean);

        // List<DataBean> dataBeanList = responseBean.getData();
        // log.info("admin size [{}]", dataBeanList.size());
        //
        // for (DataBean dataBean : dataBeanList) {
        //     log.info("admin bean: {}", dataBean);
        // }
    }

    private void saveToFile(String filePath, ResponseBean responseBean) throws IOException, InterruptedException, URISyntaxException {
        Thread.sleep(3000L);
        Path path = Paths.get(filePath);
        if (responseBean == null || responseBean.getData() == null || responseBean.getData().size() < 1) {
            return;
        }
        List<DataBean> dataBeanList = responseBean.getData();
        for (DataBean dataBean : dataBeanList) {
            Files.write(path, Collections.singletonList(StringUtils.join(dataBean.adminValues(), ';')), Charset.forName("utf-8"), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
            List<DataBean> childDataBeanList = dataBean.getChild();
            if (childDataBeanList != null && childDataBeanList.size() > 0) {
                for (DataBean childDataBean : childDataBeanList) {
                    try {

                        if (StringUtils.isNotBlank(childDataBean.getCityCode())) {
                            String content = HttpCLientUtil.getContentAsString(getUri(getQueryParameters(childDataBean.getCityCode())));
                            log.info("child response content: {}", content);
                            ResponseBean childResponseBean = JSON.parseObject(content, ResponseBean.class);
                            saveToFile(filePath, childResponseBean);
                        }
                    } catch (Exception e) {
                        log.error("save data bean failed: {}", childDataBean);
                        log.error("save failed", e);
                    }
                }
            }
        }
    }

    private List<NameValuePair> getQueryParameters(String cityCode) {
        SearchInput searchInput = new SearchInput();
        searchInput.setSearchWord(cityCode);
        searchInput.setSearchType(0);
        searchInput.setNeedAll(false);
        searchInput.setNeedPolygon(true);
        searchInput.setNeedPre(true);
        searchInput.setNeedSubInfo(true);

        String postStr = JSON.toJSONString(searchInput);
        NameValuePair valuePair1 = new BasicNameValuePair("postStr", postStr);
        NameValuePair nameValuePair2 = new BasicNameValuePair("tk", TiandituAdminClawer.TOKEN);
        List<NameValuePair> nameValuePairList = new ArrayList<>();
        nameValuePairList.add(valuePair1);
        nameValuePairList.add(nameValuePair2);

        return nameValuePairList;
    }

}

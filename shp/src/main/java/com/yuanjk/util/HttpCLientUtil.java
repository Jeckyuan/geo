package com.yuanjk.util;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;

public class HttpCLientUtil {
    private static final Logger log = LoggerFactory.getLogger(HttpCLientUtil.class);

    private static CloseableHttpClient httpclient = HttpClients.createDefault();

    public static String getContentAsString(URI uri) throws IOException {
        String content = null;
        HttpGet httpget = new HttpGet(uri);
        CloseableHttpResponse response = httpclient.execute(httpget);
        try {
            log.info("response status line: {}", response.getStatusLine());
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                long len = entity.getContentLength();
                log.info("content length: {}", len);
                content = EntityUtils.toString(entity);
                // if (len != -1 && len < 2 * 1024 * 1024) {
                //     System.out.println(content);
                // } else {
                //     log.error("response content is too large");
                // }
            }
        } finally {
            response.close();
        }
        return content;
    }


}

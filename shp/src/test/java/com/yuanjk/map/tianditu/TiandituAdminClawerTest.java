package com.yuanjk.map.tianditu;

import com.alibaba.fastjson.JSON;
import org.apache.http.message.BasicNameValuePair;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.apache.http.NameValuePair;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TiandituAdminClawerTest {

    private static TiandituAdminClawer tiandituAdminClawer;

    @Before
    public void setUp() throws Exception {
        tiandituAdminClawer = new TiandituAdminClawer();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getUri() throws URISyntaxException {
        SearchInput searchInput = new SearchInput();
        searchInput.setSearchWord("中国");
        searchInput.setSearchType(1);
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

        System.out.println(tiandituAdminClawer.getUri(nameValuePairList));

    }


    @Test
    public void getAllAdmin() {
        try {
            tiandituAdminClawer.allAdmin();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
package com.yuanjk.map.tianditu;

import com.alibaba.fastjson.JSON;
import org.apache.http.message.BasicNameValuePair;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.apache.http.NameValuePair;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        String filePath = "D:\\tmp\\allAdmin_v2.txt";
        String cityCode = "156000000";//中华人民共和国
        String cityName = "中华人民共和国";//中华人民共和国

        // String cityCode = "156110000";
        // String cityName = "北京市";
        try {
            tiandituAdminClawer.allAdmin(filePath, cityCode, cityName);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void pathResolve() {
        String pathStr = "D:\\data\\spatial_data\\China\\admin\\Untitled-1.json";
        Path path = Paths.get(pathStr);
        System.out.println("source path: " + path.toString());
        System.out.println("source path get parent: " + path.getParent());
        System.out.println("source path get root: " + path.getRoot());
        String partPath = "srcContent/aa.txt";
        Path tmpPath = path.getParent().resolve(partPath);
        System.out.println("resolve path: " + tmpPath.toString());


        System.out.println("file path separator: "+ File.pathSeparator);
        System.out.println("file separator: "+ File.separator);

    }

}
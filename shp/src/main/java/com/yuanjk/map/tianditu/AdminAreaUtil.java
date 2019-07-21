package com.yuanjk.map.tianditu;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.compress.changes.ChangeSet;
import org.apache.commons.io.FileUtils;
import org.opengis.metadata.identification.CharacterSet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class AdminAreaUtil {


    public static void main(String[] args) throws IOException {
        String inputFile = "d:\\data\\spatial_data\\China\\admin\\BeiJingCity.json";
        String outputFile = "D:\\data\\spatial_data\\China\\admin\\BeiJingCity.txt";

        String content = FileUtils.readFileToString(Paths.get(inputFile).toFile(), "utf8");

        Path beijingArea = Paths.get(outputFile);

        JSONObject jsonObject = JSON.parseObject(content);
        JSONArray dataArray = jsonObject.getJSONArray("data");
        JSONObject firstDataBean = dataArray.getJSONObject(0);
        List<String> areas = new ArrayList<>();
        pointsToPolygon(areas, firstDataBean);
        JSONArray childArray = dataArray.getJSONObject(0).getJSONArray("child");

        for (int i = 0; i < childArray.size(); i++) {
            JSONObject childObj = childArray.getJSONObject(i);
            // String name = childObj.getString("name");
            // System.out.println("name=" + name);
            // StringBuilder sb = new StringBuilder();
            // sb.append(name).append(" ");
            // sb.append("MultiPolygon ((");
            // JSONArray pointsArray = childObj.getJSONArray("points");
            // for (int k = 0; k < pointsArray.size(); k++) {
            //     JSONObject pointsObj = pointsArray.getJSONObject(k);
            //     if (k != 0) {
            //         sb.append(",");
            //     }
            //     sb.append("(").append(pointsObj.getString("region")).append(")");
            // }
            // sb.append("))");
            // areas.add(sb.toString());
            pointsToPolygon(areas, childObj);
        }
        Files.write(beijingArea, areas, StandardCharsets.UTF_8);
    }

    private static void pointsToPolygon(List<String> areas, JSONObject jsonObject) {
        String name = jsonObject.getString("name");
        System.out.println("name=" + name);
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" ");
        sb.append("MultiPolygon ((");
        JSONArray pointsArray = jsonObject.getJSONArray("points");
        for (int k = 0; k < pointsArray.size(); k++) {
            JSONObject pointsObj = pointsArray.getJSONObject(k);
            if (k != 0) {
                sb.append(",");
            }
            sb.append("(").append(pointsObj.getString("region")).append(")");
        }
        sb.append("))");
        areas.add(sb.toString());
    }

}

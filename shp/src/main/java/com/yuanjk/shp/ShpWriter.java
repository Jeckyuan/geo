package com.yuanjk.shp;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class ShpWriter {

    private static Double startLongitude = 115.964D;
    private static Double endLongitude = 116.742D;
    private static Double startLatitude = 39.616D;
    private static Double endLatitude = 40.566D;

    public static void main(String[] args) throws IOException {
        String points = "G:\\data\\geospatial_data\\all_points_wkt.txt";
        // FileWriter fw = new FileWriter(points);
        Path path = Paths.get(points);
        Charset utf8 = StandardCharsets.UTF_8;
        double longitudeInc = (endLongitude - startLongitude) / 1000;
        double latitudeInc = (endLatitude - startLatitude) / 1000;

        for (int i = 0; i < 1000; i++) {
            List<String> pts = new ArrayList<>();
            for (int j = 0; j < 1000; j++) {
                double tmpLongitude = startLongitude + (i * longitudeInc);
                double tmpLatitude = startLatitude + (j * latitudeInc);
                pts.add("Point (" + tmpLongitude + " " + tmpLatitude + ")");
            }
            Files.write(path, pts, utf8, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        }
    }

}

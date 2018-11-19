package com.yuanjk.shp;

import junit.framework.TestCase;
import org.junit.Test;

import java.io.IOException;

public class ShpLoaderTest extends TestCase {

    @Test
    public void testGetShpStructure() throws IOException {
        String shpPathStr1 = "G:\\data\\geospatial_data\\italy-points-shape\\points.shp";
        String shpPathStr2 = "G:\\data\\geospatial_data\\building\\building.shp";

        ShpLoader.getShpStructure(shpPathStr1);
        ShpLoader.getShpStructure(shpPathStr2);
    }
}
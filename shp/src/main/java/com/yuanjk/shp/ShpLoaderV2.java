package com.yuanjk.shp;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.Query;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.WKTWriter2;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.yuanjk.util.PinyinUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@SuppressWarnings("Duplicates")
public class ShpLoaderV2 {

    private static Logger log = LoggerFactory.getLogger(ShpLoaderV2.class);

    public static final String FID = "feature_id";
    public static final String THE_GEOM = "the_geom";
    private static final String CREATE_PROPERTY_CMD_TEMPLATE = "CREATE PROPERTY %s %s";
    private static final String CREATE_SPATIAL_INDEX_CMD_TEMPLATE = "CREATE INDEX %s ON %s(%s) SPATIAL ENGINE LUCENE";

    public static SimpleDateFormat defaultDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) throws IOException {
        OrientDB orientDB = new OrientDB("remote:localhost", OrientDBConfig.defaultConfig());
        ODatabaseSession db = orientDB.open("spatial", "root", "wyc");
        String shpPathStr = "G:\\data\\geospatial_data\\查询测试数据\\WGS84\\电力工程电网隧道.shp";

        try {
            String className = "XHC_KJCX_DLSD_V2";
            createOClass(db, className, shpPathStr);
            contentInsert(db, shpPathStr, className);
        } finally {
            db.close();
        }
    }


    public static boolean createOClass(ODatabaseSession database, String className, String shpFile) throws IOException {
        File file = new File(shpFile);
        FileDataStore dataStore = FileDataStoreFinder.getDataStore(file);
        SimpleFeatureSource featureSource = dataStore.getFeatureSource();
        ((ShapefileDataStore) dataStore).setCharset(Charset.forName("UTF-8"));
        SimpleFeatureType featureType = featureSource.getSchema();

        List<AttributeDescriptor> attributeDescriptorList = featureType.getAttributeDescriptors();
        StringBuilder stringBuilder = new StringBuilder();
        //add new class
        OSchema oSchema = database.getMetadata().getSchema();
        if (oSchema.existsClass(className)) {
            log.error("OClass of {} is already exists", className);
            return false;
        }

        OClass oClass = database.createClass(className);
        stringBuilder.append(className).append(": \n");
        oClass.createProperty(FID, OType.STRING);
        stringBuilder.append("\t").append(FID).append("\t").append(OType.STRING).append("\n");
        GeometryDescriptor geometryDescriptor = featureType.getGeometryDescriptor();
        if (geometryDescriptor == null) {
            log.error("no geometry descriptor exists");
            return false;
        }
        String geoEmbeddedClassName = "O" + geometryDescriptor.getType().getName();
        OClass geoOClass = oSchema.getClass(geoEmbeddedClassName);
        if (geoOClass == null) {
            log.error("embedded OClass of {} not exists", geoEmbeddedClassName);
            return false;
        }
        oClass.createProperty(THE_GEOM, OType.EMBEDDED, geoOClass);
        stringBuilder.append("\t").append(THE_GEOM).append("\t").append(OType.EMBEDDED).append("\t").append(geoEmbeddedClassName).append("\n");

        for (AttributeDescriptor attributeDescriptor : attributeDescriptorList) {
            OType oType = OType.getTypeByClass(attributeDescriptor.getType().getBinding());
            String propertyName = attributeDescriptor.getName().toString();
            System.out.println("property name: " + propertyName);
            if (propertyName.toLowerCase().equals(FID.toLowerCase()) || propertyName.toLowerCase().equals(THE_GEOM.toLowerCase())) {
                continue;
            }
            if (oType == null) {
                log.error("Can not get OType of this property, name={}, class={}", attributeDescriptor.getName(), attributeDescriptor.getType().getBinding());
            } else {
                propertyName = PinyinUtils.getPinYinWithoutSpecialChar(propertyName);
                stringBuilder.append("\t").append(propertyName).append("\t").append(oType).append("\n");
                oClass.createProperty(propertyName, oType);
            }
        }
        log.info("OClass " + stringBuilder);
        return true;
    }

    public static void contentInsert(ODatabaseSession database, String shpFile, String className) throws IOException {
        File file = new File(shpFile);
        FileDataStore dataStore = FileDataStoreFinder.getDataStore(file);
        ((ShapefileDataStore) dataStore).setCharset(Charset.forName("UTF-8"));
        SimpleFeatureSource featureSource = dataStore.getFeatureSource();
        SimpleFeatureType featureType = featureSource.getSchema();
        Query query = new Query(featureType.getTypeName());
        query.setMaxFeatures(Integer.MAX_VALUE);
        FeatureCollection<SimpleFeatureType, SimpleFeature> collection = featureSource.getFeatures(query);

        try (FeatureIterator<SimpleFeature> features = collection.features()) {
            while (features.hasNext()) {
                StringBuilder builder = new StringBuilder();
                builder.append("INSERT INTO ").append(className).append(" SET ");
                SimpleFeature feature = features.next();
                //add feature id property
                String column = String.format(" %s = \'%s\' ", FID, feature.getID().substring(feature.getID().lastIndexOf('.') + 1));
                builder.append(column);
                //add geometry property
                Geometry geometry = (Geometry) feature.getDefaultGeometry();
                builder.append(", " + THE_GEOM + " = St_GeomFromText(\"").append((new WKTWriter2()).writeFormatted(geometry).replaceAll("\n", "")).append("\") ");
                //非空间类型
                //add other properties
                for (Property property : feature.getProperties()) {
                    String propertyName = property.getName().toString();
                    if (THE_GEOM.toLowerCase().equals(propertyName.toLowerCase())) {
                        continue;
                    }
                    propertyName = PinyinUtils.getPinYinWithoutSpecialChar(propertyName);
                    Object propertyValue = property.getValue();
                    if (propertyValue != null) {
                        String propertyValueStr;
                        Class aClass = property.getType().getBinding();
                        if (Date.class.equals(aClass)) {
                            propertyValueStr = defaultDateFormat.format(property.getValue());
                        } else {
                            propertyValueStr = property.getValue().toString();
                        }
                        if (propertyValueStr != null && !propertyValueStr.trim().equals("")) {
                            column = String.format(" , %s = \'%s\'", propertyName, propertyValueStr.replace("'", " "));
                            builder.append(column);
                        }
                    }
                }
                // System.out.println(builder.toString());
                try {
                    // database.command(new OCommandSQL(builder.toString())).execute();
                    database.command(builder.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("insert error: " + builder);
                }
            }
            String createIndexCmd = String.format(CREATE_SPATIAL_INDEX_CMD_TEMPLATE, className + "." + THE_GEOM, className, THE_GEOM);
            System.out.println(createIndexCmd);
            // database.command(new OCommandSQL(createIndexCmd));
            database.command(createIndexCmd);
        } finally {
            if (dataStore != null) {
                dataStore.dispose();
            }
        }
    }

}

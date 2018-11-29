package com.yuanjk.shp;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.WKTWriter2;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@SuppressWarnings("Duplicates")
public class ShpLoader {

    private static Logger log = LoggerFactory.getLogger(ShpLoader.class);

    public static final String FID = "feature_id";
    public static final String THE_GEOM = "the_geom";
    private static final String CREATE_PROPERTY_CMD_TEMPLATE = "CREATE PROPERTY %s %s";
    private static final String CREATE_SPATIAL_INDEX_CMD_TEMPLATE = "CREATE INDEX %s ON %s(%s) SPATIAL ENGINE LUCENE";

    public static SimpleDateFormat defaultDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) throws IOException {
        ODatabaseDocumentTx database = new ODatabaseDocumentTx("remote:localhost/spatial").open("root", "wyc");
        // String shpPathStr = "D:\\data\\spatial_data\\italy-points-shape\\points.shp";
        // String shpPathStr = "D:\\data\\spatial_data\\ne_10m_rivers_lake_centerlines\\ne_10m_rivers_lake_centerlines.shp";
        String shpPathStr = "G:\\data\\geospatial_data\\italy-points-shape\\points.shp";
        // String shpPathStr = "G:\\data\\geospatial_data\\ne_10m_admin_1_states_provinces\\ne_10m_admin_1_states_provinces.shp";

        database.begin();
        try {
            // YOUR CODE
            // createDocument(database, shpPathStr);
            // contentInsert(database, shpPathStr, "test_load_point");
            // listAllOClasses(database);
            String className = "italy_points";
            // String className = "states_provinces_boundary";
            // createDocument(database, className, shpPathStr);
            createOClass(database, className, shpPathStr);
            contentInsert(database, shpPathStr, className);
            // getShpStructure(shpPathStr);
            database.commit();
        } finally {
            database.close();
        }

    }

    public static boolean createDocument(ODatabaseDocumentTx database, String className, String shpFile) throws IOException {
        File file = new File(shpFile);
        FileDataStore myData = FileDataStoreFinder.getDataStore(file);
        SimpleFeatureSource featureSource = myData.getFeatureSource();
        SimpleFeatureType featureType = featureSource.getSchema();

        List<AttributeDescriptor> attributeDescriptorList = featureType.getAttributeDescriptors();
        StringBuilder stringBuilder = new StringBuilder();
        //add new class
        OSchema oSchema = database.getMetadata().getSchema();
        if (oSchema.existsClass(className)) {
            log.error("OClass of {} is already exists", className);
            return false;
        }
        OClass oClass = oSchema.createClass(className);
        stringBuilder.append(className).append(": \n");
        //add default properties: fid-->string, the_geom-->embedded
        oClass.createProperty(FID, OType.STRING);
        stringBuilder.append("\t").append(FID).append("\t").append(OType.STRING).append("\n");
        GeometryDescriptor geometryDescriptor = featureType.getGeometryDescriptor();
        if (geometryDescriptor == null) {
            log.error("no geometry descriptor exists");
            return false;
        }
        String geoEmbeddedClassName = "O" + geometryDescriptor.getType().getName();
        // log.info("geoEmbeddedClassName={}", geoEmbeddedClassName);
        OClass geoOClass = oSchema.getClass(geoEmbeddedClassName);
        if (geoOClass == null) {
            log.error("embedded OClass of {} not exists", geoEmbeddedClassName);
            return false;
        }
        oClass.createProperty(THE_GEOM, OType.EMBEDDED, geoOClass);
        stringBuilder.append("\t").append(THE_GEOM).append("\t").append(OType.EMBEDDED).append("\t").append(geoEmbeddedClassName).append("\n");

        for (AttributeDescriptor attributeDescriptor : attributeDescriptorList) {
            OType oType = OType.getTypeByClass(attributeDescriptor.getType().getBinding());
            if (oType == null) {
                log.error("Can not get OType of this property, name={}, class={}", attributeDescriptor.getName(), attributeDescriptor.getType().getBinding());
            } else {
                stringBuilder.append("\t").append(attributeDescriptor.getName()).append("\t").append(oType).append("\n");
                oClass.createProperty(attributeDescriptor.getName().toString(), oType);
            }
        }
        log.info("OClass " + stringBuilder);
        oSchema.save();
        return true;
    }

    public void queryDocument() {
        ODatabaseDocumentTx db = new ODatabaseDocumentTx("remote:localhost/spatial").open("root", "wyc");

        List<ODocument> result = db.query(new OSQLSynchQuery<ODocument>("SELECT FROM Restaurant"));
        if (result != null) {
            for (ODocument oDocument : result) {
                System.out.println("name=" + oDocument.field("name") + ", location=" + oDocument.field("location"));
            }
        } else {
            System.out.println("No record is found");
        }
        OClass account = db.getMetadata().getSchema().createClass("Account");
        account.createProperty("id", OType.INTEGER);
        account.createProperty("birthDate", OType.DATE);
    }

    public static void contentLoader(String shpFile, String className) throws IOException {
        File file = new File(shpFile);
        FileDataStore dataStore = FileDataStoreFinder.getDataStore(file);
        SimpleFeatureSource featureSource = dataStore.getFeatureSource();
        SimpleFeatureType featureType = featureSource.getSchema();
        Query query = new Query(featureType.getTypeName());
        query.setMaxFeatures(3);
        FeatureCollection<SimpleFeatureType, SimpleFeature> collection = featureSource.getFeatures(query);

        try (FeatureIterator<SimpleFeature> features = collection.features()) {
            while (features.hasNext()) {

                ODocument doc = new ODocument(className);

                SimpleFeature feature = features.next();
                //add fid property
                System.out.println("fid" + " = " + feature.getID());
                doc.field("fid", feature.getID());
                //add geometry property
                GeometryAttribute geometryAttribute = feature.getDefaultGeometryProperty();
                String geoClassName = "O" + geometryAttribute.getType().getBinding().getSimpleName();
                System.out.println("geoClassName" + ": " + geoClassName);
                Geometry geometry = (Geometry) feature.getDefaultGeometry();
                ODocument theGeom = new ODocument(geoClassName);
                theGeom.field("coordinates", geometry.getCoordinates());
                doc.field("the_geom", theGeom);

                for (Property attribute : feature.getProperties()) {
                    System.out.println(attribute.getType().getBinding().getSimpleName() + "\t" + attribute.getName() + ":" + attribute.getValue());
                    doc.field(attribute.getName().toString(), attribute.getValue());
                }
                doc.save();
            }
        }
    }

    public static boolean createOClass(ODatabaseDocumentTx database, String className, String shpFile) throws IOException {
        File file = new File(shpFile);
        FileDataStore myData = FileDataStoreFinder.getDataStore(file);
        SimpleFeatureSource featureSource = myData.getFeatureSource();
        SimpleFeatureType featureType = featureSource.getSchema();

        List<AttributeDescriptor> attributeDescriptorList = featureType.getAttributeDescriptors();
        StringBuilder stringBuilder = new StringBuilder();
        //add new class
        OSchema oSchema = database.getMetadata().getSchema();
        if (oSchema.existsClass(className)) {
            log.error("OClass of {} is already exists", className);
            return false;
        }

        String createClassCmd = "CREATE CLASS " + className;
        database.command(new OCommandSQL(createClassCmd)).execute();
        System.out.println(createClassCmd);
        // OClass oClass = oSchema.createClass(className);
        stringBuilder.append(className).append(": \n");
        //add default properties: fid-->string, the_geom-->embedded
        // oClass.createProperty(FID, OType.STRING);
        String cmd = String.format(CREATE_PROPERTY_CMD_TEMPLATE, className + "." + FID, OType.STRING);
        System.out.println(cmd);
        database.command(new OCommandSQL(cmd)).execute();
        stringBuilder.append("\t").append(FID).append("\t").append(OType.STRING).append("\n");
        GeometryDescriptor geometryDescriptor = featureType.getGeometryDescriptor();
        if (geometryDescriptor == null) {
            log.error("no geometry descriptor exists");
            return false;
        }
        String geoEmbeddedClassName = "O" + geometryDescriptor.getType().getName();
        // log.info("geoEmbeddedClassName={}", geoEmbeddedClassName);
        OClass geoOClass = oSchema.getClass(geoEmbeddedClassName);
        if (geoOClass == null) {
            log.error("embedded OClass of {} not exists", geoEmbeddedClassName);
            return false;
        }
        // oClass.createProperty(THE_GEOM, OType.EMBEDDED, geoOClass);
        cmd = String.format(CREATE_PROPERTY_CMD_TEMPLATE, className + "." + THE_GEOM, OType.EMBEDDED + " " + geoEmbeddedClassName);
        System.out.println(cmd);
        database.command(new OCommandSQL(cmd)).execute();
        stringBuilder.append("\t").append(THE_GEOM).append("\t").append(OType.EMBEDDED).append("\t").append(geoEmbeddedClassName).append("\n");

        for (AttributeDescriptor attributeDescriptor : attributeDescriptorList) {
            OType oType = OType.getTypeByClass(attributeDescriptor.getType().getBinding());
            String propertyName = attributeDescriptor.getName().toString();
            if (propertyName.toLowerCase().equals(FID.toLowerCase()) || propertyName.toLowerCase().equals(THE_GEOM.toLowerCase())) {
                continue;
            }
            if (oType == null) {
                log.error("Can not get OType of this property, name={}, class={}", attributeDescriptor.getName(), attributeDescriptor.getType().getBinding());
            } else {
                stringBuilder.append("\t").append(attributeDescriptor.getName()).append("\t").append(oType).append("\n");
                // oClass.createProperty(attributeDescriptor.getName().toString(), oType);
                cmd = String.format(CREATE_PROPERTY_CMD_TEMPLATE, className + "." + attributeDescriptor.getName(), oType.toString());
                System.out.println(cmd);
                database.command(new OCommandSQL(cmd)).execute();
            }
        }
        log.info("OClass " + stringBuilder);
        return true;
    }

    public static void contentInsert(ODatabaseDocumentTx database, String shpFile, String className) throws IOException {
        File file = new File(shpFile);
        FileDataStore dataStore = FileDataStoreFinder.getDataStore(file);
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
                // builder.append(FID + " = '").append(feature.getID()).append("' ");
                String column = String.format(" %s = \'%s\' ", FID, feature.getID());
                builder.append(column);
                //add geometry property
                Geometry geometry = (Geometry) feature.getDefaultGeometry();
                builder.append(", " + THE_GEOM + " = St_GeomFromText(\"").append((new WKTWriter2()).writeFormatted(geometry)).append("\") ");
                //add other properties
                for (Property property : feature.getProperties()) {
                    String propertyName = property.getName().toString();
                    if (THE_GEOM.toLowerCase().equals(propertyName.toLowerCase())) {
                        continue;
                    }
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
                System.out.println(builder.toString());
                try {
                    database.command(new OCommandSQL(builder.toString())).execute();
                } catch (Exception e) {
                    System.out.println("insert error: " + builder);
                }
            }
            String createIndexCmd = String.format(CREATE_SPATIAL_INDEX_CMD_TEMPLATE, className + "." + THE_GEOM, className, THE_GEOM);
            System.out.println(createIndexCmd);
            database.command(new OCommandSQL(createIndexCmd));
        } finally {
            if (dataStore != null) {
                dataStore.dispose();
            }
        }
    }

    public static void listAllOClasses(ODatabaseDocumentTx database) {
        OSchema oSchema = database.getMetadata().getSchema();
        Collection<OClass> oClassCollection = oSchema.getClasses();
        for (OClass oClass : oClassCollection) {
            System.out.println(oClass.getName() + ": ");
            Collection<OProperty> oPropertyCollection = oClass.properties();
            for (OProperty oProperty : oPropertyCollection) {
                System.out.println("    " + oProperty.getName() + "\t" + oProperty.getType());
            }
        }
    }

    public static void getShpStructure(String shpFile) throws IOException {
        File file = new File(shpFile);
        FileDataStore dataStore = FileDataStoreFinder.getDataStore(file);
        SimpleFeatureSource featureSource = dataStore.getFeatureSource();
        SimpleFeatureType featureType = featureSource.getSchema();
        GeometryDescriptor geometryDescriptor = featureType.getGeometryDescriptor();

        CoordinateReferenceSystem referenceSystem = geometryDescriptor.getCoordinateReferenceSystem();

        System.out.println("CRS of " + shpFile + " is " + referenceSystem.toWKT());
        // System.out.println("CRS of " + shpFile + " is " + referenceSystem);

    }

}

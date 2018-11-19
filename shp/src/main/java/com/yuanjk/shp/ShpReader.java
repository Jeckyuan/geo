package com.yuanjk.shp;

import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.spatial.shape.OPointShapeBuilder;
import org.geotools.data.DataUtilities;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.Query;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.SchemaException;
import org.geotools.geometry.jts.WKTWriter2;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.*;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ShpReader {

    public static void main(String[] args) throws IOException, SchemaException {
        String shpPathStr = "D:\\data\\spatial_data\\ne_10m_admin_1_states_provinces\\ne_10m_admin_1_states_provinces.shp";
        // File shpFile = Paths.get(shpPathStr).toFile();
        //
        // ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
        // Map<String, Serializable> params = new HashMap<>();
        // params.put("url", shpFile.toURI().toURL());
        //
        // ShapefileDataStore newDataStore = (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);
        // String typeName = newDataStore.getTypeNames()[0];
        // System.out.println("feature type name: " + typeName);
        // SimpleFeatureSource featureSource = newDataStore.getFeatureSource(typeName);
        // SimpleFeatureType SHAPE_TYPE = featureSource.getSchema();
        // System.out.println("feature type schema: " + SHAPE_TYPE);

        // featureSource.

        // featureTypes(shpPathStr);
        // SHAPE_TYPE.
        // showShpFile(shpPathStr);
        readShpCotent(shpPathStr);
    }


    public static void featureTypes(String shpPathStr) throws SchemaException, IOException {
        /*
         * We use the DataUtilities class to create a FeatureType that will describe the data in our
         * shapefile.
         *
         * See also the createFeatureType method below for another, more flexible approach.
         */
        final SimpleFeatureType TYPE =
                DataUtilities.createType(
                        "Location",
                        "the_geom:Point:srid=4326,"
                                + // <- the geometry attribute: Point type
                                "name:String,"
                                + // <- a String attribute
                                "number:Integer" // a number attribute
                );
        System.out.println("TYPE:" + TYPE);

        ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();

        Map<String, Serializable> params = new HashMap<>();


        /*
         * Get an output file name and create the new shapefile
         */
        File newFile = Paths.get(shpPathStr).toFile();
        params.put("url", newFile.toURI().toURL());
        params.put("create spatial index", Boolean.TRUE);

        ShapefileDataStore newDataStore = (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);

        /*
         * TYPE is used as a template to describe the file contents
         */
        newDataStore.createSchema(TYPE);

        /*
         * Write the features to the shapefile
         */
        // Transaction transaction = new DefaultTransaction("create");

        String typeName = newDataStore.getTypeNames()[0];
        SimpleFeatureSource featureSource = newDataStore.getFeatureSource(typeName);
        SimpleFeatureType SHAPE_TYPE = featureSource.getSchema();
        /*
         * The Shapefile format has a couple limitations:
         * - "the_geom" is always first, and used for the geometry attribute name
         * - "the_geom" must be of type Point, MultiPoint, MuiltiLineString, MultiPolygon
         * - Attribute names are limited in length
         * - Not all data types are supported (example Timestamp represented as Date)
         *
         * Each data store has different limitations so check the resulting SimpleFeatureType.
         */
        System.out.println("SHAPE:" + SHAPE_TYPE);

    }


    public static void showShpFile(String filePathStr) {
        ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
        try {
            ShapefileDataStore sds = (ShapefileDataStore) dataStoreFactory.createDataStore(new File(filePathStr).toURI().toURL());
            // sds.setCharset(Charset.forName("GBK"));
            SimpleFeatureSource featureSource = sds.getFeatureSource();
            SimpleFeatureIterator itertor = featureSource.getFeatures().features();

            System.out.println(featureSource.getInfo());
            while (itertor.hasNext()) {
                SimpleFeature feature = itertor.next();
                Iterator<Property> it = feature.getProperties().iterator();

                while (it.hasNext()) {
                    Property pro = it.next();
                    System.out.println(pro);
                }
            }
            itertor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void readShpCotent(String shpFile) throws IOException {
        File file = new File(shpFile);
        FileDataStore myData = FileDataStoreFinder.getDataStore(file);
        SimpleFeatureSource featureSource = myData.getFeatureSource();
        SimpleFeatureType featureType = featureSource.getSchema();

        GeometryDescriptor geometryDescriptor = featureType.getGeometryDescriptor();

        System.out.println("geometryDescriptor.getName()=" + geometryDescriptor.getName() + ", geometryDescriptor.getType().getName()=" + geometryDescriptor.getType().getName() + ", geometryDescriptor.getType().getBinding()=" + geometryDescriptor.getType().getBinding());


        List<AttributeDescriptor> attributeDescriptorList = featureType.getAttributeDescriptors();
        StringBuilder stringBuilder = new StringBuilder();
        for (AttributeDescriptor attributeDescriptor : attributeDescriptorList) {
            stringBuilder.append(attributeDescriptor.getName()).append("=").append(attributeDescriptor.getType().getBinding().getSimpleName()).append("\n");
        }
        System.out.println("Descriptor schema definition: " + stringBuilder);

        List<AttributeType> attributeTypes = featureType.getTypes();
        stringBuilder = new StringBuilder();
        for (AttributeType attributeType : attributeTypes) {
            stringBuilder.append(attributeType.getName()).append("=").append(attributeType.getBinding().toString()).append(";");
        }
        System.out.println("Schema definition: " + stringBuilder);

        Query query = new Query(featureType.getTypeName());
        query.setMaxFeatures(1);

        FeatureCollection<SimpleFeatureType, SimpleFeature> collection = featureSource.getFeatures(query);
        try (FeatureIterator<SimpleFeature> features = collection.features()) {
            while (features.hasNext()) {
                SimpleFeature feature = features.next();
                GeometryAttribute geometryAttribute = feature.getDefaultGeometryProperty();
                GeometryType geometryType = geometryAttribute.getType();
                System.out.println("GeoAttribute type: " + geometryType.getBinding().getName());
                System.out.println("GeoAttribute content WKT: " + (new WKTWriter2()).write((Geometry) feature.getDefaultGeometry()));

                System.out.println(feature.getID() + ": ");
                for (Property attribute : feature.getProperties()) {
                    System.out.println("\t" + attribute.getName() + ":" + attribute.getValue());
                }
            }
        }
    }

}

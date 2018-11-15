import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.spatial.shape.OPointShapeBuilder;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.WKTWriter2;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryType;

import java.io.File;
import java.io.IOException;
import java.util.List;

@SuppressWarnings("Duplicates")
public class ShpLoader {

    public static void main(String[] args) throws IOException {
        ODatabaseDocumentTx database = new ODatabaseDocumentTx("remote:localhost/spatial").open("root", "wyc");
        String shpPathStr = "D:\\data\\spatial_data\\italy-points-shape\\points.shp";

        database.begin();
        try {
            // YOUR CODE
            // createDocument(database, shpPathStr);
            contentInster(database, shpPathStr, "test_load_point");
            database.commit();
        } finally {
            database.close();
        }

    }

    public static void createDocument(ODatabaseDocumentTx database, String shpFile) throws IOException {
        File file = new File(shpFile);
        FileDataStore myData = FileDataStoreFinder.getDataStore(file);
        SimpleFeatureSource source = myData.getFeatureSource();
        SimpleFeatureType schema = source.getSchema();

        List<AttributeDescriptor> attributeDescriptorList = schema.getAttributeDescriptors();
        StringBuilder stringBuilder = new StringBuilder();
        OClass createSchema = database.getMetadata().getSchema().createClass("test_class_a");
        for (AttributeDescriptor attributeDescriptor : attributeDescriptorList) {
            stringBuilder.append(attributeDescriptor.getName()).append("=").append(attributeDescriptor.getType().getBinding().getSimpleName()).append("\n");
            createSchema.createProperty(attributeDescriptor.getName().toString(), OType.getTypeByClass(attributeDescriptor.getType().getBinding()));
        }
        System.out.println("Descriptor schema definition: " + stringBuilder);

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


    public static void contentInster(ODatabaseDocumentTx database, String shpFile, String className) throws IOException {
        File file = new File(shpFile);
        FileDataStore dataStore = FileDataStoreFinder.getDataStore(file);
        SimpleFeatureSource featureSource = dataStore.getFeatureSource();
        SimpleFeatureType featureType = featureSource.getSchema();
        Query query = new Query(featureType.getTypeName());
        query.setMaxFeatures(3);
        FeatureCollection<SimpleFeatureType, SimpleFeature> collection = featureSource.getFeatures(query);

        try (FeatureIterator<SimpleFeature> features = collection.features()) {
            while (features.hasNext()) {
                StringBuilder builder = new StringBuilder();
                builder.append("INSERT INTO  ").append(className).append(" SET ");

                SimpleFeature feature = features.next();
                //add fid property
                builder.append(" fid = '").append(feature.getID()).append("' ");
                //add geometry property
                Geometry geometry = (Geometry) feature.getDefaultGeometry();
                builder.append(", the_geom = St_GeomFromText(\"").append((new WKTWriter2()).writeFormatted(geometry)).append("\") ");
                for (Property attribute : feature.getProperties()) {
                    // System.out.println(attribute.getType().getBinding().getSimpleName() + "\t" + attribute.getName() + ":" + attribute.getValue());
                }

                System.out.println(builder.toString());
               database.command(new OCommandSQL(builder.toString())).execute();
                System.out.println("insert result: " );
            }
        }
    }

}

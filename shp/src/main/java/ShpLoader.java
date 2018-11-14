import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ShpLoader {

    public static void main(String[] args) throws IOException {
        ODatabaseDocumentTx database = new ODatabaseDocumentTx("remote:localhost/spatial").open("root", "wyc");
        String shpPathStr = "D:\\data\\spatial_data\\ne_10m_admin_1_states_provinces\\ne_10m_admin_1_states_provinces.shp";

        database.begin();
        try {
            // YOUR CODE
            createDocument(database, shpPathStr);
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

}

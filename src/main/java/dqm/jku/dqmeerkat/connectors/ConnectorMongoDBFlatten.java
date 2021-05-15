package dqm.jku.dqmeerkat.connectors;

import com.mongodb.ConnectionString;
import com.mongodb.client.*;
import dqm.jku.dqmeerkat.dsd.DSDFactory;
import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.elements.Concept;
import dqm.jku.dqmeerkat.dsd.elements.Datasource;
import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.util.Constants;
import dqm.jku.dqmeerkat.util.Miscellaneous;
import dqm.jku.dqmeerkat.util.converters.DataTypeConverter;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;

public class ConnectorMongoDBFlatten extends DSConnector {

    private String DBUrl;
    private String DBName;
    private MongoClient mongoClient;
    private MongoDatabase database;

    private int currentAttributeOrdinalPosition;

    private static int DEFAULT_PORT = 27017;
    private static String DEFAULT_USER_DB = "admin"; // userDBName is the database in which the userm which credentials are used, resides

    private static final String SEPARATOR = "__";

    public ConnectorMongoDBFlatten(String DBUrl, int DBPort, String DBName, String DBuser, String DBpw, String userDBName) {
        ConnectionString cs = new ConnectionString("mongodb://" + DBuser + ":" + DBpw + "@" + DBUrl + ":" + DBPort + "/" + userDBName);
        this.mongoClient = MongoClients.create(cs);
        this.database = mongoClient.getDatabase(DBName);
        this.DBUrl = DBUrl;
        this.DBName = DBName;
    }

    public ConnectorMongoDBFlatten(String DBUrl, int DBPort, String DBName, String DBuser, String DBpw) {
        this(DBUrl, DBPort, DBName, DBuser, DBpw, DBName);
    }

    public ConnectorMongoDBFlatten(String DBUrl, String DBName, String DBuser, String DBpw) throws UnknownHostException {
        this(DBUrl, DEFAULT_PORT, DBName, DBuser, DBpw, DEFAULT_USER_DB);
    }

    public ConnectorMongoDBFlatten(String DBUrl, int DBPort) {
        ConnectionString cs = new ConnectionString("mongodb://" + DBUrl + ":" + DBPort);
        this.mongoClient = MongoClients.create(cs);
        this.database = mongoClient.getDatabase(DBName);
        this.DBUrl = DBUrl;
    }

    public ConnectorMongoDBFlatten(String DBUrl) {
        this(DBUrl, DEFAULT_PORT);
    }

    @Override
    public Datasource loadSchema() throws IOException {
        return loadSchema(Constants.DEFAULT_URI, Constants.DEFAULT_PREFIX);
    }

    @Override
    public Datasource loadSchema(String uri, String prefix) throws IOException {
        Datasource ds = DSDFactory.makeDatasource(DBName, Miscellaneous.DBType.MONGODB, uri, prefix);

        for (String collectionName : database.listCollectionNames()) {
            Concept c = DSDFactory.makeConcept(collectionName, ds);
            loadAttributes(c);
        }
        return ds;
    }

    @Override
    public Iterator<Record> getRecords(Concept concept) throws IOException {
        final int size = getNrRecords(concept);

        return new Iterator<Record>() {
            private static final int bufferSize = 1000;
            private int count = 0;

            private Record[] buffer = new Record[bufferSize];
            private MongoCursor<Document> mongodbCursor = database.getCollection(concept.getLabelOriginal()).find().iterator();

            @Override
            public boolean hasNext() {
                return count < size;
            }

            @Override
            public Record next() {
                if (count % bufferSize == 0) {
                    try {
                        fillBuffer();
                    } catch (SQLException | ParseException e) {
                        e.printStackTrace();
                    }
                }
                return buffer[count++ % bufferSize];
            }

            private void fillBuffer() throws SQLException, ParseException {
                int i = 0;
                while (mongodbCursor.hasNext() && i < bufferSize) {
                    Record r = new Record(concept);
                    Document doc = mongodbCursor.next();


                    for (Attribute a : concept.getAttributes()) {
                        String[] nameParts = a.getLabel().split(SEPARATOR);
                        Object value = doc.get(nameParts[0]);
                        int j = 1;
                        while (j < nameParts.length && (value instanceof Document || value instanceof ArrayList)) {
                            if (value instanceof Document) {
                                value = ((Document) value).get(nameParts[j]);
                                j++;
                            } else if (value instanceof ArrayList) {
                                int index = Integer.parseInt(nameParts[j]);
                                if (index < ((ArrayList) value).size()) {
                                    value = ((ArrayList) value).get(index);
                                } else {
                                    j = nameParts.length;
                                }
                                j++;
                            }
                        }
                        if (value != null) {
                            r.addValue(a, value);
                        }
                    }
                    buffer[i++] = r;
                }
            }

        };
    }


    @Override
    public int getNrRecords(Concept concept) throws IOException {
        return (int) this.database.getCollection(concept.getLabelOriginal()).countDocuments();
    }

    @Override
    public RecordList getRecordList(Concept concept) throws IOException {
        Iterator<Record> rIt = getRecords(concept);
        RecordList rs = new RecordList();
        while (rIt.hasNext()) {
            rs.addRecord(rIt.next());
        }
        return rs;
    }

    @Override
    public RecordList getPartialRecordList(Concept concept, int offset, int noRecords) throws IOException {
        Iterator<Record> rIt = getRecords(concept);
        RecordList rs = new RecordList();
        int i = 0;
        while (rIt.hasNext() && i < offset) {
            rIt.next();
            i++;
        }
        i = 0;
        while (rIt.hasNext() && i < noRecords) {
            rs.addRecord(rIt.next());
            i++;
        }
        return rs;
    }


    private void loadAttributes(Concept concept) {
        MongoCollection<Document> collection = database.getCollection(concept.getLabelOriginal());

        //for(Document doc : collection.find()){
        Document doc = collection.find().first();
        this.currentAttributeOrdinalPosition = 0;
        handleDocument(doc, concept, "");
        //}

    }

    private void handleDocument(Document doc, Concept concept, String namePrefix) {
        for (String attributeName : doc.keySet()) {
            Object value = doc.get(attributeName);
            if (value != null) {

                if (value instanceof Document) {
                    handleDocument((Document) value, concept, namePrefix + attributeName + SEPARATOR);
                } else if (value instanceof ArrayList) {
                    handleArray((ArrayList) value, concept, namePrefix + attributeName + SEPARATOR);
                } else {
                    Attribute a = makeAttribute(namePrefix + attributeName, concept, this.currentAttributeOrdinalPosition++);
                    a.setDataType(value.getClass());
                    if (value != null) {
                        DataTypeConverter.getTypeFromMongoDB(a, value.getClass());
                        if (value instanceof ObjectId) {
                            a.setNullable(false);
                            a.setUnique(true);
                        }
                    }
                }
            }
        }
    }

    private void handleArray(ArrayList list, Concept concept, String namePrefix) {
        for (int j = 0; j < list.size(); j++) {
            Object value = list.get(j);
            if (value != null) {
                if (value instanceof Document) {
                    handleDocument((Document) value, concept, namePrefix + j + SEPARATOR);
                } else if (value instanceof ArrayList) {
                    handleArray((ArrayList) value, concept, namePrefix + j + SEPARATOR);
                } else {
                    Attribute a = makeAttribute(namePrefix + j, concept, this.currentAttributeOrdinalPosition++);
                    a.setDataType(value.getClass());
                    if (value != null) {
                        DataTypeConverter.getTypeFromMongoDB(a, value.getClass());
                        if (value instanceof ObjectId) {
                            a.setNullable(false);
                            a.setUnique(true);
                        }
                    }
                }
            }
        }
    }

    private Attribute makeAttribute(String attributeName, Concept concept, int ordinalPosition) {
        Attribute a = DSDFactory.makeAttribute(attributeName, concept);
        a.setOrdinalPosition(ordinalPosition);
        a.setNullable(true);
        a.setUnique(false);
        a.setAutoIncrement(false);
        return a;
    }

    private Attribute makeAttribute(String attributeName, Concept concept, int ordinalPosition, boolean key) {
        Attribute a = makeAttribute(attributeName, concept, ordinalPosition);
        if (key) {
            a.setNullable(false);
            a.setUnique(true);
            a.setDataType(Byte.class);
            concept.addPrimaryKeyAttribute(a);
        }
        return a;
    }


    public static void main(String[] args) {

        try {
            ConnectorMongoDBFlatten connector = new ConnectorMongoDBFlatten("localhost", "sample_restaurants", "admin", "adminpw");

            Datasource ds = connector.loadSchema();
            for (Concept c : ds.getConcepts()) {
                // get first record
                Iterator<Record> it = connector.getRecords(c);
                if (it.hasNext()) {
                    Record r = it.next();
                    if (r != null) {
                        for (Attribute a : r.getFields().getAttributes()) {
                            System.out.println(r.getFields().get(a.getLabel()) + "\t" + a.getDataType() + "\t" + r.getField(a.getLabel()));
                        }
                    } else {
                        System.out.println(c.getLabel() + " is null!");
                    }
                }
                /*
                // get ALL records.
                for (Iterator<Record> it = connector.getRecords(c); it.hasNext(); ) {
                    Record r = it.next();
                    if (r != null) {
                        for (Attribute a : r.getFields().getAttributes()) {
                            System.out.println(r.getFields().get(a.getLabel()) + "\t" + a.getDataType() + "\t" + r.getField(a.getLabel()));
                        }
                    } else {
                        System.out.println(c.getLabel() + " is null!");
                    }

                }*/
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
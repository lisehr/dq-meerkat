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

public class ConnectorMongoDB extends DSConnector {

    private String DBUrl;
    private String DBName;
    private MongoClient mongoClient;
    private MongoDatabase database;

    private static int DEFAULT_PORT = 27017;
    private static String DEFAULT_USER_DB = "admin"; // userDBName is the database in which the user which credentials are used, resides

    private static final String SEPARATOR = "__";
    private static final String ARRAY_SUFFIX = "ARRAY";
    private static final String DOC_SUFFIX = "DOC";
    private static final String ID_FIELD = "_id";
    private static final String DOC_ID_FIELD = "parent_id";
    private static final String ARRAY_INDEX_FIELD = "index";
    private static final String ARRAY_VALUE_FIELD = "value";


    public ConnectorMongoDB(String DBUrl, int DBPort, String DBName, String DBuser, String DBpw, String userDBName) {
        ConnectionString cs = new ConnectionString("mongodb://" + DBuser + ":" + DBpw + "@" + DBUrl + ":" + DBPort + "/" + userDBName);
        this.mongoClient = MongoClients.create(cs);
        this.database = mongoClient.getDatabase(DBName);
        this.DBUrl = DBUrl;
        this.DBName = DBName;
    }

    public ConnectorMongoDB(String DBUrl, int DBPort, String DBName, String DBuser, String DBpw) {
        this(DBUrl, DBPort, DBName, DBuser, DBpw, DBName);
    }

    public ConnectorMongoDB(String DBUrl, String DBName, String DBuser, String DBpw) throws UnknownHostException {
        this(DBUrl, DEFAULT_PORT, DBName, DBuser, DBpw, DEFAULT_USER_DB);
    }

    public ConnectorMongoDB(String DBUrl, int DBPort) {
        ConnectionString cs = new ConnectionString("mongodb://" + DBUrl + ":" + DBPort);
        this.mongoClient = MongoClients.create(cs);
        this.database = mongoClient.getDatabase(DBName);
        this.DBUrl = DBUrl;
    }

    public ConnectorMongoDB(String DBUrl) {
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
                        Object value = doc.get(a.getLabelOriginal());
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
        Document doc = collection.find().first();
        handleDocument(doc, concept);
    }

    private void handleDocument(Document doc, Concept concept) {
        int i = 0;
        for (String attributeName : doc.keySet()) {
            Object value = doc.get(attributeName);
            if (value != null) {
                Attribute a = makeAttribute(attributeName, concept, i++);
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

    public void createRelationalDB(String DBUrl, int DBPort, String DBName, String DBuser, String DBpw, String userDBName) {
        ConnectionString cs = new ConnectionString("mongodb://" + DBuser + ":" + DBpw + "@" + DBUrl + ":" + DBPort + "/" + userDBName);
        MongoClient relationalClient = MongoClients.create(cs);
        MongoDatabase relationalDatabase = relationalClient.getDatabase(DBName);
        relationalDatabase.drop();
        for (String collectionName : database.listCollectionNames()) {
            MongoCollection<Document> sourceCollection = database.getCollection(collectionName);
            if (collectionExists(collectionName, relationalDatabase)) {
                relationalDatabase.getCollection(collectionName).drop();
            }
            relationalDatabase.createCollection(collectionName);

            MongoCollection<Document> destCollection = relationalDatabase.getCollection(collectionName);
            for (Document doc : sourceCollection.find()) {
                //Document doc = sourceCollection.find().first();
                createRelationalDocument(doc, null, collectionName, relationalDatabase);
            }

        }
        this.database=relationalDatabase;
        this.mongoClient=relationalClient;
    }

    private void createRelationalDocument(Document doc, Document parentDoc, String collectionName, MongoDatabase database) {
        Document newDoc = new Document();
        MongoCollection<Document> collection = database.getCollection(collectionName);

        if (parentDoc != null) {
            ObjectId id = new ObjectId();
            newDoc.append(ID_FIELD, id);
            newDoc.append(DOC_ID_FIELD, parentDoc.get(ID_FIELD));
        }

        for (String key : doc.keySet()) {
            Object value = doc.get(key);
            if (value instanceof Document) {
                String newCollectionName = collectionName + SEPARATOR + key;
                createRelationalDocument((Document) value, newDoc, newCollectionName, database);
            } else if (value instanceof ArrayList) {
                String newCollectionName = collectionName + SEPARATOR + key;
                createRelationalArray((ArrayList) value, newDoc, newCollectionName, database);
            } else {
                newDoc.append(key, value);
            }
        }
        collection.insertOne(newDoc);
    }

    private void createRelationalArray(ArrayList list, Document parentDoc, String collectionName, MongoDatabase database) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        int arrayIndex = 0;

        for (Object value : list) {
            Document newDoc = new Document();
            if (parentDoc != null) {
                ObjectId id = new ObjectId();
                newDoc.append(ID_FIELD, id);
                newDoc.append(DOC_ID_FIELD, parentDoc.get(ID_FIELD));
                newDoc.append(ARRAY_INDEX_FIELD, arrayIndex++);
            }
            if (value instanceof Document) {
                String newCollectionName = collectionName + SEPARATOR + DOC_SUFFIX;
                createRelationalDocument((Document) value, newDoc, newCollectionName, database);
            } else if (value instanceof ArrayList) {
                String newCollectionName = collectionName + SEPARATOR + ARRAY_SUFFIX;
                createRelationalArray((ArrayList) value, newDoc, newCollectionName, database);
            } else {
                newDoc.append(ARRAY_VALUE_FIELD, value);
            }
            collection.insertOne(newDoc);
        }

    }

    private static boolean collectionExists(String collectionName, MongoDatabase database) {
        for (final String name : database.listCollectionNames()) {
            if (name.equalsIgnoreCase(collectionName)) {
                return true;
            }
        }
        return false;
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
            ConnectorMongoDB connector = new ConnectorMongoDB("localhost", "sample_restaurants", "admin", "adminpw");
            connector.createRelationalDB("localhost", 27017, "sample_restaurants_relational", "admin", "adminpw", "admin");
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
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
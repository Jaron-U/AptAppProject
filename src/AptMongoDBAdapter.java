import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class AptMongoDBAdapter {
    private MongoClient mongoClient = null;
    private static String conStr = "mongodb+srv://jianglongyu:Password123@cluster0.0qmt6kt.mongodb.net";

    private MongoDatabase database;
    public void connect() {
        if (mongoClient != null)
            mongoClient.close();
        mongoClient = new MongoClient(new MongoClientURI(conStr));
        this.database = mongoClient.getDatabase("apartments_db");
    }

    public Apartment getAptById(String id) {
        connect();
        MongoCollection<Document> collection = database.getCollection("apartments");
        Apartment apartment = null;
        try {
            Document doc = collection.find(Filters.eq("_id", new ObjectId(id))).first();
            if (doc != null) {
                apartment = new Apartment();
                apartment.setAptName(doc.getString("aptName"));
                apartment.setPosterID(doc.getInteger("posterID"));
                apartment.setAddress(doc.getString("address"));
                apartment.setArea(doc.getDouble("area"));
                apartment.setPrice(doc.getDouble("price"));
                apartment.setAvailableDate(doc.getString("availableDate"));
                apartment.setType(doc.getString("type"));
                apartment.setDescr(doc.getString("Descr"));
            }
        } finally {
            mongoClient.close();
        }
        return apartment;
    }

    public List<Apartment> getAllApts() {
        connect();
        MongoCollection<Document> collection = database.getCollection("apartments");
        List<Apartment> apartments = new ArrayList<>();

        try {
            FindIterable<Document> documents = collection.find();
            for (Document doc : documents) {
                Apartment apt = new Apartment();
                apt.setAptName(doc.getString("aptName"));
                apt.setPosterID(doc.getInteger("posterID"));
                apt.setAddress(doc.getString("address"));
                apt.setArea(doc.getDouble("area"));
                apt.setPrice(doc.getDouble("price"));
                apt.setAvailableDate(doc.getString("availableDate"));
                apt.setType(doc.getString("type"));
                apt.setDescr(doc.getString("descr"));
                apartments.add(apt);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return apartments;
    }

    public String createApt(Apartment apartment) {
        connect();
        MongoCollection<Document> collection = database.getCollection("apartments");
        String id = null;
        Document doc = new Document()
                .append("aptName", apartment.getAptName())
                .append("posterID", apartment.getPosterID())
                .append("address", apartment.getAddress())
                .append("area", apartment.getArea())
                .append("price", apartment.getPrice())
                .append("availableDate", apartment.getAvailableDate())
                .append("type", apartment.getType())
                .append("descr", apartment.getDescr());

        try {
            collection.insertOne(doc);
            id = doc.getObjectId("_id").toString();
        } catch (Exception e) {
            mongoClient.close();
            System.out.println("Insert failed: " + e.getMessage());
        }
        mongoClient.close();

        return id;
    }

    public boolean updateApartment(String id, Apartment apartment) {
        connect();
        MongoCollection<Document> collection = database.getCollection("apartments");

        ObjectId objectId = new ObjectId(id);

        Document found = collection.find(Filters.eq("_id", objectId)).first();
        if (found == null) {
            mongoClient.close();
            return false;
        }

        Bson updateOperation = Updates.combine(
                Updates.set("aptName", apartment.getAptName()),
                Updates.set("posterID", apartment.getPosterID()),
                Updates.set("address", apartment.getAddress()),
                Updates.set("area", apartment.getArea()),
                Updates.set("price", apartment.getPrice()),
                Updates.set("availableDate", apartment.getAvailableDate()),
                Updates.set("type", apartment.getType()),
                Updates.set("descr", apartment.getDescr())
        );

        UpdateResult result = collection.updateOne(Filters.eq("_id", objectId), updateOperation);

        mongoClient.close();
        return result.getModifiedCount() > 0;
    }

    public List<Apartment> searchAptByPrice(double lowPrice, double highPrice) {
        List<Apartment> apartments = new ArrayList<>();
        connect();
        MongoCollection<Document> collection = database.getCollection("apartments");

        Bson priceFilter = Filters.and(
                Filters.gte("price", lowPrice),
                Filters.lte("price", highPrice)
        );

        try {
            FindIterable<Document> documents = collection.find(priceFilter);

            for (Document doc : documents) {
                Apartment apt = new Apartment();
                apt.setAptName(doc.getString("aptName"));
                apt.setPosterID(doc.getInteger("posterID"));
                apt.setAddress(doc.getString("address"));
                apt.setArea(doc.getDouble("area"));
                apt.setPrice(doc.getDouble("price"));
                apt.setAvailableDate(doc.getString("availableDate"));
                apt.setType(doc.getString("type"));
                apt.setDescr(doc.getString("descr"));
                apartments.add(apt);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        mongoClient.close();

        return apartments;
    }

    public List<Apartment> searchAptsByType(String type) {
        List<Apartment> apartments = new ArrayList<>();
        connect();
        MongoCollection<Document> collection = database.getCollection("apartments");

        Bson typeFilter = Filters.eq("type", type);
        try {
            FindIterable<Document> documents = collection.find(typeFilter);
            for (Document doc : documents) {
                Apartment apt = new Apartment();
                apt.setAptName(doc.getString("aptName"));
                apt.setPosterID(doc.getInteger("posterID"));
                apt.setAddress(doc.getString("address"));
                apt.setArea(doc.getDouble("area"));
                apt.setPrice(doc.getDouble("price"));
                apt.setAvailableDate(doc.getString("availableDate"));
                apt.setType(doc.getString("type"));
                apt.setDescr(doc.getString("descr"));

                apartments.add(apt);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        mongoClient.close();

        return apartments;
    }
}



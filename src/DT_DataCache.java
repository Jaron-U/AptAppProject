import java.util.*;

public class DT_DataCache {
    HashMap<Integer, User> userCache = new HashMap<Integer, User>();
    HashMap<String, Apartment> aptCache = new HashMap<String, Apartment>();

    public void c_addUser(User user) {
        System.out.println("saving to cache");
        if (user == null) {
            return;
        }
        if (!userCache.containsKey(user.userID)) {
            userCache.put(user.userID, user);
        }
    }

    public User c_loadUser(int userID) {
        System.out.println("checking cache");
        if (userCache.containsKey(userID)) {
            System.out.println("Found");
            return userCache.get(userID);
        }
        return null;
    }

    public void c_addApt(Apartment apt) {
        System.out.println("saving to cache");
        if (apt == null) {
            return;
        }
        if (!aptCache.containsKey(apt.getId())) {
            aptCache.put(apt.getId(), apt);
        }
    }

    public Apartment c_loadApt(String aptID) {
        System.out.println("checking cache");
        if (aptCache.containsKey(aptID)) {
            System.out.println("Found");
            return aptCache.get(aptID);
        }
        return null;
    }
}

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.awt.SystemTray;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.Socket;
import java.sql.*;
import java.util.*;

import org.json.JSONObject;

public class DT_DataAdaptor {
    Gson gson = new Gson();
    Socket s = null;
    DataInputStream dis = null;
    DataOutputStream dos = null;
    ServiceManager serviceMan = new ServiceManager();
    DT_DataCache _cache = new DT_DataCache();

    public void Conn() {
        // Set up service manager
        serviceMan.addService(ServiceInfoModel.SERVICE_USER_LOGIN);
        serviceMan.addService(ServiceInfoModel.SERVICE_USER_LOAD);
        serviceMan.addService(ServiceInfoModel.SERVICE_USER_SAVE);

        serviceMan.addService(ServiceInfoModel.SERVICE_WISHLIST_LOAD);
        serviceMan.addService(ServiceInfoModel.SERVICE_WISHLIST_ADD);
        serviceMan.addService(ServiceInfoModel.SERVICE_WISHLIST_DELETE);

        serviceMan.addService(ServiceInfoModel.SERVICE_APT_LOADALL);
        serviceMan.addService(ServiceInfoModel.SERVICE_APT_LOAD);
        serviceMan.addService(ServiceInfoModel.SERVICE_APT_SAVE);
        serviceMan.addService(ServiceInfoModel.SERVICE_APT_SEARCH_PRICE);
        serviceMan.addService(ServiceInfoModel.SERVICE_APT_SEARCH_TYPE);

        serviceMan.discoverAll();
    }

    private String getServiceAddress(int serviceCode) {
        ServiceInfoModel info = serviceMan.getServiceInfo(serviceCode);
        if (info != null) {
            return info.serviceHostAddress;
        } else
            return null;
    }

    public User loadUser(String username, String password) {
        System.out.println("===============================================");
        System.out.println("Logging in with:");
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        String srvcAddr = getServiceAddress(ServiceInfoModel.SERVICE_USER_LOGIN);
        if (srvcAddr == null) {
            System.err.println("Service not found");
            return null;
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        String response = ServiceManager.sendJSONReq(srvcAddr, gson.toJson(user), "GET");
        if (response == null) {
            System.err.println("No response");
            return null;
        }
        try {
            User tmp = gson.fromJson(response, User.class);
            if (tmp.getUserID() == -1) {
                System.err.println("No user");
                return null;
            }
            System.out.println("Login success");
            // Set all fields
            user.setUserID(tmp.getUserID());
            System.out.println(user.getUserID());

            user.setFullName(tmp.getFullName());
            System.out.println(user.getFullName());

            user.setEmail(tmp.getEmail());
            System.out.println(user.getEmail());

            user.setWishListID(tmp.getWishListID());
            System.out.println(user.getWishListID());

            return user;
        } catch (Exception e) {
            System.out.println("Parsing issue");
            return null;
        }
    }

    public boolean saveUser(User user) {
        System.out.println("===============================================");
        System.out.println("Registering user");
        String srvcAddr = getServiceAddress(ServiceInfoModel.SERVICE_USER_SAVE);
        if (srvcAddr == null) {
            System.err.println("Service not found");
            return false;
        }
        String response = ServiceManager.sendJSONReq(srvcAddr, gson.toJson(user), "GET");
        if (response == null) {
            System.err.println("No response");
            return false;
        }
        try {
            User tmp = gson.fromJson(response, User.class);
            if (tmp.getUserID() == -1) {
                System.err.println("No user");
                return false;
            }
            System.out.println("Register success");
            // Set all fields
            user.setUserID(tmp.getUserID());
            System.out.println(user.getUserID());
            return true;
        } catch (Exception e) {
            System.out.println("Parsing issue");
            return false;
        }
    }

    public User loadUserByID(int ID) {
        System.out.println("===============================================");
        System.out.println("Loading user by ID: " + ID);
        // Check cache return if exist
        User stored = _cache.c_loadUser(ID);
        if (stored != null) {
            return stored;
        }
        // Otherwise ,get from remote
        String srvcAddr = getServiceAddress(ServiceInfoModel.SERVICE_USER_LOAD);
        if (srvcAddr == null) {
            System.err.println("Service not found");
            return null;
        }
        String response = ServiceManager.sendJSONReq(srvcAddr, Integer.toString(ID), "GET");
        if (response == null) {
            System.err.println("No response");
            return null;
        }

        try {
            User tmp = gson.fromJson(response, User.class);
            if (tmp.getUserID() == -1) {
                System.err.println("No user");
                return null;
            }
            System.out.println("Load success");
            System.out.println(tmp.userID);
            System.out.println(tmp.username);
            System.out.println(tmp.email);
            // Save to cache
            _cache.c_addUser(tmp);

            return tmp;
        } catch (Exception e) {
            System.out.println("Parsing issue");
            return null;
        }

    }

    public List<Apartment> loadAptList() {
        System.out.println("===============================================");
        ArrayList<Apartment> empty = new ArrayList<Apartment>();
        System.out.println("Loading all apartments");
        String srvcAddr = getServiceAddress(ServiceInfoModel.SERVICE_APT_LOADALL);
        if (srvcAddr == null) {
            System.err.println("Service not found");
            return empty;
        }
        String res = ServiceManager.sendDirectRequest(srvcAddr, "GET");
        if (res == null) {
            System.err.println("No response");
            return empty;
        }
        try {

            Type listType = new TypeToken<ArrayList<Apartment>>() {
            }.getType();
            // Convert JSON to ArrayList
            ArrayList<Apartment> aptList = gson.fromJson(res, listType);
            // Save to cache
            for(Apartment apt : aptList){
                _cache.c_addApt(apt);
            }
            return aptList;
        } catch (Exception e) {
            System.out.println("Parsing issue");
            return empty;
        }

    }

    public Apartment loadAptByID(String postID) {
        System.out.println("===============================================");
        System.out.println("Loading Apartment with ID: " + postID);
        // get from cache if exist
        Apartment stored = _cache.c_loadApt(postID);
        if (stored != null) {
            return stored;
        }
        // Otherwise get from remote
        String srvcAddr = getServiceAddress(ServiceInfoModel.SERVICE_APT_LOAD);
        if (srvcAddr == null) {
            System.err.println("Service not found");
            return null;
        }
        String res = ServiceManager.sendDirectRequest(srvcAddr + "/" + postID, "GET");
        if (res == null) {
            System.err.println("No response");
            return null;
        }
        System.out.println(res);
        try {
            Apartment apt = gson.fromJson(res, Apartment.class);
            apt.setId(postID);
            // Add to cache
            // _cache.c_addApt(apt);
            return apt;
        } catch (Exception e) {
            System.out.println("Parsing issue");
            return null;
        }
    }

    public boolean saveApt(Apartment post) {
        System.out.println("===============================================");
        System.out.println("Saving apt");
        String srvcAddr = getServiceAddress(ServiceInfoModel.SERVICE_APT_SAVE);
        if (srvcAddr == null) {
            System.err.println("Service not found");
            return false;
        }
        // Parse apartment info and send it out
        String json = gson.toJson(post);
        String res = ServiceManager.sendJSONReq(srvcAddr, json, "POST");
        if (res == null) {
            System.err.println("No response");
            return false;
        }
        System.out.println("Returned ID: " + res);

        try {
            post.setId(res);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Apartment> loadAptByPrice(double min, double max) {
        System.out.println("===============================================");
        System.out.println("Loading apt by price: " + min + " - " + max);
        List<Apartment> aptList = new ArrayList<Apartment>();
        String srvcAddr = getServiceAddress(ServiceInfoModel.SERVICE_APT_SEARCH_PRICE);
        if (srvcAddr == null) {
            System.err.println("Service not found");
            return aptList;
        }
        // Create request and send it
        JSONObject req = new JSONObject();
        req.put("lowPrice", min);
        req.put("highPrice", max);
        String json = req.toString();
        String res = ServiceManager.sendJSONReq(srvcAddr, json, "GET");
        if (res == null) {
            System.err.println("No response");
            return aptList;
        }
        try {
            System.out.println("Got:");
            System.out.println(res);
            Type listType = new TypeToken<ArrayList<Apartment>>() {
            }.getType();
            // Convert JSON to ArrayList
            aptList = gson.fromJson(res, listType);
            return aptList;
        } catch (Exception e) {
            System.out.println("Parsing issue");
            return aptList;
        }
    }

    public List<Apartment> loadAptByType(String type) {
        System.out.println("===============================================");
        System.out.println("Loading apt by type: " + type);
        List<Apartment> aptList = new ArrayList<Apartment>();

        String srvcAddr = getServiceAddress(ServiceInfoModel.SERVICE_APT_SEARCH_TYPE);
        if (srvcAddr == null) {
            System.err.println("Service not found");
            return aptList;
        }
        // Create request and send it
        String res = ServiceManager.sendDirectRequest(srvcAddr + "/" + type, "GET");
        if (res == null) {
            System.err.println("No response");
            return aptList;
        }
        // Parse
        try {
            System.out.println("Got:");
            System.out.println(res);
            Type listType = new TypeToken<ArrayList<Apartment>>() {
            }.getType();
            // Convert JSON to ArrayList
            aptList = gson.fromJson(res, listType);
            return aptList;
        } catch (Exception e) {
            System.out.println("Parsing issue");
            return aptList;
        }
    }

    public WishApt saveApt2WishList(WishApt wishApt) {
        System.out.println("===============================================");
        System.out.println("Adding to wishList " + wishApt.userID + " apartment" + wishApt.aptID);
        String srvcAddr = getServiceAddress(ServiceInfoModel.SERVICE_WISHLIST_ADD);
        // Set up request
        String aptID = wishApt.aptID;
        String userID = Integer.toString(wishApt.userID);
        TupleArgs req = new TupleArgs();
        req.arg0 = userID;
        req.arg1 = aptID;

        String res = ServiceManager.sendJSONReq(srvcAddr, gson.toJson(req), "POST");
        if (res == null) {
            System.err.println("No response");
            return null;
        }
        try {
            System.out.println("Got:");
            System.out.println(res);
            TupleArgs tmp = gson.fromJson(res, TupleArgs.class);
            if (tmp.arg0 != "-1") {
                System.err.println("Operation success");
                if (tmp.arg1 != "0") {
                    System.out.println("Inserted");
                } else {
                    System.out.println("Duplicate");
                }
            } else {
                System.out.println("Insert failed");
            }
            return wishApt;
        } catch (Exception e) {
            System.out.println("Parsing issue");
            return null;
        }
    }

    public List<Apartment> loadWishListByUserID(int userID) {
        System.out.println("===============================================");
        System.out.println("Loading wishList of user " + userID);
        List<Apartment> WLApt = new ArrayList<Apartment>();
        List<String> WLID = null;

        String srvcAddrList = getServiceAddress(ServiceInfoModel.SERVICE_WISHLIST_LOAD);
        // String srvcAddrLoad =
        // getServiceAddress(ServiceInfoModel.SERVICE_WISHLIST_ADD);
        if (srvcAddrList == null) {
            return WLApt;
        }
        // First get the ID list
        String req = Integer.toString(userID);

        String res = ServiceManager.sendJSONReq(srvcAddrList, gson.toJson(req), "GET");
        if (res == null) {
            System.err.println("No response");
            return WLApt;
        }
        System.out.println("List:" + res);
        try {
            // Convert JSON to ArrayList
            Type listType = new TypeToken<ArrayList<String>>() {
            }.getType();
            WLID = gson.fromJson(res, listType);

            System.out.println("Loading all apts in wishlist");
            // Then load all the individual apartments
            for (String aptID : WLID) {
                Apartment apt = loadAptByID(aptID);
                if (apt != null) {
                    WLApt.add(apt);
                }
            }
        } catch (Exception e) {
            System.out.println("possible parsing issue");
            return WLApt;
        }

        return WLApt;
    }

}

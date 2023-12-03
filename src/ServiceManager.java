import java.io.*;
import java.net.*;
import java.util.*;

import com.google.gson.Gson;

public class ServiceManager {
    public static Gson gson = new Gson();
    public static String registryURL = "http://localhost:8080/disc";

    private Map<Integer, ServiceInfoModel> _reg = new HashMap<>(); // An internal registry to keep track of services
    private Set<Integer> pendingService = new HashSet<>();

    /**
     * Add service code to the list of services that manager will try to discover
     * when calling discoverAll();
     */
    public void addService(int serviceCode) {
        pendingService.add(serviceCode);
    }

    /**
     * Try to discover all the services added by addService.
     * Usually should be called at initialization
     */
    public void discoverAll() {
        System.out.println("trying to discover all added service ");
        Iterator<Integer> iterator = pendingService.iterator();

        // Iterate through it and discover, if discovered remove from pending
        while (iterator.hasNext()) {
            int serviceCode = iterator.next();
            System.out.print(serviceCode + ", ");
            if (discover(serviceCode)) {
                iterator.remove(); // Remove even numbers from the set
            }
        }
        if (pendingService.isEmpty()) {
            System.out.println("All found");
        } else {
            for (int code : pendingService) {
                System.out.print(code + ", ");
            }
            System.out.print("\n");

        }

    }

    /**
     * Try to get service info of a specific serviceCode
     * If it is not found yet, manager try to discover and return info if
     * discovered.
     */
    public ServiceInfoModel getServiceInfo(int serviceCode) {
        if (_reg.containsKey(serviceCode)) {
            System.out.println("Acquired Service info:");
            System.out.println("Service Code: " + _reg.get(serviceCode).serviceCode);
            System.out.println("Address: " + _reg.get(serviceCode).serviceHostAddress);
            System.out.println("Port: " + _reg.get(serviceCode).serviceHostPort);
            return _reg.get(serviceCode);
        } else {
            // Not found, then try to discover one
            System.out.println("Service of ID " + serviceCode + " not found, discovering");

            // If it is got after discovering return
            if (discover(serviceCode)) {
                System.out.println("Found");
                return _reg.get(serviceCode);
            } else {
                System.out.println("No service of ID " + serviceCode + "discovered, maybe the server is not on");
                return null;
            }

        }
    }

    protected boolean discover(int serviceCode) {

        System.out.println("=======");
        System.out.println("Seeking service code" + serviceCode);
        // out.println(userInput);

        try {
            // Create request object
            ServiceMessageModel request = new ServiceMessageModel();
            request.code = ServiceMessageModel.SERVICE_DISCOVER_REQUEST;
            String serviceCodeStr = gson.toJson(serviceCode);
            request.data = serviceCodeStr;
            // Parser Request object
            String jsonData = gson.toJson(request);
            // Send the request and get response
            String res = sendJSONReq(registryURL, jsonData, "GET");
            if (res == null) {
                System.out.println("network failure");
                return false;
            }

            // Parse response object
            System.out.println("parsing message");
            ServiceMessageModel msg = gson.fromJson(res, ServiceMessageModel.class);
            if (msg.code == ServiceMessageModel.SERVICE_DISCOVER_NOT_FOUND) {
                System.out.println("Service Not found");
                return false;
            }
            System.out.println("parsing info " + msg.data);
            ServiceInfoModel info = gson.fromJson(msg.data, ServiceInfoModel.class);

            System.out.println("Acquired Service info:");
            System.out.println("Service Code: " + info.serviceCode);
            System.out.println("Address: " + info.serviceHostAddress);
            System.out.println("Port: " + info.serviceHostPort);

            // Add to internal registry if match
            if (info.serviceCode == serviceCode) {
                _reg.put(serviceCode, info);
                return true;
            } else {
                System.out.println("Service code mismatch, is " + info.serviceCode + "should be " + serviceCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    /**
     * Send an http request with json body.
     * Return the json response body and print the content.
     */
    public static String sendJSONReq(String urlString, String jsonData, String reqMethod) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod(reqMethod);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            OutputStream outputStream = connection.getOutputStream();

            outputStream.write(jsonData.getBytes("UTF-8"));
            int responseCode = connection.getResponseCode();

            System.out.println("Request made with url:" + url.toString());
            // Check response
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = reader.readLine()) != null) {
                    response.append(inputLine);
                }
                reader.close();

                System.out.println("Response Content: " + response.toString());
                return response.toString();

            } else {
                System.out.println("HTTP GET request failed with response code: " + responseCode);
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String sendDirectRequest(String uString, String methodString) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(uString);
            BufferedReader reader = null;
            // Open a connection to the URL
            connection = (HttpURLConnection) url.openConnection();

            // Set request method
            connection.setRequestMethod(methodString);

            // Get the response code
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // Read the response from the API
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                System.out.println("Response Body: " + response.toString());
                return response.toString();
            } else {
                System.out.println("Error in HTTP request, Response Code: " + responseCode);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

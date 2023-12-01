import java.io.*;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;

/**
 * Base class for micro services
 * 
 */
public class MicroService {
    // These value all needs to be set for service to work
    public static String registryAddr = null;
    public static String serviceAddr = null;
    public static int servicePort = 0;
    public static int serviceCode = 0;

    private static Gson gson = new Gson();

    /**
     * Publish the service, return true if success.
     * Make sure to first set the value of registryAddr, serviceAddr, servicePort,
     * serviceCode
     */
    protected static boolean register() {
        HttpURLConnection connection = null;
        try {
            // Create request object
            ServiceMessageModel request = new ServiceMessageModel();
            request.code = ServiceMessageModel.SERVICE_PUBLISH_REQUEST;
            // Create info object
            ServiceInfoModel info = new ServiceInfoModel();
            info.serviceCode = serviceCode;
            info.serviceHostAddress = serviceAddr;
            info.serviceHostPort = servicePort;
            String jsonINFO = gson.toJson(info);
            // Assign info to request
            request.data = jsonINFO;
            //
            // newProduct.productID = id;

            URL url = new URL(registryAddr);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            OutputStream outputStream = connection.getOutputStream();
            String jsonData = gson.toJson(request);
            outputStream.write(jsonData.getBytes("UTF-8"));
            int responseCode = connection.getResponseCode();

            System.out.println("Request made with url:" + url.toString());
            // Check response
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = reader.readLine()) != null) {
                    response.append(inputLine);
                }
                reader.close();

                System.out.println("Response Content: " + response.toString());
                return true;
            } else {
                System.out.println("HTTP POST request failed with response code: " + responseCode);
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

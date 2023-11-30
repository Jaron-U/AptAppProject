public class ServiceMessageModel {
    public static int SERVICE_PUBLISH_REQUEST = 101;
    public static int SERVICE_UNPUBLISH_REQUEST = 102;
    public static int SERVICE_DISCOVER_REQUEST = 201;
    public static int SERVICE_PUBLISH_OK = 103;
    public static int SERVICE_DISCOVER_NOT_FOUND = 202;
    public static int SERVICE_DISCOVER_OK = 203;

    int code;
    String data;

}

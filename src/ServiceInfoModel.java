public class ServiceInfoModel {
    public static int SERVICE_USER_LOGIN = 101;
    public static int SERVICE_USER_LOAD = 102;
    public static int SERVICE_USER_SAVE = 103;

    public static int SERVICE_WISHLIST_LOAD = 201;
    public static int SERVICE_WISHLIST_ADD = 202;
    public static int SERVICE_WISHLIST_DELETE = 203;

    public static int SERVICE_APT_SAVE = 301;
    public static int SERVICE_APT_LOAD = 302;
    public static int SERVICE_APT_LOADALL = 303;
    public static int SERVICE_APT_SEARCH_PRICE = 311;
    public static int SERVICE_APT_SEARCH_TYPE = 312;

    int serviceCode;
    String serviceHostAddress;
    int serviceHostPort; // May not be needed, as it's better to include port within the address

}

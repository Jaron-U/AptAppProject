class ServiceInfoModel {
    static SERVICE_USER_LOGIN = 101;
    static SERVICE_USER_LOAD = 102;
    static SERVICE_USER_SAVE = 103;

    static SERVICE_WISHLIST_LOAD = 201;
    static SERVICE_WISHLIST_ADD = 202;
    static SERVICE_WISHLIST_DELETE = 203;

    static SERVICE_APT_SAVE = 301;
    static SERVICE_APT_LOAD = 302;
    static SERVICE_APT_LOADALL = 303;
    static SERVICE_APT_SEARCH_PRICE = 311;
    static SERVICE_APT_SEARCH_TYPE = 312;

    constructor(serviceCode, serviceHostAddress, serviceHostPort) {
        this.serviceCode = serviceCode;
        this.serviceHostAddress = serviceHostAddress;
        this.serviceHostPort = serviceHostPort;
    }
}

export default ServiceInfoModel;
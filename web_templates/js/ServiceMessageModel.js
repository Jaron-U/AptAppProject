class ServiceMessageModel {
    static SERVICE_PUBLISH_REQUEST = 101;
    static SERVICE_UNPUBLISH_REQUEST = 102;
    static SERVICE_PUBLISH_OK = 103;
    static SERVICE_PUBLISH_FAILED = 104;

    static SERVICE_DISCOVER_REQUEST = 201;
    static SERVICE_DISCOVER_NOT_FOUND = 202;
    static SERVICE_DISCOVER_OK = 203;

    constructor(code, data) {
        this.code = code;
        this.data = data;
    }
}

export default ServiceMessageModel;
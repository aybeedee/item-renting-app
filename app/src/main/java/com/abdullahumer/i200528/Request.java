package com.abdullahumer.i200528;

public class Request {

    String requestId;
    String itemId;
    String customerId;

    public Request() {}

    public Request(String requestId, String itemId, String customerId) {
        this.requestId = requestId;
        this.itemId = itemId;
        this.customerId = customerId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
}

package me.jangjunha.ftgo.kitchen_service.grpc;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "destinations")
public class Destinations {

    private String orderServiceUrl;

    protected Destinations() {
    }

    public String getOrderServiceUrl() {
        return orderServiceUrl;
    }

    public void setOrderServiceUrl(String orderServiceUrl) {
        this.orderServiceUrl = orderServiceUrl;
    }
}

package com.uav.autodebit.vo;

import java.io.Serializable;

public class CustomerAuthServiceVO implements Serializable {
    private String providerTokenId;
    private CustomerVO customer;


    public CustomerAuthServiceVO() {
    }


    public String getProviderTokenId() {
        return providerTokenId;
    }

    public void setProviderTokenId(String providerTokenId) {
        this.providerTokenId = providerTokenId;
    }

    public CustomerVO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerVO customer) {
        this.customer = customer;
    }
}

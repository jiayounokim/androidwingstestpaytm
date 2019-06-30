package com.example.wingstestpaytm;

import com.google.gson.annotations.SerializedName;

public class ChecksumPaytm {

    @SerializedName("CHECKSUMHASH")
    private String checksumHash;

    @SerializedName("ORDER_ID")
    private String orderId;

    @SerializedName("TXN_AMOUNT")
    private String tnxAmount;

    @SerializedName("MERCHANT_ID")
    private  String merchant_key;

    @SerializedName("CUST_ID")
    private String cust_id;


    @SerializedName("CALLBACK_URL")
    private String call_back_url;

    @SerializedName("CHANNEL_ID")
    private String channel_id;

    @SerializedName("WEBSITE")
    private  String website;

    @SerializedName("INDUSTRY_TYPE_ID")
    private String industry_type_id;



    private ChecksumPaytm(String checksumHash, String orderId,String cust_id,String tnxAmount,String merchant_key) {
        this.checksumHash = checksumHash;
        this.orderId = orderId;

        this.tnxAmount=tnxAmount;
        this.merchant_key=merchant_key;
        this.cust_id=cust_id;
    }

    public String getChecksumHash() {
        return checksumHash;
    }

    public String getOrderId() {
        return orderId;
    }



    public String getTnxAmount() {
        return tnxAmount;
    }
    public String getMerchant_key() {
        return merchant_key;
    }
    public String getCustId() {
        return cust_id;
    }

    public String getCall_back_url() {
        return call_back_url;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public String getWebsite() {
        return website;
    }

    public String getIndustry_type_id() {
        return industry_type_id;
    }

    @Override
    public String toString() {
        return "ChecksumPaytm{" +
                "checksumHash='" + checksumHash + '\'' +
                ", orderId='" + orderId + '\'' +
                      ", paytStatus='" +", tnx='" + tnxAmount + '\'' +", mid='" + merchant_key + '\'' +
                      '}';
    }
}
package com.beggar.admin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "receipts")
public class Receipt {

    @Id
    @Column(name = "receipt_id")
    private Long receiptId;

    @Column(name = "room_no", insertable = false, updatable = false)
    private Long roomNo;

    @Column(name = "room_member_id", insertable = false, updatable = false)
    private Long roomMemberId;

    @Column(name = "receipt_type")
    private String receiptType;

    @Column(name = "input_method")
    private String inputMethod;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "ocr_status")
    private String ocrStatus;

    @Column(name = "store_name")
    private String storeName;

    @Column(name = "total_amount")
    private Integer totalAmount;

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "address")
    private String address;

    @Column(name = "good_price_store_id")
    private String goodPriceStoreId;

    @Column(name = "good_price_store_name")
    private String goodPriceStoreName;

    @Column(name = "good_price_store_address")
    private String goodPriceStoreAddress;

    @Column(name = "good_price_matched")
    private Boolean goodPriceMatched;

    @Column(name = "good_price_verified_at")
    private LocalDateTime goodPriceVerifiedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    protected Receipt() {
    }

    public Long getReceiptId() {
        return receiptId;
    }

    public Long getRoomNo() {
        return roomNo;
    }

    public Long getRoomMemberId() {
        return roomMemberId;
    }

    public String getReceiptType() {
        return receiptType;
    }

    public String getInputMethod() {
        return inputMethod;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getOcrStatus() {
        return ocrStatus;
    }

    public String getStoreName() {
        return storeName;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public Integer getAmount() {
        return amount;
    }

    public String getAddress() {
        return address;
    }

    public String getGoodPriceStoreId() {
        return goodPriceStoreId;
    }

    public String getGoodPriceStoreName() {
        return goodPriceStoreName;
    }

    public String getGoodPriceStoreAddress() {
        return goodPriceStoreAddress;
    }

    public Boolean getGoodPriceMatched() {
        return goodPriceMatched;
    }

    public LocalDateTime getGoodPriceVerifiedAt() {
        return goodPriceVerifiedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

package com.beggar.admin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "good_price_stores")
public class GoodPriceStore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "store_id")
    private String storeId;

    @Column(name = "name")
    private String name;

    @Column(name = "category")
    private String category;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "price")
    private Integer price;

    @Column(name = "address")
    private String address;

    @Column(name = "lat")
    private BigDecimal lat;

    @Column(name = "lng")
    private BigDecimal lng;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "source")
    private String source;

    @Column(name = "visible")
    private Boolean visible;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    protected GoodPriceStore() {
    }

    public GoodPriceStore(
            String storeId,
            String name,
            String category,
            String itemName,
            Integer price,
            String address,
            BigDecimal lat,
            BigDecimal lng,
            String phoneNumber,
            Boolean visible
    ) {
        this.storeId = storeId;
        this.name = name;
        this.category = category;
        this.itemName = itemName;
        this.price = price;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.phoneNumber = phoneNumber;
        this.source = "ADMIN";
        this.visible = visible == null || visible;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void update(
            String storeId,
            String name,
            String category,
            String itemName,
            Integer price,
            String address,
            BigDecimal lat,
            BigDecimal lng,
            String phoneNumber,
            Boolean visible
    ) {
        this.storeId = storeId;
        this.name = name;
        this.category = category;
        this.itemName = itemName;
        this.price = price;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.phoneNumber = phoneNumber;
        this.visible = visible == null || visible;
        this.updatedAt = LocalDateTime.now();
    }

    public void toggleVisible() {
        this.visible = !Boolean.TRUE.equals(this.visible);
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getStoreId() {
        return storeId;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getItemName() {
        return itemName;
    }

    public Integer getPrice() {
        return price;
    }

    public String getAddress() {
        return address;
    }

    public BigDecimal getLat() {
        return lat;
    }

    public BigDecimal getLng() {
        return lng;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getSource() {
        return source;
    }

    public Boolean getVisible() {
        return visible;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}

package com.beggar.admin.dto;

public class GoodPriceStoreListItem {

    private final Long id;
    private final String name;
    private final String category;
    private final String itemName;
    private final String price;
    private final String address;
    private final String visibleLabel;
    private final String visibleClass;
    private final String updatedAt;

    public GoodPriceStoreListItem(
            Long id,
            String name,
            String category,
            String itemName,
            String price,
            String address,
            String visibleLabel,
            String visibleClass,
            String updatedAt
    ) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.itemName = itemName;
        this.price = price;
        this.address = address;
        this.visibleLabel = visibleLabel;
        this.visibleClass = visibleClass;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
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

    public String getPrice() {
        return price;
    }

    public String getAddress() {
        return address;
    }

    public String getVisibleLabel() {
        return visibleLabel;
    }

    public String getVisibleClass() {
        return visibleClass;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
}

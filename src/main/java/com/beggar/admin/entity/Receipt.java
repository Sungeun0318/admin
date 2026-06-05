package com.beggar.admin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "receipts")
public class Receipt {

    @Id
    @Column(name = "receipt_id")
    private Long receiptId;

    @Column(name = "title")
    private String title;

    @Column(name = "room_no", insertable = false, updatable = false)
    private Long roomNo;

    @Column(name = "amount")
    private Integer amount;

    protected Receipt() {
    }

    public Long getReceiptId() {
        return receiptId;
    }

    public String getTitle() {
        return title;
    }

    public Long getRoomNo() {
        return roomNo;
    }

    public Integer getAmount() {
        return amount;
    }
}

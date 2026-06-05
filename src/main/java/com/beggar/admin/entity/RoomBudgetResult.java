package com.beggar.admin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "room_budget_results")
public class RoomBudgetResult {

    @Id
    @Column(name = "result_id")
    private Long resultId;

    @Column(name = "room_no", insertable = false, updatable = false)
    private Long roomNo;

    @Column(name = "min_budget_per_person")
    private Integer minBudgetPerPerson;

    @Column(name = "member_count")
    private Integer memberCount;

    @Column(name = "total_budget")
    private Integer totalBudget;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    protected RoomBudgetResult() {
    }

    public Long getResultId() {
        return resultId;
    }

    public Long getRoomNo() {
        return roomNo;
    }

    public Integer getMinBudgetPerPerson() {
        return minBudgetPerPerson;
    }

    public Integer getMemberCount() {
        return memberCount;
    }

    public Integer getTotalBudget() {
        return totalBudget;
    }

    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }
}

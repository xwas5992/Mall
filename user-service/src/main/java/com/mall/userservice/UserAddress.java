package com.mall.userservice;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "user_address")
public class UserAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "recipient_name", nullable = false, length = 32)
    private String recipientName;

    @Column(name = "recipient_phone", nullable = false, length = 20)
    private String recipientPhone;

    @Column(name = "province", nullable = false, length = 32)
    private String province;

    @Column(name = "city", nullable = false, length = 32)
    private String city;

    @Column(name = "district", nullable = false, length = 32)
    private String district;

    @Column(name = "address_detail", nullable = false, length = 128)
    private String addressDetail;

    @Column(name = "postal_code", length = 10)
    private String postalCode;

    @Column(name = "is_default", columnDefinition = "tinyint(1) default 0")
    private Boolean isDefault = false;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
} 
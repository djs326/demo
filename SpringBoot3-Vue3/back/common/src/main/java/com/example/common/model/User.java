package com.example.common.model;

import com.example.common.annotation.FieldPermission;
import com.example.common.enums.MaskType;
import lombok.Data;

@Data
public class User {
    private Long id;
    private String username;
    @FieldPermission(hidden = true)
    private String password;
    @FieldPermission(mask = true, maskType = MaskType.PHONE)
    private String phone;
    @FieldPermission(mask = true, maskType = MaskType.EMAIL)
    private String email;
    @FieldPermission(mask = true, maskType = MaskType.ID_CARD)
    private String idCard;
    @FieldPermission(mask = true, maskType = MaskType.BANK_CARD)
    private String bankCard;
    @FieldPermission(mask = true)
    private String address;
}
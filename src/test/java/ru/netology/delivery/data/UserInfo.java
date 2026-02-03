package ru.netology.delivery.data;

import lombok.Data;

@Data
public class UserInfo {
    private final String city;
    private final String name;
    private final String phone;
}
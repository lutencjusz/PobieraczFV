package com.example.application.model;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class Test {
    private String name;
    private String url;
    private LocalDate estimatedDeliveryDate;

    public Test(String name, String url, LocalDate estimatedDeliveryDate) {
        this.name = name;
        this.url = url;
        this.estimatedDeliveryDate = estimatedDeliveryDate;
    }
}

package com.example.application.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class Test {
    private String name;
    private String url;
    private LocalDate estimatedDeliveryDate;
    private boolean status;

    public Test(String name, String url, LocalDate estimatedDeliveryDate) {
        this.name = name;
        this.url = url;
        this.estimatedDeliveryDate = estimatedDeliveryDate;
        this.status=true;
    }
}

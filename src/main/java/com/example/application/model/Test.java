package com.example.application.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class Test {
    private String name;
    private String url;

    private String nrFv;
    private String dropboxLink;
    private LocalDate estimatedDeliveryDate;
    private String status;

    public Test(String name, String url, String nrFv, String dropboxLink, LocalDate estimatedDeliveryDate, String status) {
        this.name = name;
        this.url = url;
        this.estimatedDeliveryDate = estimatedDeliveryDate;
        this.status=status;
        this.nrFv=nrFv;
        this.dropboxLink = dropboxLink;
    }
}

package com.example.application.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class Test {
    private static int counter = 1;
    private int id;
    private String name;
    private String url;
    private String nrFv;
    private String dropboxLink;
    private LocalDate estimatedDeliveryDate;
    private TestStatus status;
    private boolean isInteractionNeed;

    private Double progress;
    public Test(String name, String url, String nrFv, String dropboxLink, LocalDate estimatedDeliveryDate, TestStatus status, boolean isInteractionNeed) {
        this.id = counter++;
        this.name = name;
        this.url = url;
        this.estimatedDeliveryDate = estimatedDeliveryDate;
        this.status = status;
        this.nrFv = nrFv;
        this.dropboxLink = dropboxLink;
        this.isInteractionNeed = isInteractionNeed;
        this.progress = 0.0;
    }
}

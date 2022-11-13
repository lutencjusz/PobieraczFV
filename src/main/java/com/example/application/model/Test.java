package com.example.application.model;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Setter
@Getter
public class Test {
    private static int counter = 1;
    private int id;
    private String name;
    private String url;
    private List<String> nrFv;
    private String dropboxLink;
    private LocalDateTime estimatedDeliveryDate;
    private Duration duration;
    private TestStatus status;
    private boolean isInteractionNeed;
    private boolean isDatePickerNeed;

    private Double progress;
    public Test(String name, String url, String nrFv, String dropboxLink, LocalDateTime estimatedDeliveryDate, TestStatus status, boolean isInteractionNeed, boolean isDatePickerNeed) {
        this.id = counter++;
        this.name = name;
        this.url = url;
        this.estimatedDeliveryDate = estimatedDeliveryDate;
        this.status = status;
        this.nrFv = new ArrayList<>(Collections.singleton(nrFv));
        this.dropboxLink = dropboxLink;
        this.isInteractionNeed = isInteractionNeed;
        this.isDatePickerNeed = isDatePickerNeed;
        this.progress = 0.0;
        this.duration = Duration.ZERO;
    }
}

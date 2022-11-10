package com.example.application;

import com.example.application.model.Test;
import com.example.application.tests.InvoicesDownloadTest;


public class ThreadTest extends Thread {

    private final Test test;

    public ThreadTest(Test test) {
        this.test = test;
    }

    public void run() {
        InvoicesDownloadTest invoicesDownloadTest = new InvoicesDownloadTest();
        System.out.println("Uruchomiono test:" + this.test.getName());
        this.test.setStatus("progress");
        switch (test.getName().toLowerCase()) {
            case "pko": {
                invoicesDownloadTest.pko();
                break;
            }
            case "leaselink":{
                invoicesDownloadTest.leaseLink();
                break;
            }
            case "microsoft":{
                invoicesDownloadTest.microsoft();
                break;
            }
            case "t-mobile":{
                invoicesDownloadTest.tMobile();
                break;
            }
            case "toyota":{
                invoicesDownloadTest.toyota();
                break;
            }
            case "fakturownia":{
                invoicesDownloadTest.fakturownia();
                break;
            }
        }
        System.out.println("Zakończono test: " + this.test.getName());
    }
}


package com.example.application;

import com.example.application.model.Test;
import com.vaadin.flow.component.grid.Grid;

public class ThreadTest extends Thread {

    private final Test test;

    public ThreadTest(Test test) {
        this.test = test;
    }

    public void run() {
        System.out.println("Uruchomiono test:" + this.test.getName());
        this.test.setStatus("progress");
        messageAndSleep(5);
        System.out.println("Zakończono test: " + this.test.getName());
        this.test.setStatus("pass");
    }

    /**
     * Metoda wprowadza opóźnienie określone w <i>sleep</i> wcześniej informując o tym poprzez <i>message</i>
     *
     * @param sleepTimeInSec czas oczekiwania wyrażony w sek.
     * @param message        komunikaty pojawiające się przed wejściem w czekanie w osobnej linijce
     */
    public static void messageAndSleep(Integer sleepTimeInSec, String... message) {
        try {
            if (message.length >= 1) {
                for (String s : message) System.out.println(s);
            }
            Thread.sleep(sleepTimeInSec * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


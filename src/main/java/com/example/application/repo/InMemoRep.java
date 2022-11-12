package com.example.application.repo;

import com.example.application.model.Test;
import com.example.application.model.TestStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@Repository
public class InMemoRep {
    private static final List<Test> tests = new ArrayList<>();

    @PostConstruct
    public void initData() {
        tests.add(new Test("LeaseLink", "link1", "...", "...", LocalDate.now(), TestStatus.todo, true));
        tests.add(new Test("Microsoft", "link1", "...", "...", LocalDate.now(), TestStatus.todo, false));
        tests.add(new Test("PKO", "link1", "...", "...", LocalDate.now(), TestStatus.todo, false));
        tests.add(new Test("T-Mobile", "link1", "...", "...", LocalDate.now(), TestStatus.todo, true));
        tests.add(new Test("Toyota", "link1", "...", "...", LocalDate.now(), TestStatus.todo, false));
        tests.add(new Test("Fakturownia", "link1", "...", "...", LocalDate.now(), TestStatus.todo, true));
    }

    public List<Test> getTests() {
        return tests;
    }

    public void clear() {
        tests.clear();
    }

    public void add(Test test) {
        tests.add(test);
    }

    public void setStatus(String name, TestStatus newStatus) {
        Optional<Test> test = tests.stream().filter(item -> item.getName().equals(name)).findFirst();
        if (test.isPresent()){
            test.get().setStatus(newStatus);
        } else {
            System.out.println("Nie odświeżyłem statusu");
        }
    }

    public void setProgress(String name, double progress){
        Optional<Test> test = tests.stream().filter(item -> item.getName().equals(name)).findFirst();
        if (test.isPresent()){
            test.get().setProgress(progress);
        } else {
            System.out.println("Nie odświeżyłem postępu testu");
        }
    }

    public void updateTestData(String name, String nrFv, String dropboxLink, TestStatus status) {
        Optional<Test> test = tests.stream().filter(item -> item.getName().equals(name)).findFirst();
        if (test.isPresent()) {
            test.get().setNrFv(nrFv);
            test.get().setDropboxLink(dropboxLink);
            test.get().setEstimatedDeliveryDate(LocalDate.now());
            test.get().setStatus(status);
        } else {
            System.out.println("Nie odświeżyłem danych");
        }
    }
}

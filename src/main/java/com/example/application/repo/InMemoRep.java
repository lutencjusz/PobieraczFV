package com.example.application.repo;

import com.example.application.model.Test;
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
        tests.add(new Test("LeaseLink", "link1", "15911/10/2022UL", "dropboxLink", LocalDate.now(), "todo"));
        tests.add(new Test("Microsoft", "link1", "E0400KHCU0", "dropboxLink", LocalDate.now(), "todo"));
        tests.add(new Test("PKO", "link1", "LM/22/10/110018", "dropboxLink", LocalDate.now(), "todo"));
        tests.add(new Test("T-Mobile", "link1", "503438161022", "dropboxLink", LocalDate.now(), "todo"));
        tests.add(new Test("Toyota", "link1", "14978/10/2022/SP", "dropboxLink", LocalDate.now(), "todo"));
        tests.add(new Test("Fakturownia", "link1", "FV2022/10/1", "dropboxLink", LocalDate.now(), "todo"));
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

    public Test getTest(int id) {
        Optional<Test> test = tests.stream().filter(item -> item.getId() == id).findFirst();
        return test.get();
    }

    public void setStatus(String name, String newStatus) {
        Optional<Test> test = tests.stream().filter(item -> item.getName().equals(name)).findFirst();
        test.ifPresent(value -> value.setStatus(newStatus));

    }

    public void updateTestData(String name, String nrFv, String dropboxLink, String status) {
        Optional<Test> test = tests.stream().filter(item -> item.getName().equals(name)).findFirst();
        if (test.isPresent()) {
            test.get().setNrFv(nrFv);
            test.get().setDropboxLink(dropboxLink);
            test.get().setEstimatedDeliveryDate(LocalDate.now());
            test.get().setStatus(status);
        } else {
            System.out.println("Nie zaktualizaowałem danych");
        }
    }
}

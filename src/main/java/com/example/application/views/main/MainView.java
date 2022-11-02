package com.example.application.views.main;

import com.example.application.ThreadTest;
import com.example.application.model.Test;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.LocalDate;

import java.util.*;

import static com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode.AROUND;

@PageTitle("Tests")
@Route(value = "")
public class MainView extends VerticalLayout {

    public MainView() {
        Set<Test> tests = new HashSet<>();
        initData(tests);

        HorizontalLayout logo = new HorizontalLayout();

        DatePicker datePicker = new DatePicker("Wybierz datę wystawienia faktury:");
        DatePicker.DatePickerI18n polishI18nDatePicker = new DatePicker.DatePickerI18n();
        polishI18nDatePicker.setMonthNames(List.of("Styczeń", "Luty", "Marzec", "Kwiecień",
                "Maj", "Czerwiec", "Lipiec", "Sierpień", "Wrzesień", "Październik",
                "Listopad", "Grudzień"));
        polishI18nDatePicker.setWeekdays(List.of("Niedziela", "Poniedziałek", "Wtorek", "Środa", "Czwartek", "Piątek", "Sobota", "Niedziela"));
        polishI18nDatePicker.setWeekdaysShort(List.of("Nie", "Pon", "Wto", "Śro", "Czw", "Pią", "Sob"));
        polishI18nDatePicker.setWeek("Tydzień");
        polishI18nDatePicker.setToday("Dzisiaj");
        polishI18nDatePicker.setCancel("Wróć");
        polishI18nDatePicker.setDateFormat("dd/MM/yyyy");
        polishI18nDatePicker.setFirstDayOfWeek(1);
        datePicker.setI18n(polishI18nDatePicker);

        logo.add(
                new H2("Aplikacja Vaadin"),
                datePicker
        );

        Grid<Test> grid = new Grid<>(Test.class, false);
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.setItems(tests);
        grid.addSelectionListener(selection -> {
            System.out.printf("Ilość zaznaczonych testów: %s%n", selection.getAllSelectedItems().size());
        });

        grid.addComponentColumn(test -> createStatusBadge(test.getStatus())).setHeader("Status").setKey("status").setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(Test::getName).setHeader("Nazwa serwisu").setKey("name").setFooter(new Html("<b>Początkowa ilość serwisów: " + tests.size() + "</b>")).setResizable(true);
        grid.addColumn(Test::getNrFv).setHeader("Numer FV").setKey("nrfv").setResizable(true);
        grid.addColumn(Test::getDropboxLink).setHeader("Link do Dropbox").setKey("link");
        grid.addColumn(new LocalDateRenderer<>(Test::getEstimatedDeliveryDate, "dd/MM/yyyy")).setSortable(true).setHeader("Estymowana data dostarczenia").setKey("date");
        grid.addComponentColumn(test -> createButtons(test, tests, grid)).setHeader("Akcje");

        grid.addThemeVariants(GridVariant.LUMO_NO_ROW_BORDERS,
                GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);
        grid.setColumnReorderingAllowed(true);

        grid.addItemClickListener(event -> System.out
                .println(("Kliknięto wiersz: " + event.getItem().getName())));

        grid.getColumnByKey("name")
                .setSortable(true);
        grid.getColumnByKey("status")
                .setSortable(true);
        grid.getColumnByKey("link")
                .setSortable(false);

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setPadding(false);
        buttons.setJustifyContentMode(AROUND);

        Button addButton = new Button("Dodaj test");
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Usuń wszystko");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        Button initButton = new Button("Przywróć ustawienia");
        initButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST);

        Button executeTestsButton = new Button("Wykonaj testy");
        executeTestsButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

        buttons.add(executeTestsButton);
        buttons.add(addButton);
        buttons.add(cancelButton);
        buttons.add(initButton);

        cancelButton.addClickListener(buttonClickEvent -> {
            tests.clear();
            grid.getDataProvider().refreshAll();
        });

        initButton.addClickListener(buttonClickEvent -> {
            tests.clear();
            initData(tests);
            grid.getDataProvider().refreshAll();
        });

        executeTestsButton.addClickListener(buttonClickEvent -> {
            int testsNumber = grid.getSelectedItems().size();
            Notification notification;
            if (testsNumber > 0) {
                notification = Notification.show("Uruchomiono testów: " + grid.getSelectedItems().size());
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                notification.setPosition(Notification.Position.TOP_CENTER);
                executionTests(grid.getSelectedItems());
            } else {
                notification = Notification.show("Nie wybrano żadnych testów do uruchomienia");
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.setPosition(Notification.Position.MIDDLE);
            }
        });

        addButton.addClickListener(buttonClickEvent -> {
            tests.add(new Test("Test6", "link6", "FV0001", "dropboxLink", LocalDate.now(), "todo"));
            grid.getDataProvider().refreshAll();
        });

        addButton.addClickShortcut(Key.ENTER);

        add(
                logo,
                grid,
                buttons
        );
    }

    private Span createStatusBadge(String status) {
        String theme;
        switch (status) {
            case "todo":
                theme = "badge primary";
                break;
            case "pass":
                theme = "badge success primary";
                break;
            case "fail":
                theme = "badge error primary";
                break;
            default:
                theme = "badge contrast primary";
                break;
        }
        Span badge = new Span(status.toUpperCase());
        badge.getStyle().set("width", "60px");
        badge.getElement().getThemeList().add(theme);
        return badge;
    }

    private HorizontalLayout createButtons(Test test, Set<Test> tests, Grid<Test> grid) {
        HorizontalLayout horizontalLayout = new HorizontalLayout();

        /* Przycisk usuwający test */
        Button trashButton = new Button();
        trashButton.addThemeVariants(ButtonVariant.LUMO_ICON,
                ButtonVariant.LUMO_ERROR,
                ButtonVariant.LUMO_TERTIARY);
        trashButton.addClickListener(e -> {
            tests.remove(test);
            grid.getDataProvider().refreshAll();
        });
        trashButton.setIcon(new Icon(VaadinIcon.TRASH));

        /* Przycisk uruchamiający pojedynczy test */
        Button testButton = new Button();
        testButton.addThemeVariants(ButtonVariant.LUMO_ICON,
                ButtonVariant.LUMO_SUCCESS,
                ButtonVariant.LUMO_TERTIARY);
        testButton.addClickListener(e -> {
            Set<Test> oneTest = new HashSet<>();
            oneTest.add(test);
            executionTests(oneTest);
            grid.getDataProvider().refreshAll();
        });
        testButton.setIcon(new Icon(VaadinIcon.PLAY_CIRCLE));

        /* pokazuje, że tes został uruchomiony */
        Button progressButton = new Button();
        progressButton.addThemeVariants(ButtonVariant.LUMO_ICON,
                ButtonVariant.LUMO_CONTRAST,
                ButtonVariant.LUMO_TERTIARY);
        progressButton.setIcon(new Icon(VaadinIcon.PROGRESSBAR));
        progressButton.setVisible(false);
        test.setProgress(progressButton);

        /* Dodanie przycisków do layoutu */
        horizontalLayout.add(testButton, trashButton, progressButton);
        return horizontalLayout;
    }

    private void initData(Set<Test> tests) {
        tests.add(new Test("LeaseLink", "link1", "15911/10/2022UL", "dropboxLink", LocalDate.now(), "todo"));
        tests.add(new Test("Microsoft", "link1", "E0400KHCU0", "dropboxLink", LocalDate.now(), "todo"));
        tests.add(new Test("PKO", "link1", "LM/22/10/110018", "dropboxLink", LocalDate.now(), "todo"));
        tests.add(new Test("T-Mobile", "link1", "503438161022", "dropboxLink", LocalDate.now(), "todo"));
        tests.add(new Test("Toyota", "link1", "14978/10/2022/SP", "dropboxLink", LocalDate.now(), "todo"));
        tests.add(new Test("Fakturownia", "link1", "FV2022/10/1", "dropboxLink", LocalDate.now(), "todo"));
    }

    private void executionTests(Set<Test> tests) {
        for (Test test : tests) {
            Notification notification = Notification.show("Uruchomiono test: " + test.getName());
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            notification.setPosition(Notification.Position.TOP_CENTER);
            notification.setOpened(true);
            notification.setVisible(true);
            test.getProgress().setVisible(true);
            ThreadTest testThread = new ThreadTest(test);
            Thread thread = new Thread(testThread);
            thread.start();
//            messageAndSleep(5);
            test.getProgress().setVisible(false);
//            grid.getDataProvider().refreshAll();
        }
    }
}

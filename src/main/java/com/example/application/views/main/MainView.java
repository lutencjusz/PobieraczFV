package com.example.application.views.main;

import com.example.application.ThreadTest;
import com.example.application.model.Test;
import com.example.application.repo.InMemoRep;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
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
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import java.util.*;

import static com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode.AROUND;

@PageTitle("Tests")
@Route(value = "")
@StyleSheet("/style.css")
public class MainView extends VerticalLayout {

    @Autowired
    InMemoRep inMemoRep = new InMemoRep();

    Grid<Test> grid;

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        HorizontalLayout logo = new HorizontalLayout();
        Image image = new Image("/icon.png", "Logo");
        image.setHeight("10%");
        image.setWidth("10%");

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
                image,
                new H2("Fakturowania"),
                datePicker
        );

        grid = new Grid<>(Test.class, false);
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.setItems(inMemoRep.getTests());
        grid.addSelectionListener(selection -> {
            System.out.printf("Ilość zaznaczonych testów: %s%n", selection.getAllSelectedItems().size());
        });

        grid.addComponentColumn(test -> createStatusBadge(test.getStatus())).setHeader("Status").setKey("status").setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(Test::getName).setHeader("Nazwa serwisu").setKey("name").setFooter(new Html("<b>Początkowa ilość serwisów: " + inMemoRep.getTests().size() + "</b>")).setResizable(true);
        grid.addColumn(Test::getNrFv).setHeader("Numer FV").setKey("nrfv").setResizable(true);
        grid.addColumn(Test::getDropboxLink).setHeader("Link do Dropbox").setKey("link");
        grid.addColumn(new LocalDateRenderer<>(Test::getEstimatedDeliveryDate, "dd/MM/yyyy")).setSortable(true).setHeader("Estymowana data dostarczenia").setKey("date");
        grid.addComponentColumn(test -> {
            Image imagePng = new Image("/png/" + test.getName().toLowerCase() + ".png", "screen shot");
            imagePng.setWidth("70px");
            imagePng.setHeight("50px");
            return imagePng;
        }).setKey("screenshot").setHeader("Screeny");
        grid.addComponentColumn(test -> createButtons(test, inMemoRep.getTests(), grid)).setHeader("Akcje");

        grid.addThemeVariants(GridVariant.LUMO_NO_ROW_BORDERS,
                GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);
        grid.setColumnReorderingAllowed(true);

//        grid.addItemClickListener(event -> System.out
//                .println(("Kliknięto wiersz: " + event.getItem().getName())));

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
            inMemoRep.clear();
            refreshItems();
        });

        initButton.addClickListener(buttonClickEvent -> {
            inMemoRep.clear();
            inMemoRep.initData();
            refreshItems();
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
            inMemoRep.add(new Test("Test6", "link6", "FV0001", "dropboxLink", LocalDate.now(), "todo"));
            refreshItems();
        });

        addButton.addClickShortcut(Key.ENTER);
        refreshItems();
        add(
                logo,
                grid,
                buttons
        );
    }

    public void refreshItems() {
        grid.setItems(inMemoRep.getTests());
        grid.getDataProvider().refreshAll();
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
        badge.getStyle().set("width", "80px");
        badge.getElement().getThemeList().add(theme);
        return badge;
    }

    private HorizontalLayout createButtons(Test test, List<Test> tests, Grid<Test> grid) {
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
            refreshItems();
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

    private void executionTests(Set<Test> tests) {
        for (Test test : tests) {
            Notification notification = Notification.show("Uruchomiono test: " + test.getName());
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            notification.setPosition(Notification.Position.TOP_CENTER);
            notification.setOpened(true);
            notification.setVisible(true);
            notification.addOpenedChangeListener(not->{
                refreshItems();
            });
            ThreadTest testThread = new ThreadTest(test);
            Thread thread = new Thread(testThread);
            thread.start();
            refreshItems();
        }
    }

    /**
     * Metoda wprowadza opóźnienie określone w <i>sleep</i> wcześniej informując o tym poprzez <i>message</i>
     *
     * @param sleep   czas oczekiwania wyrażony w sek.
     * @param message komunikaty pojawiające się przed wejściem w czekanie w osobnej linijce
     */
    public static void messageAndSleep(Integer sleep, String... message) {
        try {
            if (message.length >= 1) {
                for (String s : message) System.out.println(s);
            }
            Thread.sleep(sleep * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

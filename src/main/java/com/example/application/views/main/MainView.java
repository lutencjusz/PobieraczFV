package com.example.application.views.main;

import com.example.application.model.Test;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;

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
        tests.add(new Test("Test1", "link1", "FV0001","dropboxLink",LocalDate.now(),"todo"));
        tests.add(new Test("Test2", "link2", "FV0001","dropboxLink",LocalDate.now(),"pass"));
        tests.add(new Test("Test3", "link3", "FV0001","dropboxLink",LocalDate.now(),"fail"));

        HorizontalLayout logo = new HorizontalLayout();

        Image image = new Image("C:\\Data\\Java\\vaadintests\\target\\classes\\images\\icon.png", "Logo");
        Button buttonLink = new Button("Page 1", event -> UI.getCurrent().navigate("https:\\\\www.google.pl\\"));

        logo.add(
                image,
                new H2("Aplikacja Vaadin")
        );

        Grid<Test> grid = new Grid<>(Test.class, false);
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.setItems(tests);
        grid.addSelectionListener(selection -> {
             System.out.printf("Ilość zaznaczonych testów: %s%n", selection.getAllSelectedItems().size());
        });

        grid.addComponentColumn(test -> createStatusBadge(test.getStatus())).setHeader("Status").setKey("status").setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(Test::getName).setHeader("Nazwa serwisu").setKey("name").setFooter(new Html("<b>Suma</b>")).setResizable(true);
        grid.addColumn(Test::getNrFv).setHeader("Numer FV").setKey("nrfv").setResizable(true);
        grid.addColumn(Test::getDropboxLink).setHeader("Link do Dropbox").setKey("link");
        grid.addColumn(new LocalDateRenderer<>(Test::getEstimatedDeliveryDate, "dd/MM/yyyy")).setSortable(true).setHeader("Estymowana data dostarczenia").setKey("date");
        grid.addColumn(
                new ComponentRenderer<>(Button::new, (button, test) -> {
                    button.addThemeVariants(ButtonVariant.LUMO_ICON,
                            ButtonVariant.LUMO_ERROR,
                            ButtonVariant.LUMO_TERTIARY);
                    button.addClickListener(e -> {
                        tests.remove(test);
                        grid.getDataProvider().refreshAll();
                    });
                    button.setIcon(new Icon(VaadinIcon.TRASH));
                })).setHeader("Akcje");

//        grid.addColumn(TemplateRenderer.<Test>of(
//                        "<button on-click='handleUpdate'>Update</button>" +
//                                "<button on-click='handleRemove'>Remove</button>" +
//                                "<button on-click='handleAdd'>Add</button>"
//                ).withEventHandler("handleUpdate", test -> {
//                    test.setName(test.getName() + " poprawiony");
//                    grid.getDataProvider().refreshItem(test);
//                }).withEventHandler("handleRemove", test -> {
//                    tests.remove(test);
//                    grid.getDataProvider().refreshAll();
//                    System.out.println(grid.getDataProvider().getId(test));
//                }).withEventHandler("handleAdd", test -> {
//                    tests.add(new Test("Test4", "link4", LocalDate.now(),"todo"));
//                    grid.getDataProvider().refreshAll();
//                })
//        ).setHeader("Akcje");
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

        Button addButton = new Button("Dodaj");
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Usuń");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        buttons.add(addButton);
        buttons.add(cancelButton);

        cancelButton.addClickListener(buttonClickEvent -> {
            tests.clear();
            grid.getDataProvider().refreshAll();
        });

        addButton.addClickListener(buttonClickEvent -> {
            tests.add(new Test("Test6", "link6", "FV0001","dropboxLink", LocalDate.now(),"todo"));
            grid.getDataProvider().refreshAll();
        });

        addButton.addClickShortcut(Key.ENTER);

        add(
                logo,
                buttonLink,
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
        badge.getStyle().set("width","60px");
        badge.getElement().getThemeList().add(theme);
        return badge;
    }
}

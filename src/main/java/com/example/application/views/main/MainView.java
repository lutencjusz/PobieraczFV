package com.example.application.views.main;

import com.example.application.model.Test;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSingleSelectionModel;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.NativeButtonRenderer;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.data.selection.SingleSelect;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

import static com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode.AROUND;

@PageTitle("Tests")
@Route(value = "")
public class MainView extends VerticalLayout {

    public MainView() {
        String selectedTest;
        Set<Test> tests = new HashSet<>();
        tests.add(new Test("Test1", "link1", LocalDate.now()));
        tests.add(new Test("Test2", "link2", LocalDate.now()));
        tests.add(new Test("Test3", "link3", LocalDate.now()));

        HorizontalLayout logo = new HorizontalLayout();

        Image image = new Image("C:\\Data\\Java\\vaadintests\\target\\classes\\images\\icon.png", "Logo");

        logo.add(
                image,
                new H2("Aplikacja Vaadin")
        );

        Grid<Test> grid = new Grid<>();
        grid.setItems(tests);
        grid.addColumn(Test::getName).setHeader("Nazwa").setKey("name").setFooter(new Html("<b>Suma</b>"));
        grid.addColumn(Test::getUrl).setHeader("Link").setKey("link");
        grid.addColumn(new LocalDateRenderer<>(
                        Test::getEstimatedDeliveryDate,
                        "dd/MM/yyyy"))
                .setHeader("Estymowana data dostarczenia").setKey("date");
        grid.addColumn(new NativeButtonRenderer<>("Usuń",
                clickedItem -> {
                    System.out.println("Kliknąłem usuń: " + clickedItem.getName());
                })
        );
        grid.addColumn(new ComponentRenderer<>(test -> {
            if (test.getStatus().equals("new")) {
                return new Icon(VaadinIcon.MALE);
            } else {
                return new Icon(VaadinIcon.FEMALE);
            }
        })).setHeader("Gender");

        grid.addColumn(TemplateRenderer.<Test>of(
                        "<button on-click='handleUpdate'>Update</button>" +
                                "<button on-click='handleRemove'>Remove</button>" +
                                "<button on-click='handleAdd'>Add</button>"
                ).withEventHandler("handleUpdate", test -> {
                    test.setName(test.getName() + " poprawiony");
                    grid.getDataProvider().refreshItem(test);
                }).withEventHandler("handleRemove", test -> {
                    tests.remove(test);
                    grid.getDataProvider().refreshAll();
                    System.out.println(grid.getDataProvider().getId(test));
                }).withEventHandler("handleAdd", test -> {
                    tests.add(new Test("Test4", "link4", LocalDate.now()));
                    grid.getDataProvider().refreshAll();
                })
        ).setHeader("Akcje");
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.setColumnReorderingAllowed(true);
        SingleSelect<Grid<Test>, Test> testSingleSelect =
                grid.asSingleSelect();
        testSingleSelect.addValueChangeListener(e -> {
            System.out.println(e.getValue().getName());
        });
        grid.addItemClickListener(event -> System.out
                .println(("Kliknięto wiersz: " + event.getItem().getName())));

        GridSingleSelectionModel<Test> singleSelect =
                (GridSingleSelectionModel<Test>) grid
                        .getSelectionModel();
        singleSelect.setDeselectAllowed(false);

        grid.getColumnByKey("name")
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
            tests.add(new Test("Test6", "link6", LocalDate.now()));
            grid.getDataProvider().refreshAll();
        });

        addButton.addClickShortcut(Key.ENTER);

        add(
                logo,
                grid,
                buttons
        );
    }

}

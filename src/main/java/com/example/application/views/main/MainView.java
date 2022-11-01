package com.example.application.views.main;

import com.example.application.GreetService;
import com.example.application.model.Test;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSingleSelectionModel;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.NativeButtonRenderer;
import com.vaadin.flow.data.selection.SingleSelect;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode.AROUND;

@Route
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
public class MainView extends VerticalLayout {
    public MainView(@Autowired GreetService service) {

        List<Test> tests = Arrays.asList(
                new Test("Test1","link", LocalDate.now()),
                new Test("Test2","link2", LocalDate.now()),
                new Test("Test3","link3", LocalDate.now())
        );

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
                    System.out.println("Kliknąłem usuń: "+clickedItem.getName());
                })
        );
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.setColumnReorderingAllowed(true);
        SingleSelect<Grid<Test>, Test> testSingleSelect =
                grid.asSingleSelect();
        testSingleSelect.addValueChangeListener(e -> {
            Test selectedTest = e.getValue();
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
        });

        addButton.addClickListener(buttonClickEvent -> {
        });

        addButton.addClickShortcut(Key.ENTER);

        add(
                logo,
                grid,
                buttons
        );
    }
}


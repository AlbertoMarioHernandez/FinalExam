package org.example;

import entity.DigitalVideoGame;
import entity.PhysicalVideoGame;
import entity.Sale;
import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import service.SaleServiceImpl;
import service.VideoGameServiceImpl;

public class Main extends Application {

    private final VideoGameServiceImpl videoGameService = new VideoGameServiceImpl();
    private final SaleServiceImpl      saleService      = new SaleServiceImpl();

    private ObservableList<DigitalVideoGame>  digitalList;
    private ObservableList<PhysicalVideoGame> physicalList;
    private ObservableList<Sale>              salesList;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("GameZone");
        primaryStage.setMinWidth(860);
        primaryStage.setMinHeight(580);

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.getTabs().addAll(
                new Tab("Digitales", buildDigitalTab()),
                new Tab("Físicos",   buildPhysicalTab()),
                new Tab("Buscar",    buildSearchTab()),
                new Tab("Ventas",    buildSalesTab())
        );

        primaryStage.setScene(new Scene(tabPane, 960, 600));
        primaryStage.show();
    }

    private VBox buildDigitalTab() {
        TableView<DigitalVideoGame> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<DigitalVideoGame, String>  colTitle    = col("Título",      "title");
        TableColumn<DigitalVideoGame, String>  colPlatform = col("Plataforma",  "platform");
        TableColumn<DigitalVideoGame, String>  colGenre    = col("Género",      "genre");
        TableColumn<DigitalVideoGame, Integer> colStock    = col("Stock",       "stock");
        TableColumn<DigitalVideoGame, Double>  colSize     = col("Tamaño GB",   "sizeGB");
        TableColumn<DigitalVideoGame, String>  colDl       = col("Descarga en", "downloadPlatform");

        TableColumn<DigitalVideoGame, Double> colPrice = new TableColumn<>("Precio Base");
        colPrice.setCellValueFactory(d ->
                new SimpleDoubleProperty(d.getValue().getPrice()).asObject());

        TableColumn<DigitalVideoGame, Double> colFinal = new TableColumn<>("Precio Final");
        colFinal.setCellValueFactory(d ->
                new SimpleDoubleProperty(d.getValue().calculateFinalPrice()).asObject());

        table.getColumns().addAll(colTitle, colPlatform, colGenre, colStock, colSize, colDl, colPrice, colFinal);

        digitalList = FXCollections.observableArrayList();
        refreshDigital();
        table.setItems(digitalList);

        Button btnAdd    = new Button("Agregar");
        Button btnEdit   = new Button("Editar");
        Button btnDelete = new Button("Eliminar");

        btnAdd.setOnAction(e -> showDigitalForm(null, table.getScene().getWindow()));
        btnEdit.setOnAction(e -> {
            DigitalVideoGame sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { showWarn("Selecciona un videojuego para editar."); return; }
            showDigitalForm(sel, table.getScene().getWindow());
        });
        btnDelete.setOnAction(e -> {
            DigitalVideoGame sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { showWarn("Selecciona un videojuego para eliminar."); return; }
            confirmDelete(sel.getTitle(), () -> {
                try {
                    if (videoGameService.deleteDigitalVideoGame(sel.getTitle()))
                    { showInfo("Videojuego eliminado."); refreshDigital(); }
                    else showWarn("No se encontró el videojuego.");
                } catch (Exception ex) { showError("Error al eliminar", ex.getMessage()); }
            });
        });

        HBox toolbar = new HBox(8, btnAdd, btnEdit, btnDelete);
        toolbar.setPadding(new Insets(8));
        VBox tab = new VBox(4, toolbar, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        tab.setPadding(new Insets(8));
        return tab;
    }

    private void showDigitalForm(DigitalVideoGame ex, javafx.stage.Window owner) {
        Stage d = dialog(owner, ex == null ? "Agregar Digital" : "Editar Digital");

        TextField txtTitle    = field(ex != null ? ex.getTitle()                  : "", "Título");
        TextField txtPrice    = field(ex != null ? String.valueOf(ex.getPrice())   : "", "Precio");
        TextField txtPlatform = field(ex != null ? ex.getPlatform()               : "", "Plataforma");
        TextField txtStock    = field(ex != null ? String.valueOf(ex.getStock())   : "", "Stock");
        TextField txtGenre    = field(ex != null ? ex.getGenre()                  : "", "Género");
        TextField txtSize     = field(ex != null ? String.valueOf(ex.getSizeGB())  : "", "Tamaño GB");
        TextField txtDl       = field(ex != null ? ex.getDownloadPlatform()       : "", "Plataforma de descarga");

        Button btnSave   = new Button("Guardar");
        Button btnCancel = new Button("Cancelar");
        btnCancel.setOnAction(e -> d.close());

        btnSave.setOnAction(e -> {
            try {
                DigitalVideoGame game = new DigitalVideoGame(
                        txtTitle.getText().trim(),
                        Double.parseDouble(txtPrice.getText().trim()),
                        txtPlatform.getText().trim(),
                        Integer.parseInt(txtStock.getText().trim()),
                        txtGenre.getText().trim(),
                        Double.parseDouble(txtSize.getText().trim()),
                        txtDl.getText().trim()
                );
                if (ex == null) {
                    videoGameService.addDigitalVideoGame(game);
                } else {
                    if (!videoGameService.updateDigitalVideoGame(ex.getTitle(), game))
                    { showWarn("No se encontró el videojuego."); return; }
                }
                showInfo(ex == null ? "Videojuego agregado." : "Videojuego actualizado.");
                refreshDigital();
                d.close();
            } catch (NumberFormatException ex2) {
                showError("Formato inválido", "Precio, stock y tamaño deben ser números válidos.");
            } catch (IllegalArgumentException ex2) {
                showError("Validación fallida", ex2.getMessage());
            } catch (Exception ex2) {
                showError("Error inesperado", ex2.getMessage());
            }
        });

        GridPane grid = formGrid(
                new String[]{"Título:", "Precio:", "Plataforma:", "Stock:", "Género:", "Tamaño GB:", "Descarga en:"},
                new TextField[]{txtTitle, txtPrice, txtPlatform, txtStock, txtGenre, txtSize, txtDl}
        );
        HBox btns = new HBox(8, btnSave, btnCancel);
        btns.setAlignment(Pos.CENTER_RIGHT);
        grid.add(btns, 0, 7, 2, 1);
        d.setScene(new Scene(grid));
        d.showAndWait();
    }

    private VBox buildPhysicalTab() {
        TableView<PhysicalVideoGame> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<PhysicalVideoGame, String>  colTitle    = col("Título",       "title");
        TableColumn<PhysicalVideoGame, String>  colPlatform = col("Plataforma",   "platform");
        TableColumn<PhysicalVideoGame, String>  colGenre    = col("Género",       "genre");
        TableColumn<PhysicalVideoGame, Integer> colStock    = col("Stock",        "stock");
        TableColumn<PhysicalVideoGame, String>  colCond     = col("Condición",    "condition");
        TableColumn<PhysicalVideoGame, String>  colDist     = col("Distribuidor", "distributor");

        TableColumn<PhysicalVideoGame, Double> colPrice = new TableColumn<>("Precio Base");
        colPrice.setCellValueFactory(d ->
                new SimpleDoubleProperty(d.getValue().getPrice()).asObject());

        TableColumn<PhysicalVideoGame, Double> colFinal = new TableColumn<>("Precio Final");
        colFinal.setCellValueFactory(d ->
                new SimpleDoubleProperty(d.getValue().calculateFinalPrice()).asObject());

        table.getColumns().addAll(colTitle, colPlatform, colGenre, colStock, colCond, colDist, colPrice, colFinal);

        physicalList = FXCollections.observableArrayList();
        refreshPhysical();
        table.setItems(physicalList);

        Button btnAdd    = new Button("Agregar");
        Button btnEdit   = new Button("Editar");
        Button btnDelete = new Button("Eliminar");

        btnAdd.setOnAction(e -> showPhysicalForm(null, table.getScene().getWindow()));
        btnEdit.setOnAction(e -> {
            PhysicalVideoGame sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { showWarn("Selecciona un videojuego para editar."); return; }
            showPhysicalForm(sel, table.getScene().getWindow());
        });
        btnDelete.setOnAction(e -> {
            PhysicalVideoGame sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { showWarn("Selecciona un videojuego para eliminar."); return; }
            confirmDelete(sel.getTitle(), () -> {
                try {
                    if (videoGameService.deletePhysicalVideoGame(sel.getTitle()))
                    { showInfo("Videojuego eliminado."); refreshPhysical(); }
                    else showWarn("No se encontró el videojuego.");
                } catch (Exception ex) { showError("Error al eliminar", ex.getMessage()); }
            });
        });

        HBox toolbar = new HBox(8, btnAdd, btnEdit, btnDelete);
        toolbar.setPadding(new Insets(8));
        VBox tab = new VBox(4, toolbar, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        tab.setPadding(new Insets(8));
        return tab;
    }

    private void showPhysicalForm(PhysicalVideoGame ex, javafx.stage.Window owner) {
        Stage d = dialog(owner, ex == null ? "Agregar Físico" : "Editar Físico");

        TextField txtTitle    = field(ex != null ? ex.getTitle()                 : "", "Título");
        TextField txtPrice    = field(ex != null ? String.valueOf(ex.getPrice()) : "", "Precio");
        TextField txtPlatform = field(ex != null ? ex.getPlatform()              : "", "Plataforma");
        TextField txtStock    = field(ex != null ? String.valueOf(ex.getStock()) : "", "Stock");
        TextField txtGenre    = field(ex != null ? ex.getGenre()                 : "", "Género");
        TextField txtCond     = field(ex != null ? ex.getCondition()             : "", "nuevo / usado");
        TextField txtDist     = field(ex != null ? ex.getDistributor()           : "", "Distribuidor");

        Button btnSave   = new Button("Guardar");
        Button btnCancel = new Button("Cancelar");
        btnCancel.setOnAction(e -> d.close());

        btnSave.setOnAction(e -> {
            try {
                PhysicalVideoGame game = new PhysicalVideoGame(
                        txtTitle.getText().trim(),
                        Double.parseDouble(txtPrice.getText().trim()),
                        txtPlatform.getText().trim(),
                        Integer.parseInt(txtStock.getText().trim()),
                        txtGenre.getText().trim(),
                        txtCond.getText().trim(),
                        txtDist.getText().trim()
                );
                if (ex == null) {
                    videoGameService.addPhysicalVideoGame(game);
                } else {
                    if (!videoGameService.updatePhysicalVideoGame(ex.getTitle(), game))
                    { showWarn("No se encontró el videojuego."); return; }
                }
                showInfo(ex == null ? "Videojuego agregado." : "Videojuego actualizado.");
                refreshPhysical();
                d.close();
            } catch (NumberFormatException ex2) {
                showError("Formato inválido", "Precio y stock deben ser números válidos.");
            } catch (IllegalArgumentException ex2) {
                showError("Validación fallida", ex2.getMessage());
            } catch (Exception ex2) {
                showError("Error inesperado", ex2.getMessage());
            }
        });

        GridPane grid = formGrid(
                new String[]{"Título:", "Precio:", "Plataforma:", "Stock:", "Género:", "Condición:", "Distribuidor:"},
                new TextField[]{txtTitle, txtPrice, txtPlatform, txtStock, txtGenre, txtCond, txtDist}
        );
        HBox btns = new HBox(8, btnSave, btnCancel);
        btns.setAlignment(Pos.CENTER_RIGHT);
        grid.add(btns, 0, 7, 2, 1);
        d.setScene(new Scene(grid));
        d.showAndWait();
    }

    private VBox buildSearchTab() {
        Label lblTitle     = new Label("Buscar por título:");
        TextField txtTitle = new TextField();
        txtTitle.setPromptText("Título del videojuego");
        txtTitle.setPrefWidth(300);
        Button btnTitle    = new Button("Buscar");

        String[] digitalHeaders  = {"Título", "Plataforma", "Género", "Stock", "Tamaño GB",  "Descarga en",  "Precio Base", "Precio Final"};
        String[] physicalHeaders = {"Título", "Plataforma", "Género", "Stock", "Condición",  "Distribuidor", "Precio Base", "Precio Final"};

        Label typeLabel    = new Label("");
        Label[] headers    = new Label[8];
        Label[] data       = new Label[8];
        Label noResult     = new Label("");

        GridPane resultGrid = new GridPane();
        resultGrid.setHgap(16);
        resultGrid.setVgap(6);
        resultGrid.setPadding(new Insets(10, 0, 10, 0));

        int[] colWidths = {120, 90, 90, 50, 90, 140, 90, 90};
        for (int w : colWidths) resultGrid.getColumnConstraints().add(colConstraint(w));

        for (int i = 0; i < 8; i++) {
            headers[i] = new Label(digitalHeaders[i]);
            headers[i].setStyle("-fx-font-weight: bold; -fx-underline: true;");
            data[i] = new Label("-");
            resultGrid.add(headers[i], i, 0);
            resultGrid.add(data[i],    i, 1);
        }

        btnTitle.setOnAction(e -> {
            try {
                String q = txtTitle.getText().trim();
                if (q.isEmpty()) { showWarn("Escribe un título para buscar."); return; }

                DigitalVideoGame  dg = videoGameService.findDigitalByTitle(q);
                PhysicalVideoGame ph = videoGameService.findPhysicalByTitle(q);

                if (dg != null) {
                    typeLabel.setText("Tipo: Digital");
                    for (int i = 0; i < 8; i++) headers[i].setText(digitalHeaders[i]);
                    data[0].setText(dg.getTitle());
                    data[1].setText(dg.getPlatform());
                    data[2].setText(dg.getGenre());
                    data[3].setText(String.valueOf(dg.getStock()));
                    data[4].setText(dg.getSizeGB() + " GB");
                    data[5].setText(dg.getDownloadPlatform());
                    data[6].setText("$" + dg.getPrice());
                    data[7].setText("$" + dg.calculateFinalPrice());
                    noResult.setText("");
                } else if (ph != null) {
                    typeLabel.setText("Tipo: Físico");
                    for (int i = 0; i < 8; i++) headers[i].setText(physicalHeaders[i]);
                    data[0].setText(ph.getTitle());
                    data[1].setText(ph.getPlatform());
                    data[2].setText(ph.getGenre());
                    data[3].setText(String.valueOf(ph.getStock()));
                    data[4].setText(ph.getCondition());
                    data[5].setText(ph.getDistributor());
                    data[6].setText("$" + ph.getPrice());
                    data[7].setText("$" + ph.calculateFinalPrice());
                    noResult.setText("");
                } else {
                    typeLabel.setText("");
                    for (Label dl : data) dl.setText("-");
                    noResult.setText("No se encontró ningún videojuego con ese título.");
                }
            } catch (Exception ex) { showError("Error en búsqueda", ex.getMessage()); }
        });

        Label lblPlat      = new Label("Buscar por plataforma:");
        TextField txtPlat  = new TextField();
        txtPlat.setPromptText("Ej: PS5, Switch, PC");
        txtPlat.setPrefWidth(300);
        Button btnPlat     = new Button("Buscar");
        Label noPlatResult = new Label("");

        ObservableList<String[]> platResults = FXCollections.observableArrayList();
        TableView<String[]> platTable = new TableView<>(platResults);
        platTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        platTable.setPrefHeight(180);

        String[] platCols = {"Tipo", "Título", "Plataforma", "Género", "Stock", "Precio Final"};
        for (int i = 0; i < platCols.length; i++) {
            final int idx = i;
            TableColumn<String[], String> tc = new TableColumn<>(platCols[i]);
            tc.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue()[idx]));
            platTable.getColumns().add(tc);
        }

        btnPlat.setOnAction(e -> {
            try {
                String q = txtPlat.getText().trim();
                if (q.isEmpty()) { showWarn("Escribe una plataforma para buscar."); return; }
                platResults.clear();
                boolean found = false;
                for (DigitalVideoGame g : videoGameService.getAllDigital()) {
                    if (g.getPlatform().equalsIgnoreCase(q)) {
                        platResults.add(new String[]{"Digital", g.getTitle(), g.getPlatform(),
                                g.getGenre(), String.valueOf(g.getStock()), "$" + g.calculateFinalPrice()});
                        found = true;
                    }
                }
                for (PhysicalVideoGame g : videoGameService.getAllPhysical()) {
                    if (g.getPlatform().equalsIgnoreCase(q)) {
                        platResults.add(new String[]{"Físico", g.getTitle(), g.getPlatform(),
                                g.getGenre(), String.valueOf(g.getStock()), "$" + g.calculateFinalPrice()});
                        found = true;
                    }
                }
                noPlatResult.setText(found ? "" : "No se encontraron juegos para esa plataforma.");
            } catch (Exception ex) { showError("Error en búsqueda", ex.getMessage()); }
        });

        VBox tab = new VBox(8,
                lblTitle, new HBox(8, txtTitle, btnTitle),
                typeLabel, resultGrid, noResult,
                new Separator(),
                lblPlat, new HBox(8, txtPlat, btnPlat),
                noPlatResult, platTable
        );
        tab.setPadding(new Insets(12));
        VBox.setVgrow(platTable, Priority.ALWAYS);
        return tab;
    }

    private VBox buildSalesTab() {
        Label lblSell = new Label("Realizar venta:");

        TextField txtGame = new TextField();
        txtGame.setPromptText("Título del videojuego");
        txtGame.setPrefWidth(240);

        TextField txtQty = new TextField();
        txtQty.setPromptText("Cantidad");
        txtQty.setPrefWidth(90);

        ComboBox<String> cmbType = new ComboBox<>();
        cmbType.getItems().addAll("Digital", "Físico");
        cmbType.setValue("Digital");

        Button btnSell = new Button("Vender");

        btnSell.setOnAction(e -> {
            try {
                String title  = txtGame.getText().trim();
                int    qty    = Integer.parseInt(txtQty.getText().trim());
                String saleId = "S-" + System.currentTimeMillis();

                if (title.isEmpty()) { showWarn("Escribe el título del videojuego."); return; }
                if (qty <= 0)        { showWarn("La cantidad debe ser mayor a 0.");   return; }

                Sale sale = "Digital".equals(cmbType.getValue())
                        ? saleService.sellDigitalVideoGame(title, qty, saleId)
                        : saleService.sellPhysicalVideoGame(title, qty, saleId);

                if (sale != null) {
                    showInfo("Venta realizada.\n\n" + sale.toString());
                    refreshSales();
                    refreshDigital();
                    refreshPhysical();
                    txtGame.clear();
                    txtQty.clear();
                }
            } catch (NumberFormatException ex) {
                showError("Formato inválido", "La cantidad debe ser un número entero.");
            } catch (Exception ex) {
                showError("Error en venta", ex.getMessage());
            }
        });

        HBox sellRow = new HBox(8, txtGame, cmbType, txtQty, btnSell);
        sellRow.setAlignment(Pos.CENTER_LEFT);

        Label lblList = new Label("Historial de ventas:");

        TableView<Sale> salesTable = new TableView<>();
        salesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Sale, String>  colId    = col("ID Venta",    "id");
        TableColumn<Sale, String>  colGame  = col("Videojuego",  "videoGameTitle");
        TableColumn<Sale, String>  colType  = col("Tipo",        "videoGameType");
        TableColumn<Sale, Integer> colQty   = col("Cantidad",    "quantity");
        TableColumn<Sale, Double>  colUnit  = col("P. Unitario", "unitPrice");
        TableColumn<Sale, Double>  colTotal = col("Total",       "total");
        TableColumn<Sale, String>  colDate  = col("Fecha",       "saleDate");

        salesTable.getColumns().addAll(colId, colGame, colType, colQty, colUnit, colTotal, colDate);

        salesList = FXCollections.observableArrayList();
        refreshSales();
        salesTable.setItems(salesList);

        Button btnDeleteSale = new Button("Eliminar Venta");
        Button btnRefresh    = new Button("Refrescar");

        btnDeleteSale.setOnAction(e -> {
            Sale sel = salesTable.getSelectionModel().getSelectedItem();
            if (sel == null) { showWarn("Selecciona una venta para eliminar."); return; }
            confirmDelete("la venta " + sel.getId(), () -> {
                try {
                    if (saleService.deleteSale(sel.getId()))
                    { showInfo("Venta eliminada."); refreshSales(); }
                    else showWarn("No se encontró la venta.");
                } catch (Exception ex) { showError("Error al eliminar", ex.getMessage()); }
            });
        });

        btnRefresh.setOnAction(e -> refreshSales());

        HBox toolbar = new HBox(8, btnDeleteSale, btnRefresh);
        toolbar.setPadding(new Insets(4, 0, 4, 0));

        VBox tab = new VBox(8, lblSell, sellRow, new Separator(), lblList, toolbar, salesTable);
        VBox.setVgrow(salesTable, Priority.ALWAYS);
        tab.setPadding(new Insets(12));
        return tab;
    }

    private void refreshDigital() {
        try { digitalList.setAll(videoGameService.getAllDigital()); }
        catch (Exception e) { showError("Error al cargar digitales", e.getMessage()); }
    }

    private void refreshPhysical() {
        try { physicalList.setAll(videoGameService.getAllPhysical()); }
        catch (Exception e) { showError("Error al cargar físicos", e.getMessage()); }
    }

    private void refreshSales() {
        try { salesList.setAll(saleService.getAllSales()); }
        catch (Exception e) { showError("Error al cargar ventas", e.getMessage()); }
    }

    private <T, V> TableColumn<T, V> col(String label, String property) {
        TableColumn<T, V> c = new TableColumn<>(label);
        c.setCellValueFactory(new PropertyValueFactory<>(property));
        return c;
    }

    private ColumnConstraints colConstraint(double width) {
        ColumnConstraints cc = new ColumnConstraints();
        cc.setMinWidth(width);
        cc.setPrefWidth(width);
        return cc;
    }

    private Stage dialog(javafx.stage.Window owner, String title) {
        Stage s = new Stage();
        s.initOwner(owner);
        s.initModality(Modality.WINDOW_MODAL);
        s.setTitle(title);
        s.setResizable(false);
        return s;
    }

    private TextField field(String value, String prompt) {
        TextField tf = new TextField(value);
        tf.setPromptText(prompt);
        tf.setPrefWidth(240);
        return tf;
    }

    private GridPane formGrid(String[] labels, TextField[] fields) {
        GridPane g = new GridPane();
        g.setHgap(10); g.setVgap(10);
        g.setPadding(new Insets(18));
        g.getColumnConstraints().addAll(new ColumnConstraints(130), new ColumnConstraints(240));
        for (int i = 0; i < labels.length; i++) {
            g.add(new Label(labels[i]), 0, i);
            g.add(fields[i],            1, i);
        }
        return g;
    }

    private void confirmDelete(String name, Runnable onConfirm) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar \"" + name + "\"?", ButtonType.YES, ButtonType.NO);
        a.setHeaderText(null);
        a.showAndWait().ifPresent(r -> { if (r == ButtonType.YES) onConfirm.run(); });
    }

    private void showWarn(String msg) {
        new Alert(Alert.AlertType.WARNING,     msg, ButtonType.OK).showAndWait();
    }

    private void showInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }

    private void showError(String header, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error"); a.setHeaderText(header);
        a.setContentText(msg != null ? msg : "Error desconocido.");
        a.showAndWait();
    }

    public static void main(String[] args) { launch(args); }
}
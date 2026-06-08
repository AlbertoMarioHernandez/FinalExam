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
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("GameZone");
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.getTabs().addAll(
                new Tab("Videojuegos",       buildVideoGamesTab()),
                new Tab("Listar Todos",       buildListAllTab()),
                new Tab("Buscar por Título",  buildSearchByTitleTab()),
                new Tab("Buscar Plataforma",  buildSearchByPlatformTab()),
                new Tab("Realizar Venta",     buildSellTab()),
                new Tab("Mostrar Ventas",     buildSalesTab())
        );

        primaryStage.setScene(new Scene(tabPane, 960, 600));
        primaryStage.show();
    }


    private VBox buildVideoGamesTab() {
        Label lblDigital = new Label("Videojuegos Digitales");
        lblDigital.setStyle("-fx-font-weight: bold;");

        TableView<DigitalVideoGame> digitalTable = new TableView<>();
        digitalTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        digitalTable.setPrefHeight(220);

        TableColumn<DigitalVideoGame, String>  dColTitle    = col("Título",      "title");
        TableColumn<DigitalVideoGame, String>  dColPlatform = col("Plataforma",  "platform");
        TableColumn<DigitalVideoGame, String>  dColGenre    = col("Género",      "genre");
        TableColumn<DigitalVideoGame, Integer> dColStock    = col("Stock",       "stock");
        TableColumn<DigitalVideoGame, Double>  dColSize     = col("GB",          "sizeGB");
        TableColumn<DigitalVideoGame, String>  dColDl       = col("Descarga en", "downloadPlatform");
        TableColumn<DigitalVideoGame, Double>  dColFinal    = new TableColumn<>("Precio Final");
        dColFinal.setCellValueFactory(d ->
                new SimpleDoubleProperty(d.getValue().calculateFinalPrice()).asObject());

        digitalTable.getColumns().addAll(dColTitle, dColPlatform, dColGenre, dColStock, dColSize, dColDl, dColFinal);
        digitalList = FXCollections.observableArrayList();
        digitalTable.setItems(digitalList);

        Button btnAddDigital    = new Button("Agregar Digital");
        Button btnEditDigital   = new Button("Editar Digital");
        Button btnDeleteDigital = new Button("Eliminar Digital");
        Button btnListDigital   = new Button("Listar Digitales");

        btnListDigital.setOnAction(e -> refreshDigital());

        btnAddDigital.setOnAction(e ->
                showDigitalForm(null, digitalTable.getScene().getWindow()));

        btnEditDigital.setOnAction(e -> {
            DigitalVideoGame sel = digitalTable.getSelectionModel().getSelectedItem();
            if (sel == null) { showWarn("Selecciona un videojuego para editar."); return; }
            showDigitalForm(sel, digitalTable.getScene().getWindow());
        });

        btnDeleteDigital.setOnAction(e -> {
            DigitalVideoGame sel = digitalTable.getSelectionModel().getSelectedItem();
            if (sel == null) { showWarn("Selecciona un videojuego para eliminar."); return; }
            confirmDelete(sel.getTitle(), () -> {
                try {
                    if (videoGameService.deleteDigitalVideoGame(sel.getTitle()))
                    { showInfo("Videojuego eliminado."); refreshDigital(); }
                    else showWarn("No se encontró el videojuego.");
                } catch (Exception ex) { showError("Error al eliminar", ex.getMessage()); }
            });
        });

        HBox digitalToolbar = new HBox(8, btnAddDigital, btnEditDigital, btnDeleteDigital, btnListDigital);
        digitalToolbar.setPadding(new Insets(6, 0, 6, 0));

        Label lblPhysical = new Label("Videojuegos Físicos");
        lblPhysical.setStyle("-fx-font-weight: bold;");

        TableView<PhysicalVideoGame> physicalTable = new TableView<>();
        physicalTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        physicalTable.setPrefHeight(220);

        TableColumn<PhysicalVideoGame, String>  pColTitle    = col("Título",       "title");
        TableColumn<PhysicalVideoGame, String>  pColPlatform = col("Plataforma",   "platform");
        TableColumn<PhysicalVideoGame, String>  pColGenre    = col("Género",       "genre");
        TableColumn<PhysicalVideoGame, Integer> pColStock    = col("Stock",        "stock");
        TableColumn<PhysicalVideoGame, String>  pColCond     = col("Condición",    "condition");
        TableColumn<PhysicalVideoGame, String>  pColDist     = col("Distribuidor", "distributor");
        TableColumn<PhysicalVideoGame, Double>  pColFinal    = new TableColumn<>("Precio Final");
        pColFinal.setCellValueFactory(d -> new SimpleDoubleProperty(d.getValue().calculateFinalPrice()).asObject());

        physicalTable.getColumns().addAll(pColTitle, pColPlatform, pColGenre, pColStock, pColCond, pColDist, pColFinal);
        physicalList = FXCollections.observableArrayList();
        physicalTable.setItems(physicalList);

        Button btnAddPhysical    = new Button("Agregar Físico");
        Button btnEditPhysical   = new Button("Editar Físico");
        Button btnDeletePhysical = new Button("Eliminar Físico");
        Button btnListPhysical   = new Button("Listar Físicos");

        btnListPhysical.setOnAction(e -> refreshPhysical());

        btnAddPhysical.setOnAction(e -> showPhysicalForm(null, physicalTable.getScene().getWindow()));

        btnEditPhysical.setOnAction(e -> {
            PhysicalVideoGame sel = physicalTable.getSelectionModel().getSelectedItem();
            if (sel == null) { showWarn("Selecciona un videojuego para editar."); return; }
            showPhysicalForm(sel, physicalTable.getScene().getWindow());
        });

        btnDeletePhysical.setOnAction(e -> {
            PhysicalVideoGame sel = physicalTable.getSelectionModel().getSelectedItem();
            if (sel == null) { showWarn("Selecciona un videojuego para eliminar."); return; }
            confirmDelete(sel.getTitle(), () -> {
                try {
                    if (videoGameService.deletePhysicalVideoGame(sel.getTitle()))
                    { showInfo("Videojuego eliminado."); refreshPhysical(); }
                    else showWarn("No se encontró el videojuego.");
                } catch (Exception ex) { showError("Error al eliminar", ex.getMessage()); }
            });
        });

        HBox physicalToolbar = new HBox(8, btnAddPhysical, btnEditPhysical, btnDeletePhysical, btnListPhysical);
        physicalToolbar.setPadding(new Insets(6, 0, 6, 0));

        VBox tab = new VBox(6, lblDigital, digitalToolbar, digitalTable, new Separator(), lblPhysical, physicalToolbar, physicalTable);
        tab.setPadding(new Insets(10));
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


    private VBox buildListAllTab() {
        Label lblDigital = new Label("Videojuegos Digitales");
        lblDigital.setStyle("-fx-font-weight: bold;");

        TableView<DigitalVideoGame> dTable = new TableView<>();
        dTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        dTable.setPrefHeight(210);

        TableColumn<DigitalVideoGame, String>  c1 = col("Título",      "title");
        TableColumn<DigitalVideoGame, String>  c2 = col("Plataforma",  "platform");
        TableColumn<DigitalVideoGame, String>  c3 = col("Género",      "genre");
        TableColumn<DigitalVideoGame, Integer> c4 = col("Stock",       "stock");
        TableColumn<DigitalVideoGame, Double>  c5 = col("GB",          "sizeGB");
        TableColumn<DigitalVideoGame, String>  c6 = col("Descarga en", "downloadPlatform");
        TableColumn<DigitalVideoGame, Double>  c7 = new TableColumn<>("Precio Final");
        c7.setCellValueFactory(d -> new SimpleDoubleProperty(d.getValue().calculateFinalPrice()).asObject());
        dTable.getColumns().addAll(c1, c2, c3, c4, c5, c6, c7);
        ObservableList<DigitalVideoGame> dList = FXCollections.observableArrayList();
        dTable.setItems(dList);

        Label lblPhysical = new Label("Videojuegos Físicos");
        lblPhysical.setStyle("-fx-font-weight: bold;");

        TableView<PhysicalVideoGame> pTable = new TableView<>();
        pTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        pTable.setPrefHeight(210);

        TableColumn<PhysicalVideoGame, String>  p1 = col("Título",       "title");
        TableColumn<PhysicalVideoGame, String>  p2 = col("Plataforma",   "platform");
        TableColumn<PhysicalVideoGame, String>  p3 = col("Género",       "genre");
        TableColumn<PhysicalVideoGame, Integer> p4 = col("Stock",        "stock");
        TableColumn<PhysicalVideoGame, String>  p5 = col("Condición",    "condition");
        TableColumn<PhysicalVideoGame, String>  p6 = col("Distribuidor", "distributor");
        TableColumn<PhysicalVideoGame, Double>  p7 = new TableColumn<>("Precio Final");
        p7.setCellValueFactory(d -> new SimpleDoubleProperty(d.getValue().calculateFinalPrice()).asObject());
        pTable.getColumns().addAll(p1, p2, p3, p4, p5, p6, p7);
        ObservableList<PhysicalVideoGame> pList = FXCollections.observableArrayList();
        pTable.setItems(pList);

        Button btnListAll = new Button("Listar Todos los Juegos");
        btnListAll.setOnAction(e -> {
            try {
                dList.setAll(videoGameService.getAllDigital());
                pList.setAll(videoGameService.getAllPhysical());
            } catch (Exception ex) { showError("Error al listar", ex.getMessage()); }
        });

        VBox tab = new VBox(8,
                btnListAll,
                new Separator(),
                lblDigital, dTable,
                new Separator(),
                lblPhysical, pTable
        );
        tab.setPadding(new Insets(10));
        return tab;
    }

    private VBox buildSearchByTitleTab() {
        Label lbl          = new Label("Búsqueda por Título:");
        TextField txtTitle = new TextField();
        txtTitle.setPromptText("Escribe el título del videojuego");
        txtTitle.setPrefWidth(320);
        Button btnSearch   = new Button("Buscar por Título");

        String[] digitalHeaders  = {"Título", "Plataforma", "Género", "Stock", "Tamaño GB", "Descarga en",  "Precio Base", "Precio Final"};
        String[] physicalHeaders = {"Título", "Plataforma", "Género", "Stock", "Condición", "Distribuidor", "Precio Base", "Precio Final"};

        Label typeLabel = new Label("");
        typeLabel.setStyle("-fx-font-weight: bold;");
        Label[] headers = new Label[8];
        Label[] data    = new Label[8];
        Label noResult  = new Label("");

        GridPane resultGrid = new GridPane();
        resultGrid.setHgap(16);
        resultGrid.setVgap(8);
        resultGrid.setPadding(new Insets(10, 0, 10, 0));

        int[] widths = {120, 90, 80, 50, 90, 140, 90, 90};
        for (int w : widths) resultGrid.getColumnConstraints().add(colConstraint(w));

        for (int i = 0; i < 8; i++) {
            headers[i] = new Label(digitalHeaders[i]);
            headers[i].setStyle("-fx-font-weight: bold; -fx-underline: true;");
            data[i] = new Label("-");
            resultGrid.add(headers[i], i, 0);
            resultGrid.add(data[i],    i, 1);
        }

        btnSearch.setOnAction(e -> {
            try {
                String q = txtTitle.getText().trim();
                if (q.isEmpty()) { showWarn("Escribe un título para buscar."); return; }

                DigitalVideoGame  dg = videoGameService.findDigitalByTitle(q);
                PhysicalVideoGame ph = videoGameService.findPhysicalByTitle(q);

                if (dg != null) {
                    typeLabel.setText("Resultado — Tipo: Digital");
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
                    typeLabel.setText("Resultado — Tipo: Físico");
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

        VBox tab = new VBox(10, lbl, new HBox(8, txtTitle, btnSearch), typeLabel, resultGrid, noResult);
        tab.setPadding(new Insets(12));
        return tab;
    }

    private VBox buildSearchByPlatformTab() {
        Label lbl         = new Label("Búsqueda por Plataforma:");
        TextField txtPlat = new TextField();
        txtPlat.setPromptText("Ej: PS5, Switch, PC");
        txtPlat.setPrefWidth(320);
        Button btnSearch  = new Button("Buscar por Plataforma");
        Label noResult    = new Label("");

        ObservableList<String[]> results = FXCollections.observableArrayList();
        TableView<String[]> table = new TableView<>(results);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        String[] platCols = {"Tipo", "Título", "Plataforma", "Género", "Stock", "Precio Final"};
        for (int i = 0; i < platCols.length; i++) {
            final int idx = i;
            TableColumn<String[], String> tc = new TableColumn<>(platCols[i]);
            tc.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue()[idx]));
            table.getColumns().add(tc);
        }

        btnSearch.setOnAction(e -> {
            try {
                String q = txtPlat.getText().trim();
                if (q.isEmpty()) { showWarn("Escribe una plataforma para buscar."); return; }
                results.clear();
                boolean found = false;
                for (DigitalVideoGame g : videoGameService.getAllDigital()) {
                    if (g.getPlatform().equalsIgnoreCase(q)) {
                        results.add(new String[]{"Digital", g.getTitle(), g.getPlatform(),
                                g.getGenre(), String.valueOf(g.getStock()), "$" + g.calculateFinalPrice()});
                        found = true;
                    }
                }
                for (PhysicalVideoGame g : videoGameService.getAllPhysical()) {
                    if (g.getPlatform().equalsIgnoreCase(q)) {
                        results.add(new String[]{"Físico", g.getTitle(), g.getPlatform(),
                                g.getGenre(), String.valueOf(g.getStock()), "$" + g.calculateFinalPrice()});
                        found = true;
                    }
                }
                noResult.setText(found ? "" : "No se encontraron juegos para esa plataforma.");
            } catch (Exception ex) { showError("Error en búsqueda", ex.getMessage()); }
        });

        VBox tab = new VBox(10, lbl, new HBox(8, txtPlat, btnSearch), noResult, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        tab.setPadding(new Insets(12));
        return tab;
    }
    private VBox buildSellTab() {
        Label lblTitle  = new Label("Título del videojuego:");
        TextField txtGame = new TextField();
        txtGame.setPromptText("Título del videojuego");
        txtGame.setPrefWidth(260);

        Label lblQty    = new Label("Cantidad:");
        TextField txtQty = new TextField();
        txtQty.setPromptText("Cantidad");
        txtQty.setPrefWidth(100);

        Label lblType   = new Label("Tipo:");
        ComboBox<String> cmbType = new ComboBox<>();
        cmbType.getItems().addAll("Digital", "Físico");
        cmbType.setValue("Digital");

        Button btnSell = new Button("Realizar Venta");

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
                    showInfo("Venta realizada correctamente.\n\n" + sale.toString());
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

        GridPane form = new GridPane();
        form.setHgap(10); form.setVgap(12);
        form.setPadding(new Insets(14));
        form.add(lblTitle,  0, 0); form.add(txtGame,  1, 0);
        form.add(lblType,   0, 1); form.add(cmbType,  1, 1);
        form.add(lblQty,    0, 2); form.add(txtQty,   1, 2);
        form.add(btnSell,   1, 3);

        VBox tab = new VBox(10, new Label("Datos de la Venta:"), form);
        tab.setPadding(new Insets(12));
        return tab;
    }

    private VBox buildSalesTab() {
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
        salesTable.setItems(salesList);

        Button btnMostrar = new Button("Mostrar Todas las Ventas");
        btnMostrar.setOnAction(e -> {
            try { salesList.setAll(saleService.getAllSales()); }
            catch (Exception ex) { showError("Error al cargar ventas", ex.getMessage()); }
        });

        VBox tab = new VBox(8, btnMostrar, salesTable);
        VBox.setVgrow(salesTable, Priority.ALWAYS);
        tab.setPadding(new Insets(10));
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


}
package service;

import entity.DigitalVideoGame;
import entity.PhysicalVideoGame;
import entity.Sale;
import javafx.scene.control.Alert;
import repository.SaleRepository;
import repository.VideoGameRepository;

import java.util.ArrayList;

public class SaleServiceImpl implements ISaleService {

    private final SaleRepository saleRepository = new SaleRepository();
    private final VideoGameRepository videoGameRepository = new VideoGameRepository();

    // ================================================================
    //  SELL OPERATIONS
    // ================================================================

    @Override
    public Sale sellDigitalVideoGame(String title, int quantity, String saleId) {
        // 1. Search the game in the JSON
        DigitalVideoGame game = videoGameRepository.findDigitalByTitle(title);

        // 2. Verify it exists
        if (game == null) {
            showAlert("Venta fallida", "El videojuego '" + title + "' no existe en el catálogo.");
            return null;
        }

        // 3. Verify sufficient stock
        if (game.getStock() < quantity) {
            showAlert("Stock insuficiente", "Stock disponible: " + game.getStock() + ". Cantidad solicitada: " + quantity + ".");
            return null;
        }

        // 4. Reduce stock and persist to JSON
        game.setStock(game.getStock() - quantity);
        videoGameRepository.updateDigital(title, game);

        // 5. calculateFinalPrice() delegated to entity — create sale
        // Sale constructor: (id, videoGame, quantity, unitPrice) → calculates total internally
        double unitPrice = game.calculateFinalPrice();
        Sale sale = new Sale(saleId, game, quantity, unitPrice);
        saleRepository.save(sale);

        return sale;
    }

    @Override
    public Sale sellPhysicalVideoGame(String title, int quantity, String saleId) {
        // 1. Search the game in the JSON
        PhysicalVideoGame game = videoGameRepository.findPhysicalByTitle(title);

        // 2. Verify it exists
        if (game == null) {
            showAlert("Venta fallida", "El videojuego '" + title + "' no existe en el catálogo.");
            return null;
        }

        // 3. Verify sufficient stock
        if (game.getStock() < quantity) {
            showAlert("Stock insuficiente",
                    "Stock disponible: " + game.getStock() + ". Cantidad solicitada: " + quantity + ".");
            return null;
        }

        // 4. Reduce stock and persist to JSON
        game.setStock(game.getStock() - quantity);
        videoGameRepository.updatePhysical(title, game);

        // 5. calculateFinalPrice() delegated to entity — create sale
        // Sale constructor: (id, videoGame, quantity, unitPrice) → calculates total internally
        double unitPrice = game.calculateFinalPrice();
        Sale sale = new Sale(saleId, game, quantity, unitPrice);
        saleRepository.save(sale);

        return sale;
    }

    // ================================================================
    //  SALE CRUD
    // ================================================================

    @Override
    public ArrayList<Sale> getAllSales() {
        return saleRepository.getAll();
    }

    @Override
    public Sale findSaleById(String id) {
        return saleRepository.findById(id);
    }

    @Override
    public boolean deleteSale(String id) {
        return saleRepository.delete(id);
    }

    // ================================================================
    //  ALERT HELPER
    // ================================================================

    private void showAlert(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
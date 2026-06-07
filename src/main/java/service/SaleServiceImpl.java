package service;

import entity.DigitalVideoGame;
import entity.PhysicalVideoGame;
import entity.Sale;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import repository.SaleRepository;
import repository.VideoGameRepository;

import java.util.ArrayList;

public class SaleServiceImpl implements ISaleService {

    private final SaleRepository      saleRepository      = new SaleRepository();
    private final VideoGameRepository videoGameRepository = new VideoGameRepository();

    @Override
    public Sale sellDigitalVideoGame(String title, int quantity, String saleId) {
        DigitalVideoGame game = videoGameRepository.findDigitalByTitle(title);

        if (game == null) {
            showAlert("Venta fallida", "El videojuego '" + title + "' no existe en el catálogo.");
            return null;
        }

        if (game.getStock() < quantity) {
            showAlert("Stock insuficiente", "Stock disponible: " + game.getStock() + ". Cantidad solicitada: " + quantity + ".");
            return null;
        }

        game.setStock(game.getStock() - quantity);
        videoGameRepository.updateDigital(title, game);

        double unitPrice = game.calculateFinalPrice();
        Sale sale = new Sale(saleId, game, quantity, unitPrice);
        saleRepository.save(sale);
        return sale;
    }

    @Override
    public Sale sellPhysicalVideoGame(String title, int quantity, String saleId) {
        PhysicalVideoGame game = videoGameRepository.findPhysicalByTitle(title);

        if (game == null) {
            showAlert("Venta fallida", "El videojuego '" + title + "' no existe en el catálogo.");
            return null;
        }

        if (game.getStock() < quantity) {
            showAlert("Stock insuficiente",
                    "Stock disponible: " + game.getStock() + ". Cantidad solicitada: " + quantity + ".");
            return null;
        }

        game.setStock(game.getStock() - quantity);
        videoGameRepository.updatePhysical(title, game);

        double unitPrice = game.calculateFinalPrice();
        Sale sale = new Sale(saleId, game, quantity, unitPrice);
        saleRepository.save(sale);
        return sale;
    }

    @Override
    public ArrayList<Sale> getAllSales() {
        return saleRepository.getAll();
    }
    private void showAlert(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
        alert.setHeaderText(header);
        alert.showAndWait();
    }
}
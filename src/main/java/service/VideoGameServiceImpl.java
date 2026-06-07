package service;

import entity.DigitalVideoGame;
import entity.PhysicalVideoGame;
import javafx.scene.control.Alert;
import repository.VideoGameRepository;

import java.util.ArrayList;

public class VideoGameServiceImpl implements IDigitalVideoGameService, IPhysicalVideoGameService {

    private final VideoGameRepository repository = new VideoGameRepository();



    private void validateVideoGame(String title, double price, int stock) {
        if (title == null || title.trim().isEmpty())
            throw new IllegalArgumentException("El título no puede ser nulo o vacío.");
        if (price <= 0)
            throw new IllegalArgumentException("El precio debe ser mayor a 0.");
        if (stock <= 0)
            throw new IllegalArgumentException("El stock no puede ser negativo o igual a 0.");
    }

    private void showAlert(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void addDigitalVideoGame(DigitalVideoGame game) {
        validateVideoGame(game.getTitle(), game.getPrice(), game.getStock());

        if (repository.findDigitalByTitle(game.getTitle()) != null) {
            showAlert("Videojuego duplicado", "El videojuego ya existe en el catálogo.");
            return;
        }

        repository.saveDigital(game);
    }

    @Override
    public ArrayList<DigitalVideoGame> getAllDigital() {
        return repository.getAllDigital();
    }

    @Override
    public DigitalVideoGame findDigitalByTitle(String title) {
        return repository.findDigitalByTitle(title);
    }

    @Override
    public boolean updateDigitalVideoGame(String title, DigitalVideoGame updated) {
        validateVideoGame(updated.getTitle(), updated.getPrice(), updated.getStock());
        return repository.updateDigital(title, updated);
    }

    @Override
    public boolean deleteDigitalVideoGame(String title) {
        return repository.deleteDigital(title);
    }
    @Override
    public void addPhysicalVideoGame(PhysicalVideoGame game) {
        validateVideoGame(game.getTitle(), game.getPrice(), game.getStock());

        if (repository.findPhysicalByTitle(game.getTitle()) != null) {
            showAlert("Videojuego duplicado", "El videojuego ya existe en el catálogo.");
            return;
        }

        repository.savePhysical(game);
    }

    @Override
    public ArrayList<PhysicalVideoGame> getAllPhysical() {
        return repository.getAllPhysical();
    }

    @Override
    public PhysicalVideoGame findPhysicalByTitle(String title) {
        return repository.findPhysicalByTitle(title);
    }

    @Override
    public boolean updatePhysicalVideoGame(String title, PhysicalVideoGame updated) {
        validateVideoGame(updated.getTitle(), updated.getPrice(), updated.getStock());
        return repository.updatePhysical(title, updated);
    }

    @Override
    public boolean deletePhysicalVideoGame(String title) {
        return repository.deletePhysical(title);
    }
}
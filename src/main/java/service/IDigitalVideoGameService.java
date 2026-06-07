package service;

import entity.DigitalVideoGame;
import java.util.ArrayList;

public interface IDigitalVideoGameService {
    void addDigitalVideoGame(DigitalVideoGame game);
    ArrayList<DigitalVideoGame> getAllDigital();
    DigitalVideoGame findDigitalByTitle(String title);
    boolean updateDigitalVideoGame(String title, DigitalVideoGame updated);
    boolean deleteDigitalVideoGame(String title);

}

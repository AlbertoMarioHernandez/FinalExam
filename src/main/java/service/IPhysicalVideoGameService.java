package service;

import entity.PhysicalVideoGame;
import java.util.ArrayList;

public interface IPhysicalVideoGameService {
    void addPhysicalVideoGame(PhysicalVideoGame game);
    ArrayList<PhysicalVideoGame> getAllPhysical();
    PhysicalVideoGame findPhysicalByTitle(String title);
    boolean updatePhysicalVideoGame(String title, PhysicalVideoGame updated);
    boolean deletePhysicalVideoGame(String title);
    double calculateFinalPricePhysical(PhysicalVideoGame game);
}

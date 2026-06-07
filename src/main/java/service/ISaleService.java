package service;

import entity.Sale;
import java.util.ArrayList;

public interface ISaleService {
    Sale sellDigitalVideoGame(String title, int quantity, String saleId);
    Sale sellPhysicalVideoGame(String title, int quantity, String saleId);
    ArrayList<Sale> getAllSales();

}

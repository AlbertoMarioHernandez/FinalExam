package entity;

import java.time.LocalDateTime;

public class Sale {

    private final String id;
    private final String videoGameTitle;  // ← solo el título (serializable sin problema)
    private final String videoGameType;   // ← "Digital" o "Fisico" para saber cuál buscar
    private final int quantity;
    private final double unitPrice;
    private final double total;
    private final LocalDateTime saleDate;

    public Sale(String id, VideoGame videoGame, int quantity, double unitPrice) {
        this.id             = id;
        this.videoGameTitle = videoGame.getTitle();
        this.videoGameType  = videoGame instanceof DigitalVideoGame ? "Digital" : "Fisico";
        this.quantity       = quantity;
        this.unitPrice      = unitPrice;
        this.total          = unitPrice * quantity;
        this.saleDate       = LocalDateTime.now();
    }

    public String getId()             {
        return id;
    }
    public String getVideoGameTitle() {
        return videoGameTitle;
    }
    public String getVideoGameType()  {
        return videoGameType;
    }
    public int getQuantity()          {
        return quantity;
    }
    public double getUnitPrice()      {
        return unitPrice;
    }
    public double getTotal()          {
        return total;
    }
    public LocalDateTime getSaleDate(){
        return saleDate;
    }

    @Override
    public String toString() {
        return "Sale{id='" + id
                + "', videoGame='" + videoGameTitle
                + "', type='" + videoGameType
                + "', quantity=" + quantity
                + ", unitPrice=" + unitPrice
                + ", total=" + total
                + ", saleDate=" + saleDate + "}";
    }
}
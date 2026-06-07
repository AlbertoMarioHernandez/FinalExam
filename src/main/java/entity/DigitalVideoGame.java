package entity;

public class DigitalVideoGame extends VideoGame {

    private double sizeGB;
    private String downloadPlatform;

    public DigitalVideoGame(String title, double price, String platform, int stock, String genre, double sizeGB, String downloadPlatform) {
        super(title, price, platform, stock, genre);
        this.sizeGB = sizeGB;
        this.downloadPlatform = downloadPlatform;
    }

    public double getSizeGB() { return sizeGB; }
    public String getDownloadPlatform() { return downloadPlatform; }
    public void setSizeGB(double sizeGB) { this.sizeGB = sizeGB; }
    public void setDownloadPlatform(String downloadPlatform) { this.downloadPlatform = downloadPlatform; }

    @Override
    public double calculateFinalPrice() {
        if(this.sizeGB>50){
            return price+5000;
        }
        return price;
    }

    public double sell(int qty) {
        if (qty > stock) throw new IllegalArgumentException("Stock insuficiente");
        stock -= qty;
        return calculateFinalPrice() * qty;
    }

    public String getDisplayInfo() {
        return "Digital | " + title + " | " + platform + " | $" + calculateFinalPrice() + " | " + sizeGB + " GB | " + downloadPlatform;
    }

    public Object[] toTableRow() {
        return new Object[]{title, platform, genre, sizeGB + " GB", downloadPlatform, calculateFinalPrice(), stock};
    }

    @Override
    public String toString() {
        return "DigitalVideoGame{title='" + title
                + "', price=" + price
                + ", platform='" + platform
                + "', stock=" + stock
                + ", genre='" + genre
                + "', sizeGB=" + sizeGB
                + ", downloadPlatform='" + downloadPlatform + "'}";
    }
}

package entity;

public class DigitalVideoGame extends VideoGame {

    private double sizeGB;
    private String downloadPlatform;

    public DigitalVideoGame(String title, double price, String platform, int stock, String genre, double sizeGB, String downloadPlatform) {
        super(title, price, platform, stock, genre);
        this.sizeGB = sizeGB;
        this.downloadPlatform = downloadPlatform;
    }

    public double getSizeGB() {
        return sizeGB;
    }
    public String getDownloadPlatform() {
        return downloadPlatform;
    }
    public void setSizeGB(double sizeGB) {
        this.sizeGB = sizeGB;
    }
    public void setDownloadPlatform(String downloadPlatform) {
        this.downloadPlatform = downloadPlatform;
    }

    @Override
    public double calculateFinalPrice() {
        if(this.sizeGB>50){
            return price+5000;
        }
        return price;
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

package entity;

public class PhysicalVideoGame extends VideoGame {

    private String condition;
    private String distributor;

    public PhysicalVideoGame(String title, double price, String platform, int stock, String genre, String condition, String distributor) {
        super(title, price, platform, stock, genre);
        this.condition = condition;
        this.distributor = distributor;
    }

    public String getCondition() {
        return condition;
    }
    public String getDistributor() {
        return distributor;
    }
    public void setCondition(String condition) {
        this.condition = condition;
    }
    public void setDistributor(String distributor) {
        this.distributor = distributor;
    }

    @Override
    public double calculateFinalPrice() {
        if(condition.equalsIgnoreCase("usado")){
            return price * 0.75;
        }
        return price;

    }

    public double sell(int qty) {
        if (qty > stock) throw new IllegalArgumentException("Stock insuficiente");
        stock -= qty;
        return calculateFinalPrice() * qty;
    }

    public String getDisplayInfo() {
        return "Physical | " + title + " | " + platform + " | $" + calculateFinalPrice() + " | Condition: " + condition + " | Distributor: " + distributor;
    }

    public Object[] toTableRow() {
        return new Object[]{title, platform, genre, condition, distributor, calculateFinalPrice(), stock};
    }

    @Override
    public String toString() {
        return "PhysicalVideoGame{title='" + title
                + "', price=" + price
                + ", platform='" + platform
                + "', stock=" + stock
                + ", genre='" + genre
                + "', condition='" + condition
                + "', distributor='" + distributor + "'}";
    }
}

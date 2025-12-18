package ref;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

public class WaterTank {
    public static final String ANSI_YELLOW = "\u001b[33m";
    private String brand;
    private String color;
    private float capacity;
    private float level;

    public WaterTank() {
        this.capacity = 10000.0F;
        this.level = 0.0F;
    }

    public WaterTank(float capacity) {
        this.capacity = capacity;
        this.level = 0.0F;
    }

    public WaterTank(String brand, float capacity) {
        this.brand = brand;
        this.capacity = capacity;
        this.level = 0.0F;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setCapacity(float capacity) {
        this.capacity = capacity;
    }

    public String getBrand() {
        return this.brand;
    }

    public String getColor() {
        return this.color;
    }

    public double getCapacity() {
        return (double)this.capacity;
    }

    public void addWater(float amount) {
        this.level += amount;
    }

    public void fillWater(float amount) {
        if (!this.isFull()) {
            this.level += amount;
        } else {
            System.err.println("Tank is full!");
        }

    }

    public String refill(float amount) {
        if (!this.isFull()) {
            this.level += amount;
            return amount + "L is added to Tank!";
        } else {
            return "Tank is full!";
        }
    }

    public void removeWater(float amount) {
        this.level -= amount;
    }

    public float currentWater() {
        return this.level;
    }

    public boolean isEmpty() {
        return this.level == 0.0F;
    }

    public boolean isFull() {
        return this.level >= this.capacity;
    }

    public float clearWater() {
        float wastage = this.level;
        this.level = 0.0F;
        return wastage;
    }

    public void showWTInfo() {
        System.out.println("Brand\t\t: " + this.brand);
        System.out.println("Color\t\t: " + this.color);
        System.out.println("Capacity\t: " + this.capacity + "L");
        System.out.println("Water Level\t: " + this.level + "L");
        System.out.println("isEmpty\t\t: " + this.isEmpty());
        System.out.println("isFull\t\t: " + this.isFull());
    }

    public String displayWTInfo() {
        String hold = "Brand\t\t: " + this.brand + "\nColor\t\t: " + this.color + "\nCapacity\t: " + this.capacity + "L" + "\nWater Level\t: " + this.level + "L" + "\nisEmpty\t\t: " + this.isEmpty() + "\nisFull\t\t: " + this.isFull();
        return hold;
    }

    public String viewWTInfo() {
        String hold = "Brand\t: " + this.brand + "\tColor\t: " + this.color + "\nCapacity\t: " + this.capacity + "L" + "\tWater Level\t: " + this.level + "L" + "\nisEmpty\t: " + this.isEmpty() + "\tisFull\t: " + this.isFull() + "\nRemaining Capacity : " + this.remainingCapacity() + "L";
        return hold;
    }

    public float remainingCapacity() {
        return this.capacity - this.level;
    }

    public String drawTank(String ch) {
        String display = "";
        int tankHeight = 0;
        int liter = 0;
        if (this.capacity <= 100.0F) {
            liter = 10;
            tankHeight = (int)this.capacity / liter;
        } else if (this.capacity <= 1000.0F) {
            liter = 100;
            tankHeight = (int)this.capacity / liter;
        } else if (this.capacity <= 10000.0F) {
            liter = 1000;
            tankHeight = (int)this.capacity / 1000;
        } else {
            liter = 10000;
            tankHeight = (int)this.capacity / 10000;
        }

        String get = "<-" + this.level + "L";

        for(int i = tankHeight; i > 0; --i) {
            display = display + liter * i + "L|";
            if ((float)i <= this.level / (float)liter) {
                display = display + ch.repeat(20) + "|" + get;
                get = "";
            } else {
                display = display + "\t\t   |";
            }

            display = display + "\n";
        }

        display = display + "      0L(_______________________)";
        return "          ^^^^^^^^^^^^^^^^^^^^^\n" + display;
    }
}

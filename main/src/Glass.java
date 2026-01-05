public class Glass {

    public enum Liquid {
        WATER, MILK, MILO
    }

    private int level;
    private final int capacity;
    private Liquid currentLiquid;

    public Glass(int capacity) {
        this.capacity = capacity;
        this.level = 0;
        this.currentLiquid = null;
    }

    public boolean addUnit(Liquid liquid) {
        if (level < capacity) {
            level++;
            currentLiquid = liquid;
            return true;
        }
        return false;
    }

    public boolean drinkAll() {
        if (level > 0) {
            level = 0;
            currentLiquid = null;
            return true;
        }
        return false;
    }

    public boolean removeUnit() {
        if (level > 0) {
            level--;
            if (level == 0) currentLiquid = null;
            return true;
        }
        return false;
    }

    public int getLevel() {
        return level;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isFull() {
        return level == capacity;
    }

    public boolean isEmpty() {
        return level == 0;
    }

    public Liquid getCurrentLiquid() {
        return currentLiquid;
    }
}

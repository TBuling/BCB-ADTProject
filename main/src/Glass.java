public class Glass {
    enum Liquid { WATER, MILK, MILO }
    private final int capacity;
    private int level = 0;
    private Liquid current = null;

    public Glass(int capacity) { this.capacity = capacity; }

    public int getCapacity() { return capacity; }
    public int getLevel() { return level; }
    public boolean isEmpty() { return level == 0; }
    public boolean isFull() { return level >= capacity; }
    public Liquid getCurrentLiquid() { return current; }

    public boolean addUnit(Liquid type) {
        if (isFull()) return false;
        level++;
        current = type;
        return true;
    }

    public void removeUnit() {
        if (!isEmpty()) level--;
        if (isEmpty()) current = null;
    }

    public void drinkAll() {
        level = 0;
        current = null;
    }
}

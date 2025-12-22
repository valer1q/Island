package model.animals;

import config.Settings;
import model.Cell;
import java.util.Random;

public abstract class Animal {
    protected int x, y;
    public double satiety;
    protected boolean isAlive;
    protected int type;
    protected int age;
    protected int maxAge;
    protected Random random;
    protected String symbol;
    protected boolean canReproduce;

    public Animal(int x, int y, int type, int maxAge) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.satiety = Settings.ANIMAL_DATA[type].foodRequired;
        this.isAlive = true;
        this.age = 0;
        this.maxAge = maxAge;
        this.random = new Random();
        this.symbol = getSymbol(type);
        this.canReproduce = false;
    }

    private String getSymbol(int type) {
        switch(type) {
            case Settings.WOLF: return "🐺";
            case Settings.BOA: return "🐍";
            case Settings.FOX: return "🦊";
            case Settings.BEAR: return "🐻";
            case Settings.EAGLE: return "🦅";
            case Settings.HORSE: return "🐎";
            case Settings.DEER: return "🦌";
            case Settings.RABBIT: return "🐇";
            case Settings.MOUSE: return "🐁";
            case Settings.GOAT: return "🐐";
            case Settings.SHEEP: return "🐑";
            case Settings.BOAR: return "🐗";
            case Settings.BUFFALO: return "🐃";
            case Settings.DUCK: return "🦆";
            case Settings.CATERPILLAR: return "🐛";
            default: return "?";
        }
    }

    public abstract void eat(Cell cell);
    public abstract void move(Cell[][] island);
    public abstract void reproduce(Cell cell);

    public void decreaseSatiety() {
        satiety -= Settings.ANIMAL_DATA[type].foodRequired * 0.05;
        if (satiety <= 0) {
            isAlive = false;
        }
    }

    public void age() {
        age++;
        if (age >= 3 && !canReproduce) {
            canReproduce = true;
        }

        if (age > maxAge && random.nextInt(100) < (age - maxAge) * 10) {
            isAlive = false;
        }
    }

    public boolean isAlive() {
        return isAlive;
    }

    public int getType() {
        return type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getSatiety() {
        return satiety;
    }

    public int getAge() {
        return age;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
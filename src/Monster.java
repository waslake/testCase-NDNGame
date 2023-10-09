import java.util.Random;

public class Monster extends Creature {
    private final int[] gold;                 // Shows the upper and lower limit of gold that can be obtained from a monster
    private int experience;

    public Monster(int attack, int defense, int healthPoints, int[] damageRange, int speed, String name,
                    int[] gold, int experience) throws IllegalArgumentException {
        super(attack, defense, healthPoints, damageRange, speed, name);
        if (gold.length != 2 || gold[0] > gold[1]) {
            throw new IllegalArgumentException("Invalid gold argument value");
        }
        this.gold = gold;
        this.experience = experience;
    }

    // Copy constructor
    public Monster(Monster monster) {
        // If the object already exists, then all the data inside it is correct and does not need to be checked again
        super(monster.getAttack(), monster.getDefense(), monster.getMaxHealthPoints(), monster.getDamageRange(),
                monster.getSpeed(), monster.getName());
        this.gold = monster.getGold();
        this.experience = monster.getExperience();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getName()).append("[").append("атака: ").append(getAttack()).append(", защита: ").
                append(getDefense()).append(", здоровье: ").append(getCurrentHealthPoints()).append("/").
                append(getMaxHealthPoints()).append(", урон: ").append(getDamageRange()[0]).append("-").
                append(getDamageRange()[1]).append(", скорость: ").append(getSpeed()).append("]");
        return stringBuilder.toString();
    }

    // Returns the amount of gold the hero gets from the monster
    public int earnGold() {
        Random generator = new Random();
        return generator.nextInt(gold[0], gold[1] + 1);
    }

    public int[] getGold() {
        return gold;
    }

    public int getExperience() {
        return experience;
    }
}

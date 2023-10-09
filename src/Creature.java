import java.util.Arrays;
import java.util.Random;

public class Creature {
    private int attack;
    private int defense;
    private int currentHealthPoints;
    private int maxHealthPoints;
    private int[] damageRange;          // Shows min[0] and max[1] damage
    private int speed;                  // Speed must be in range from 1 to 30
    private String name;

    public Creature(int attack, int defense, int healthPoints, int[] damageRange, int speed, String name)
            throws IllegalArgumentException {
        if (attack < 1 || attack > 30 || defense <  1 || defense > 30 || healthPoints < 1
                || damageRange.length != 2 || damageRange[0] > damageRange[1] || speed < 1 || speed > 30) {
            throw new IllegalArgumentException("Illegal argument value");
        }
        this.attack = attack;
        this.defense = defense;
        this.currentHealthPoints = healthPoints;
        this.maxHealthPoints = healthPoints;
        this.damageRange = damageRange;
        this.speed = speed;
        this.name = name;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) throws IllegalArgumentException {
        if (attack < 1 || attack > 30) {
            throw new IllegalArgumentException("Invalid attack argument value. It should be in the range from 1 to 30");
        }
        this.attack = attack;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) throws IllegalArgumentException {
        if (defense < 1 || defense > 30) {
            throw new IllegalArgumentException("Invalid defense argument value. It should be in the range from 1 to 30");
        }
        this.defense = defense;
    }

    public int getCurrentHealthPoints() {
        return currentHealthPoints;
    }

    public void setCurrentHealthPoints(int healthPoints) throws IllegalArgumentException {
        if (currentHealthPoints > maxHealthPoints) {
            throw new IllegalArgumentException("The current HP value should not be greater than the maximum HP");
        }
        this.currentHealthPoints = healthPoints;
    }

    public int getMaxHealthPoints() {
        return maxHealthPoints;
    }

    public void setMaxHealthPoints(int maxHealthPoints) {
        this.maxHealthPoints = maxHealthPoints;
    }

    public void getDamage(int damage) throws IllegalArgumentException {
        if (damage < 0) {
            throw new IllegalArgumentException("Damage must be more then 0");
        }
        this.currentHealthPoints -= damage;
    }

    public int[] getDamageRange() {
        return damageRange;
    }

    public void setDamageRange(int[] damageRange) throws IllegalArgumentException{
        if (damageRange.length != 2 ||damageRange[0] >= damageRange[1]) {
            throw new IllegalArgumentException("Invalid damage argument value");
        }
        this.damageRange = damageRange;
    }

    // Heals the creature for a specified amount of healthPoints
    public void heal(int healthPoints) throws IllegalArgumentException {
        if (healthPoints < 0) {
            throw new IllegalArgumentException("Health point to be healed must be more then 0");
        }
        // If, after adding, the current health exceeds the maximum, then the difference is cut off
        if (currentHealthPoints + healthPoints > maxHealthPoints) {
            currentHealthPoints = maxHealthPoints;
        } else {
            currentHealthPoints += healthPoints;
        }
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        if (speed < 1 || speed > 30) {
            throw new IllegalArgumentException("Invalid speed argument value. It should be in the range from 1 to 30");
        }
        this.speed = speed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(name).append("[").append("атака: ").append(attack).append(", защита: ").append(defense).
                append(", урон: ").append(damageRange[0]).append("-").append(damageRange[1]).append(", здоровье: ").
                append(currentHealthPoints).append("/").append(maxHealthPoints).append("]");
        return stringBuilder.toString();
    }

    // Checks if the creature is dead (hp <= 0)
     public boolean isDead() {
        return currentHealthPoints <= 0;
     }

     // The method implements an attack by one creature on another, passed by reference to the method
     public void attack(Creature enemy) throws InterruptedException {
        int attacksCount = 1;                           // The number of attacks is always at least one
        if (getAttack() > enemy.getDefense()) {         // If the attacker's attack is greater, then the number of
                                                        // attacks is recalculated
            attacksCount = getAttack() - enemy.getDefense() + 1;
        }

        Random generator = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getName()).append(" атакует ").append(enemy.getName().toLowerCase()).append(" ").
                append(attacksCount).append(" раз!\n");
        NDNGame.printText(stringBuilder.toString());

         // The die is rolled attacksCount times or until 5/6 is rolled. If this happens, damage is dealt from the damageRange
        for (int i = 0; i < attacksCount; i++) {
            int randomValue = generator.nextInt(1, 7);
            NDNGame.printText("На кубиках выпало " + randomValue + "\n");
            if (randomValue >= 5) {
                int damage = generator.nextInt(damageRange[0], damageRange[1] + 1);
                stringBuilder.setLength(0);
                stringBuilder.append(getName()).append(" наносит ").append(damage).append(" урона!\n");
                NDNGame.printText(stringBuilder.toString());
                enemy.getDamage(damage);
                break;
            }
        }
     }
}

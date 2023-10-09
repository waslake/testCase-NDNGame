import java.util.ArrayList;
import java.util.Scanner;

public class Hero extends Creature {
    private int healPotionsCount = 4;
    private int experience = 0;
    private int experienceBound = 10;       // The threshold for moving to the next level doubles with each level
    private int level = 1;
    private int gold = 0;
    private ArrayList<String> inventory = new ArrayList<String>();      // Inventory with player items

    public Hero(int attack, int defense, int healthPoints, int[] damageRange, int speed, String name) throws IllegalArgumentException {
        super(attack, defense, healthPoints, damageRange, speed, name);

    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getName()).append("[").append("атака: ").append(getAttack()).append(", защита: ").
                append(getDefense()).append(", здоровье: ").append(getCurrentHealthPoints()).append("/").
                append(getMaxHealthPoints()).append(", урон: ").append(getDamageRange()[0]).append("-").
                append(getDamageRange()[1]).append(", скорость: ").append(getSpeed()).append(", уровень: ").
                append(level).append(", опыт: ").append(experience).append("/").append(experienceBound).append("]");
        return stringBuilder.toString();
    }

    public void useHealPotion() {
        if (healPotionsCount != 0) {
            healPotionsCount--;
            heal((int) (getMaxHealthPoints() * 0.3));
        }
    }

    public boolean isReadyToLevelUp() {
        return experience >= experienceBound;
    }

    // The method increases the hero's level and offers several upgrades to choose from. Moreover, if the attack or
    // defense has already reached 30, then they will not be offered for selection
    public void levelUp() throws InterruptedException {
        // If the experience is not enough to move to the next level, then the method does nothing
        if (experience < experienceBound) {
            return;
        }

        int itemsNumber = 4;        // Menu item counter
        int i = 1;                  // Menu item index

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getName()).append(" повысил свой уровень до ").append(level + 1).append("!").
                append(" Выберите одно из улучшений: \n");
        if (getAttack() >= 30) {        // If the attack is 30, then it cannot be increased and such an item is not offered
            itemsNumber--;              // The number of menu items is reduced by one
        } else {                        // Otherwise, such an item is added to the menu
            stringBuilder.append(i).append(". Увеличение атаки +1\n");
            i++;
        }

        if (getDefense() >= 30) {       // If the defense is 30, then it cannot be increased and such an item is not offered
            itemsNumber--;              // The number of menu items is reduced by one
        } else {                        // Otherwise, such an item is added to the menu
            stringBuilder.append(i).append(". Увеличение защиты +1\n");
            i++;
        }

        stringBuilder.append(i).append(". Увеличение урона +5\n");
        i++;
        stringBuilder.append(i).append(". Увеличение здоровья +5\n");
        NDNGame.printText(stringBuilder.toString());

        // menu for selecting characteristics for increasing
        Scanner scanner = new Scanner(System.in);
        int item;
        while (true) {
            NDNGame.printText("-> ");

            // Receiving input from the user, process incorrect input
            String value = scanner.nextLine();
            try {
                item = Integer.parseInt(value);
                if (item < 1 || item > itemsNumber) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException exception) {
                NDNGame.printText("Такого пункта меню нет, введите число еще раз!\n");
                continue;               // The user entered an incorrect value, one more iteration in a loop
            }

            break;                      // A valid menu item was received
        }

        // Increasing the selected characteristic.
        // Determine which characteristic the player chose by the number of items and what could have been selected
        switch (itemsNumber) {
            case 4:
                switch (item) {
                    case 1:
                        increaseAttack();
                        break;
                    case 2:
                        increaseDefense();
                        break;
                    case 3:
                        increaseDamage();
                        break;
                    case 4:
                        increaseHealthPoints();
                        break;
                }
            break;
            case 3:
                switch (item) {
                    case 1:                                 // The first item can only contain a proposal to
                                                            // increase attack or defense
                        if (getDefense() == 30) {
                            increaseAttack();
                        } else {
                            increaseDefense();
                        }
                        break;
                    case 2:
                        increaseDamage();
                        break;
                    case 3:
                        increaseHealthPoints();
                        break;
                }
            break;
            case 2:
                switch (item) {
                    case 1:
                        increaseDamage();
                        break;
                    case 2:
                        increaseHealthPoints();
                        break;
                }
            break;
        }
        // Level up by 1, threshold doubled, experience spent on leveling up is discarded
        level += 1;
        experience -= experienceBound;
        experienceBound *= 2;
    }

    public int getGold() {
        return gold;
    }

    public void addGold(int gold) {
        this.gold += gold;
    }

    public void addExperience(int experience) {
        this.experience += experience;
    }

    public int getHealPotionsCount() {
        return healPotionsCount;
    }

    public int getLevel() {
        return level;
    }

    // Checks whether the characteristic can be changed, changes it
    public void decreaseSpeed() {
        if (getSpeed() != 1) {
            setSpeed(getSpeed() - 1);
        }
    }

    // Checks whether the characteristic can be changed, changes it
    public void increaseAttack() {
        if (getAttack() != 30) {
            setAttack(getAttack() + 1);
        }
    }

    // Checks whether the characteristic can be changed, changes it
    public void increaseDefense() {
        if (getDefense() != 30) {
            setDefense(getDefense() + 1);
        }
    }

    // Increase damage
    public void increaseDamage() {
        setDamageRange( new int[] {getDamageRange()[0] + 5, getDamageRange()[1] + 5});
    }

    // Increase hp
    public void increaseHealthPoints() {
        setMaxHealthPoints(getMaxHealthPoints() + 5);
        setCurrentHealthPoints(getCurrentHealthPoints() + 5);
    }

    // Show if hero has a specific item in the inventory
    public boolean has(String item) {
        return inventory.contains(item);
    }

    // The function checks whether the item can be purchased and adds it to inventory if so
    public boolean buyItem(String name, int price) throws InterruptedException {
        if (inventory.contains(name)) {
            NDNGame.printText("У героя уже есть " + name + ". Вы не можете купить второй такой же!\n");
            return false;
        } else if (gold < price) {
            NDNGame.printText("У героя недостаточно золота для покупки " +  name + "!\n");
            return false;
        } else {
            inventory.add(name);
            gold -= price;
            return true;
        }

    }
}

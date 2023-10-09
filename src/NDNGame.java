import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;


public class NDNGame {
    public static void main(String[] args) throws InterruptedException {

        printStartText();

        Scanner scanner = new Scanner(System.in);

        // Creates a hero object that will then be controlled by the player
        Hero hero = new Hero(10, 10, 70, new int[] {2, 9}, 11, "");
        hero.setName(scanner.nextLine());

        // Game loop, the game ends either with the death of the player or upon reaching level 10
        // One game cycle consists of fighting a random monster and visiting a merchant's store (optional)
        while (!hero.isDead() && hero.getLevel() < 10) {
            Monster enemy = getRandomMonster(hero.getLevel());
            printText("\n" + hero.getName() + " встречает на своем пути " + enemy.getName() + "!\n");
            int item = 0;
            // Battle loop, player can fight the monster, heal without wasting a turn, or try to escape.
            // The battle ends with the death of one of the opponents,
            // or the escape of the player (in this case, the enemy variable becomes null)
            while (enemy != null && !hero.isDead() && !enemy.isDead()) {
                StringBuilder stringBuilder = new StringBuilder();
                // displaying information about opponents
                printText("\n" + hero.toString() +
                      "\n\t\t\t▀█░█▀ █▀▀ \n" +
                        "\t\t\t░█▄█░ ▀▀█ \n" +
                        "\t\t\t░░▀░░ ▀▀▀\n" +
                        enemy.toString());
                // The chance of escaping from a monster is (50 + (hero.getSpeed() - enemy.getSpeed()) * 1.5,
                // or 95 percent if the player has the boots artifact
                int chanceToEscape = (int) (50 + (hero.getSpeed() - enemy.getSpeed()) * 1.5);
                if (hero.has("сапоги-скороходы")) {
                    chanceToEscape = 95;
                }
                // Menu for selecting actions in battle
                stringBuilder.append("\nВыберите один из вариантов действий:\n1. Атаковать ").append(enemy.getName()).
                        append("\n2. Излечиться, выпив зелье [").append(hero.getHealPotionsCount()).append("/4]\n").
                        append("3. Попытаться сбежать. Вероятность: ").
                        append(chanceToEscape).append("%\nВыберите пункт:\n-> ");
                printText(stringBuilder.toString());

                // Receiving input from the user, process incorrect input
                String value = scanner.nextLine();
                try {
                    item = Integer.parseInt(value);
                } catch (NumberFormatException exception) {
                    printText("Введите номер одного из пунктов!\n");
                    continue;
                }

                // Perform an action based on the selected menu item
                switch (item) {
                    case 1:
                        // Player Attack of Monster
                        hero.attack(enemy);
                        break;
                    case 2:
                        // Using a healing potion
                        if (hero.getHealPotionsCount() > 0) {
                            hero.useHealPotion();
                        } else {
                            printText("Не осталось зелий лечения!\n");
                        }
                        continue;               // Using potion does not skip our turn, so we return to the
                                                // beginning of the battle loop
                    case 3:
                        // Escape attempt
                        Random generator = new Random();
                        int rolledValue = generator.nextInt(1, 100 + 1);
                        printText("Бросаем d100 кубик. Выпало " + rolledValue + "\n");
                        // The escape is considered successful if the dice show a value
                        // from 100 - chanceToEscape to 100
                        if (rolledValue > 100 - chanceToEscape) {
                            printText("Герой сбежал от " + enemy.getName() + "!\n");
                            enemy = null;
                        } else {
                            printText("Герою не удалось сбежать от " + enemy.getName() + "!\n");
                        }
                        break;
                    default:
                        printText("Такого пункта в меню нет, попробуйте еще раз.\n");
                        continue;
                }

                // The hero ran away from the monster and does not receive gold and experience
                if (enemy == null) {
                    break;
                } else if (enemy.isDead()) {        // The hero killed the monster
                    printText(hero.getName() + " одерживает славную победу над " + enemy.getName() + "!\n");
                    // Gaining experience and gold for killing a monster
                    hero.addGold(enemy.earnGold());
                    hero.addExperience(enemy.getExperience());

                    // The hero levels up as long as possible
                    while (hero.isReadyToLevelUp()) {
                        hero.levelUp();
                    }
                    // Checking if the player has won
                    if (hero.getLevel() >= 10) {
                        printVictoryText(hero.getName());
                    }
                    break;
                } else {
                    // The enemy is still alive and attacks back
                    enemy.attack(hero);
                    // Checking if the player is dead
                    if (hero.isDead()) {
                        printDeathText(hero.getName());
                        break;
                    }
                }
            }

            // After the battle the hero is asked if he wants to go to the store
            item = 0;
            while (!hero.isDead() && hero.getLevel() < 10 && item != 1 && item != 2) {
                StringBuilder stringBuilder = new StringBuilder();
                // Post-battle action selection menu
                stringBuilder.append("\nВыберите один из вариантов действий:\n1. Встретить следующего монстра\n").
                        append("2. Зайти в магазин\nВыберите пункт:\n-> ");
                printText(stringBuilder.toString());

                // Receiving input from the user, process incorrect input
                String value = scanner.nextLine();
                try {
                    item = Integer.parseInt(value);
                } catch (NumberFormatException exception) {
                    printText("Введите номер одного из пунктов!\n");
                    item = 0;
                }
                if (item != 1 && item != 2) {
                    printText("Пункта меню с таким номером нет. Попробуйте еще раз\n");
                }
            }

            // Displays a list of items available in the store
            if (item == 2) {
                printText("В магазине вас встречает добродушный торговец: \"Приветствую тебя путник, что " +
                        "ты желаешь приобрести?\"\nУ вас " + hero.getGold() + " золота.\n");
                while (item != 4) {     // Until the player decides to leave the store, he will be shown the available items
                    StringBuilder stringBuilder = new StringBuilder();

                    // Item selection menu in the store
                    stringBuilder.append("1. Клеймор (+1 к атаке, +5 к урону, -1 к скорости). Стоимость: 100 золота\n").
                            append("2. Стальной щит (+1 к атаке, +1 к защите). Стоимость: 130 золота\n").
                            append("3. Сапоги-скороходы (шанс сбежать от любого монстрар равен 95%). Стоимость: 200  золота\n").
                            append("4. Уйти из магазина\n->");
                    printText(stringBuilder.toString());

                    // Receiving input from the user, process incorrect input
                    String value = scanner.nextLine();
                    try {
                        item = Integer.parseInt(value);
                    } catch (NumberFormatException exception) {
                        printText("Введите номер одного из пунктов!\n");
                        item = 0;
                    }
                    if (item < 1 || item > 4) {
                        printText("Пункта меню с таким номером нет. Попробуйте еще раз\n");
                    }
                    switch (item) {
                        // Purchase of the selected item. It is added to the hero's inventory and increases his
                        // characteristics. Item cannot be re-purchased
                        // Exceeding Attack, Defense and Speed limits is not allowed
                        case 1:
                            if (hero.getAttack() == 30) {
                                printText("Герой достиг максимума атаки и не может вовысить ее больше." +
                                        "Купить предмет невозможно\n");
                            } else if (hero.getSpeed() == 1) {
                                printText("Скорость героя не может стать ниже 1. Купить предмет невозможно\n");
                            } else if (hero.buyItem("клеймор", 100)) {  // buyItem returns true if item can be bought
                                hero.increaseAttack();
                                hero.increaseDamage();
                                hero.decreaseSpeed();
                            }
                            break;
                        case 2:
                            if (hero.getAttack() == 30) {
                                printText("Герой достиг максимума атаки и не может вовысить ее больше." +
                                        "Купить предмет невозможно\n");
                            } else if (hero.getDefense()== 30) {
                                printText("Герой достиг максимума защиты и не может вовысить ее больше." +
                                        "Купить предмет невозможно\n");
                            } else if (hero.buyItem("стальной щит", 130)) {
                                hero.increaseAttack();
                                hero.increaseDefense();
                            }
                            break;
                        case 3:
                            hero.buyItem("сапоги-скороходы", 200);
                            break;
                    }
                }
            }

        }
    }

    // Create a static ArrayList with all the opponents available in the game. They will be chosen randomly
    // in the method getRandomMonster depending on the player's level (monsters are arranged in
    // order of increasing strength from the beginning of the array)
    static final ArrayList<Monster> monsters = new ArrayList<>(Arrays.asList(
            new Monster(1, 1, 5, new int[] {1, 3}, 18, "паук", new int[] {5, 10}, 5),
            new Monster(2, 2, 10, new int[] {2, 4}, 13, "кобольд", new int[] {10, 15}, 10),
            new Monster(5, 1, 15, new int[] {4, 5}, 17, "гоблин", new int[] {15, 18}, 20),
            new Monster(4, 6, 25, new int[] {4, 7}, 11, "орк", new int[] {20, 30}, 30),
            new Monster(6, 7, 40, new int[] {9, 11}, 9, "багбир", new int[] {40, 45}, 50),
            new Monster(10, 6, 60, new int[] {10, 14}, 6, "огр", new int[] {1, 100}, 100),
            new Monster(9, 9, 60, new int[] {14, 16}, 17, "мантикора", new int[] {50, 65}, 200),
            new Monster(9, 12, 80, new int[] {16, 16}, 25, "баньши", new int[] {70, 80}, 300),
            new Monster(10, 13, 90, new int[] {16, 18}, 27, "призрак", new int[] {85, 95}, 400),
            new Monster(15, 15, 130, new int[] {20, 25}, 13, "бехолдер", new int[] {100, 150}, 600),
            new Monster(20, 20, 200, new int[] {30, 40}, 20, "дракон", new int[] {200, 600}, 10000)
    ));

    // The method returns a random monster depending on the player's level: a random monster is returned with an
    // index in the array from level - 1 to (1 + the player's level).
    static Monster getRandomMonster(int level) throws IllegalArgumentException {
        if (1 + level > monsters.size() - 1) {
            throw new IllegalArgumentException("The player's level before the fight can be from 1 to 9");
        }
        Random generator = new Random();
        // New monster is a copy of th array's one
        return new Monster(monsters.get(generator.nextInt(level-1, 1 + level)));
    }

    // Prints text to the command line, making short delays between characters being output
    static void printText(String text) throws InterruptedException {
        for (int i = 0; i < text.length(); i++) {
            System.out.print(text.charAt(i));
            Thread.sleep(29);
        }
    }

    static void printStartText() throws InterruptedException {
        System.out.println("\n" +
                "           ███╗░░██╗██████╗░███╗░░██╗\n" +
                "           ████╗░██║██╔══██╗████╗░██║\n" +
                "           ██╔██╗██║██║░░██║██╔██╗██║\n" +
                "           ██║╚████║██║░░██║██║╚████║\n" +
                "           ██║░╚███║██████╔╝██║░╚███║\n" +
                "           ╚═╝░░╚══╝╚═════╝░╚═╝░░╚══╝");

        printText("Эта история начинается в таверне. Конечно, где же еще?\n" +
                "Но вы не чародей, который собирается отомстить королю соседнего государства за гибель своей семьи, " +
                "не благородный паладин, стремящийся принести добро и свет в сердца людей своим мечом, вы даже не " +
                "ловкий вор, скрывающийся от городской стражи. Вы самый обычный крестьянин, посевы которого засохли, а " +
                "последнее зерно забрали сборщики налогов. \n");
        printText("\"Нет, жить так больше нельзя\", - сказали вы себе и в этот прекрасный день взяли все свои " +
                "накопленные запасы, сделали себе деревянный меч и решили стать настоящим героем. Что ждет вас дальше? " +
                "Покажет будущее!\nКак же вас зовут?\n-> ");
    }

    static void printVictoryText(String name) throws InterruptedException {
        printText(name +  " проделал долгий путь и достиг совершенства, память о нем еще " +
                "надолго останется в сердцах людей, книгах и сказках. В один момент он просто исчез," +
                " возможно, он переместился в другое измерение, чтобы спасти его, или, может быть" +
                " просто заработал на спокойную старость? Это мы уже вряд ли когда-либо узнаем.");
        printText("\n" +
                "           ██╗░░░██╗██╗░█████╗░████████╗░█████╗░██████╗░██╗░░░██╗\n" +
                "           ██║░░░██║██║██╔══██╗╚══██╔══╝██╔══██╗██╔══██╗╚██╗░██╔╝\n" +
                "           ╚██╗░██╔╝██║██║░░╚═╝░░░██║░░░██║░░██║██████╔╝░╚████╔╝░\n" +
                "           ░╚████╔╝░██║██║░░██╗░░░██║░░░██║░░██║██╔══██╗░░╚██╔╝░░\n" +
                "           ░░╚██╔╝░░██║╚█████╔╝░░░██║░░░╚█████╔╝██║░░██║░░░██║░░░\n" +
                "           ░░░╚═╝░░░╚═╝░╚════╝░░░░╚═╝░░░░╚════╝░╚═╝░░╚═╝░░░╚═╝░░░");
    }

    static void printDeathText(String name) throws InterruptedException {
        printText("Не каждому суждено стать известным героем, но " + name + " старался, " +
                "возможно, в следующей жизни ему повезет больше?");
        printText("\n" +
                "          ██████╗░███████╗░█████╗░████████╗██╗░░██╗\n" +
                "          ██╔══██╗██╔════╝██╔══██╗╚══██╔══╝██║░░██║\n" +
                "          ██║░░██║█████╗░░███████║░░░██║░░░███████║\n" +
                "          ██║░░██║██╔══╝░░██╔══██║░░░██║░░░██╔══██║\n" +
                "          ██████╔╝███████╗██║░░██║░░░██║░░░██║░░██║\n" +
                "          ╚═════╝░╚══════╝╚═╝░░╚═╝░░░╚═╝░░░╚═╝░░╚═╝");
    }
}

import java.awt.*;

/**
 * The Town Class is where it all happens.
 * The Town is designed to manage all the things a Hunter can do in town.
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class Town {
    // instance variables
    private Hunter hunter;
    private Shop shop;
    private Terrain terrain;
    private String printMessage;
    private boolean toughTown;
    private boolean treasureFound;
    private String treasure;
    private double toughness;

    private OutputWindow window;



    /**
     * The Town Constructor takes in a shop and the surrounding terrain, but leaves the hunter as null until one arrives.
     *
     * @param shop The town's shoppe.
     * @param toughness The surrounding terrain.
     */
    public Town(Shop shop, double toughness, OutputWindow window) {
        this.shop = shop;
        this.terrain = getNewTerrain();
        this.window = window;

        // the hunter gets set using the hunterArrives method, which
        // gets called from a client class
        hunter = null;

        printMessage = "";

        // higher toughness = more likely to be a tough town
        toughTown = (Math.random() < toughness);
        assignTreasure();
        treasureFound = false;
        this.toughness = toughness;
    }

    public String getLatestNews() {
        return printMessage;
    }

    /**
     * Assigns an object to the Hunter in town.
     *
     * @param hunter The arriving Hunter.
     */
    public void hunterArrives(Hunter hunter) {
        this.hunter = hunter;
        window.addTextToWindow("Welcome to town, ", Color.BLACK);
        window.addTextToWindow(hunter.getHunterName() + ".", Color.PINK);

        if (toughTown) {
            window.addTextToWindow("\nIt's pretty rough around here, so watch yourself.", Color.BLACK);
        } else {
            window.addTextToWindow("\nWe're just a sleepy little town with mild mannered folk.", Color.BLACK);
        }
    }

    /**
     * Handles the action of the Hunter leaving the town.
     *
     * @return true if the Hunter was able to leave town.
     */
    public boolean leaveTown() {
        boolean canLeaveTown = terrain.canCrossTerrain(hunter);
        if (canLeaveTown) {
            String item = terrain.getNeededItem();
            window.addTextToWindow("You used your ", Color.BLACK);
            window.addTextToWindow(item, Color.PINK);
            window.addTextToWindow(" to cross the ", Color.BLACK);
            window.addTextToWindow(terrain.getTerrainName() + ".", Color.CYAN);
            if (checkItemBreak() && !(toughness == .25)) {
                hunter.removeItemFromKit(item);
                window.addTextToWindow("\nUnfortunately, you lost your ", Color.BLACK);
                window.addTextToWindow(item, Color.PINK);
            }

            return true;
        }

        window.addTextToWindow("You can't leave town, ", Color.BLACK);
        window.addTextToWindow(hunter.getHunterName(), Color.PINK);
        window.addTextToWindow(". You don't have a ", Color.BLACK);
        window.addTextToWindow(terrain.getNeededItem() + ".", Color.PINK);
        return false;
    }

    /**
     * Handles calling the enter method on shop whenever the user wants to access the shop.
     *
     * @param choice If the user wants to buy or sell items at the shop.
     */
    public void enterShop(String choice) {
        shop.enter(hunter, choice);
        window.addTextToWindow("\nYou left the shop", Color.BLACK);
    }

    /**
     * Gives the hunter a chance to fight for some gold.<p>
     * The chances of finding a fight and winning the gold are based on the toughness of the town.<p>
     * The tougher the town, the easier it is to find a fight, and the harder it is to win one.
     */
    public void lookForTrouble() {
        double noTroubleChance;
        if (toughTown) {
            noTroubleChance = 0.66;
        } else {
            noTroubleChance = 0.33;
        }

        if (Math.random() > noTroubleChance) {
            window.addTextToWindow("You couldn't find any trouble", Color.BLACK);
        } else {
            if(hunter.hasItemInKit("sword")) {
                int goldDiff = (int) (Math.random() * 10) + 1;
                window.addTextToWindow("You want trouble, stranger!  You got it!\n", Color.RED);
                window.addTextToWindow("\nthe brawler, seeing your sword, realizes he picked a losing fight and gives you his gold", Color.BLACK);
                window.addTextToWindow("\nYou won the brawl and receive ", Color.BLACK);
                window.addTextToWindow(""+goldDiff, Color.YELLOW);
                window.addTextToWindow("gold.", Color.BLACK);

                hunter.changeGold(goldDiff);
            } else {
                window.addTextToWindow("You want trouble, stranger!  You got it!\nOof! Umph! Ow!\n", Color.RED);
                int goldDiff = (int) (Math.random() * 10) + 1;
                if (Math.random() > noTroubleChance && !hunter.hasItemInKit("sword")) {
                    window.addTextToWindow("Okay, stranger! You proved yer mettle. Here, take my gold.", Color.BLACK);
                    window.addTextToWindow("\nYou won the brawl and receive ", Color.BLACK);
                    window.addTextToWindow(""+goldDiff, Color.YELLOW);
                    window.addTextToWindow("gold.", Color.BLACK);
                    hunter.changeGold(goldDiff);
                } else {
                    window.addTextToWindow("That'll teach you to go lookin' fer trouble in MY town! Now pay up!", Color.RED);
                    window.addTextToWindow("\nYou lost the brawl and pay", Color.BLACK);
                    window.addTextToWindow(""+goldDiff, Color.YELLOW);
                    window.addTextToWindow("gold.", Color.BLACK);
                    hunter.changeGold(-goldDiff);
                    if(hunter.getGold() < 0) {
                        window.addTextToWindow("\nGAME OVER", Color.RED);
                    }
                }
            }
        }
    }

    public void huntForTreasure() {
        if(!treasureFound) {
            if(!hunter.hasTreasure(treasure)) {
                window.addTextToWindow("\nYou found a ", Color.BLACK);
                window.addTextToWindow(treasure + "!", Color.PINK);
                if(!treasure.equals("dust")) {
                    hunter.addTreasure(treasure);
                    if(hunter.treasureIsFull()) {
                        window.addTextToWindow("\nYou found the last of the 3 ", Color.BLACK);
                        window.addTextToWindow("treasures, ", Color.PINK);
                        window.addTextToWindow("you win!", Color.BLACK);
                    }
                }
            } else {
                window.addTextToWindow("\nYou have already found a ", Color.BLACK);
                window.addTextToWindow(treasure + ", ", Color.PINK);
                window.addTextToWindow("you don't need another one", Color.BLACK);
            }
            treasureFound = true;
        } else {
            window.addTextToWindow("\nYou have already searched this town", Color.BLACK);
        }
    }

    public void digForGold(){
        if (hunter.hasItemInKit("shovel")) {
            int goldFound = 0;
            window.addTextToWindow("You enter the caves in search of", Color.BLACK);
            window.addTextToWindow("gold.", Color.YELLOW);
            if (Math.random() < 0.25) {
                goldFound = (int) (4 * (Math.random()));
                if (goldFound != 0) {
                    window.addTextToWindow("You struck ", Color.BLACK);
                    window.addTextToWindow("gold!", Color.YELLOW);
                    hunter.changeGold(goldFound);
                } else {
                    window.addTextToWindow("You didn't find any ", Color.BLACK);
                    window.addTextToWindow("gold.", Color.YELLOW);
                }
            } else {
                window.addTextToWindow("You decided you were too tired to dig", Color.BLACK);
            }
            window.addTextToWindow("You leave the cave with ", Color.BLACK);
            window.addTextToWindow(goldFound + " gold", Color.YELLOW);
        }
        else{
            window.addTextToWindow("You do not have a shovel, you need one to dig for", Color.BLACK);
            window.addTextToWindow("gold.", Color.YELLOW);
        }
    }

    public String toString() {
        return "This nice little town is surrounded by " + terrain.getTerrainName() + ".";
    }

    /**
     * Determines the surrounding terrain for a town, and the item needed in order to cross that terrain.
     *
     * @return A Terrain object.
     */
    private Terrain getNewTerrain() {
        double rnd = Math.random();
        if (rnd < .16) {
            return new Terrain("Mountains", "Rope");
        } else if (rnd < .33) {
            return new Terrain("Ocean", "Boat");
        } else if (rnd < .5) {
            return new Terrain("Plains", "Horse");
        } else if (rnd < .66) {
            return new Terrain("Desert", "Water");
        } else if (rnd < .833){
            return new Terrain("Jungle", "Machete");
        }
        else{
            return new Terrain("Marsh", "Boots");
        }
    }

    /**
     * Determines whether a used item has broken.
     *
     * @return true if the item broke.
     */
    private boolean checkItemBreak() {
        double rand = Math.random();
        return (rand < 0.5);
    }

    private void assignTreasure() {
        String[] treasureArray = {"crown", "trophy", "gem", "dust"};
        int treasureSelection = (int) (Math.random() * 3);
        treasure = treasureArray[treasureSelection];
    }
}
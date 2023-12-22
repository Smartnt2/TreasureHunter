import java.awt.*;
import java.util.Scanner;

/**
 * This class is responsible for controlling the Treasure Hunter game.<p>
 * It handles the display of the menu and the processing of the player's choices.<p>
 * It handles all the display based on the messages it receives from the Town object. <p>
 *
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class TreasureHunter {
    // static variables
    private static final Scanner SCANNER = new Scanner(System.in);

    // instance variables
    private Town currentTown;
    private Hunter hunter;
    private boolean hardMode;
    private boolean easyMode;
    private boolean samuraiMode;

    /**
     * Constructs the Treasure Hunter game.
     */
    OutputWindow window = new OutputWindow();
    public TreasureHunter() {
        // these will be initialized in the play method
        currentTown = null;
        hunter = null;
        hardMode = false;
        easyMode = false;
        samuraiMode = false;
    }

    /**
     * Starts the game; this is the only public method
     */
    public void play() {
        welcomePlayer();
        enterTown();
        showMenu();
    }

    /**
     * Creates a hunter object at the beginning of the game and populates the class member variable with it.
     */
    private void welcomePlayer() {
        window.addTextToWindow("Welcome to TREASURE HUNTER!\n", Color.BLACK);
        window.addTextToWindow("Going hunting for the big treasure, eh?\n", Color.BLACK);
        window.addTextToWindow("What's your name, Hunter? \n", Color.BLACK);
        String name = SCANNER.nextLine().toLowerCase();
        window.clear();



        window.addTextToWindow("Easy, normal, or hard mode? (e/n/h): ", Color.BLACK);
        String difficulty = SCANNER.nextLine().toLowerCase();
        if (difficulty.equals("test")) {
            hunter = new Hunter(name, 100, false);
        }
        else if(difficulty.equals("e")) {
            easyMode = true;
            hunter = new Hunter(name, 20, false);
        }
        else if (difficulty.equals("h")) {
            hardMode = true;
            hunter = new Hunter(name, 10, false);
        }
        else if (difficulty.equals("s")) {
            samuraiMode = true;
            hunter = new Hunter(name, 10, true);
        }
        else {
            hunter = new Hunter(name, 10, false);
        }
        window.clear();
    }

    /**
     * Creates a new town and adds the Hunter to it.
     */
    private void enterTown() {
        double markdown = 0.5;
        double toughness = 0.4;
        if (hardMode) {
            // in hard mode, you get less money back when you sell items
            markdown = 0.25;

            // and the town is "tougher"
            toughness = 0.75;
        }
        if(easyMode) {
            markdown = 1;
            toughness = .25;
        }
        if(samuraiMode) {
            toughness = .8;
        }

        // note that we don't need to access the Shop object
        // outside of this method, so it isn't necessary to store it as an instance
        // variable; we can leave it as a local variable
        Shop shop = new Shop(markdown, window);

        // creating the new Town -- which we need to store as an instance
        // variable in this class, since we need to access the Town
        // object in other methods of this class
        currentTown = new Town(shop, toughness, window);

        // calling the hunterArrives method, which takes the Hunter
        // as a parameter; note this also could have been done in the
        // constructor for Town, but this illustrates another way to associate
        // an object with an object of a different class
        currentTown.hunterArrives(hunter);
    }

    /**
     * Displays the menu and receives the choice from the user.<p>
     * The choice is sent to the processChoice() method for parsing.<p>
     * This method will loop until the user chooses to exit.
     */
    private void showMenu() {
        String choice = "";

        while (!choice.equals("x")) {
            window.clear();
            window.addTextToWindow(currentTown.getLatestNews(), Color.BLACK);
            if(hunter.getGold() < 0) {
                break;
            }
            if(hunter.treasureIsFull()) {
                break;
            }
            window.addTextToWindow("***\n", Color.BLACK);
            window.addTextToWindow(hunter.toString() + "\n", Color.BLACK);
            window.addTextToWindow(currentTown.toString() + "\n", Color.BLACK);
            window.addTextToWindow("(B)uy something at the shop.\n", Color.BLACK);
            window.addTextToWindow("(S)ell something at the shop.\n", Color.BLACK);
            window.addTextToWindow("(M)ove on to a different town.\n", Color.BLACK);
            window.addTextToWindow("(L)ook for trouble!\n", Color.BLACK);
            window.addTextToWindow("(H)unt for treasure!\n", Color.BLACK);
            window.addTextToWindow("(D)ig for gold!\n", Color.BLACK);
            window.addTextToWindow("Give up the hunt and e(X)it.\n", Color.BLACK);
            window.addTextToWindow("\n", Color.BLACK);
            window.addTextToWindow("What's your next move?", Color.BLACK);
            choice = SCANNER.nextLine().toLowerCase();
            window.clear();
            processChoice(choice);
        }
    }

    /**
     * Takes the choice received from the menu and calls the appropriate method to carry out the instructions.
     * @param choice The action to process.
     */
    private void processChoice(String choice) {
        if (choice.equals("b") || choice.equals("s")) {
            currentTown.enterShop(choice);
        } else if (choice.equals("m")) {
            if (currentTown.leaveTown()) {
                // This town is going away so print its news ahead of time.
                window.addTextToWindow(currentTown.getLatestNews(), Color.BLACK);
                enterTown();
            }
        } else if (choice.equals("l")) {
            currentTown.lookForTrouble();
        } else if (choice.equals("h")) {
            currentTown.huntForTreasure();
        } else if (choice.equals("x")) {
            window.addTextToWindow("Fare thee well, " + hunter.getHunterName() + "!", Color.BLACK);
        } else if (choice.equals("d")){
            currentTown.digForGold();
        } else {
            window.addTextToWindow("Yikes! That's an invalid option! Try again.", Color.BLACK);
        }

    }
}
//Final Project
//Title: Wonderland Adventure 🔑🌸
//Name: Vindhya Apoorva Vodamodula
//Student ID: 3728392
//Course: COMP 268 - Java Programming
//Date: October 2025
//Athabasca University
//Description:
// * This is a text-based adventure game inspired by Alice in Wonderland.
// * The player explores various rooms, solves simple puzzles, and collects curios
// * to reach the Queen's Garden and win the game.
//Features:
// * Personalized player name
// * Case-insensitive commands
// * Room-based storytelling with emojis and hints
// * Inventory system with 3 curios and 1 key item
// * Puzzles with 2-attempt logic and catch-up mechanism
//This project was created as part of COMP 268 (Intro to Computer Programming – Java) at Athabasca University

package finalproject;

import java.util.HashSet;
import java.util.Set;

public class Game {

    // Story states
    private enum State {GARDEN, RABBIT_HOLE, MYSTERY_ROOM, TEA_PARTY, ROYAL_COURT, FOREST_GARDEN, QUEENS_GARDEN}

    private enum SizeState {NORMAL, SHRUNK, GROWN}

    private final Control control = new Control();

    private State state = State.GARDEN;       // start in Garden
    private String playerName = "friend";     // default name

    // Minimal inventory
    private final Set<String> bag = new HashSet<>(); // stores item ids UPPERCASE
    private SizeState size = SizeState.NORMAL;

    // Item IDs
    private static final String BOTTLE = "BOTTLE";
    private static final String CAKE = "CAKE";
    private static final String MUSHROOM = "MUSHROOM";
    private static final String MAP_FRAGMENT = "MAP_FRAGMENT";
    private static final String TART = "TART";
    private static final String BRASS_KEY = "BRASS_KEY";
    private static final String CROWN = "CROWN";
    private static final String ROSE = "ROSE";
    private static final String POCKET_WATCH = "POCKET_WATCH";
    // Curio retry locks: prevents repeat attempts once failed
    private boolean tartLocked  = false;
    private boolean crownLocked = false;
    private boolean roseLocked  = false;

    public static void main(String[] args) {
        new Game().run();
    }

    private void run() {
        // Welcome
        println("\nWelcome to the Wonderland Adventure!");

        println("Type 'help' if your stuck in the adventure, or 'exit' to leave the wonderland :(");
        println("To see what's inside you’re bag, type in 'i' or 'bag' or 'inventory' any time in the middle of your wonderland adventure:)");

        // Ask name
        String name = control.read("\nType in your name before we start our adventure journey!  ");
        if (!name.isBlank()) {
            playerName = name.substring(0,1).toUpperCase() + name.substring(1);
        }
        println("Hello " + playerName +  "Let's explore our wonderland🏞️!");

        // Show first room
        showGarden();

        // Main loop
        while (true) {
            switch (state) {
                case GARDEN -> handleGarden();
                case RABBIT_HOLE -> handleRabbitHole();
                case MYSTERY_ROOM -> handleMysteryRoom();
                case TEA_PARTY -> handleTeaParty();
                case ROYAL_COURT -> handleRoyalCourt();
                case FOREST_GARDEN -> handleForestGarden();
                case QUEENS_GARDEN -> handleQueensGarden();
            }
        }
    }

    //Tea Party riddle for the Tart — 2 tries
    private boolean tartRiddlePuzzle() {
        println("The Hatter taps his teacup🍵: \"Solve my riddle for the tart!\"");
        println("Riddle: I have two hands🙌 but can’t clap. What am I?");
        int tries = 0;
        while (tries < 2) {
            String a = preprocess(control.read("> "));
            if (Control.isExitWord(a)) {
                if (confirmExit()) goodbye();
                println("Let’s keep puzzling!");
                continue;
            }
            if (a.equals("help")) {
                println("Hint: Think of time.");
                continue;
            }
            if (a.contains("clock")) return true;  // accept “clock” / “a clock”
            tries++;
            if (tries < 2) println("Not quite🫤. Hint: You look at it to know the hour.");
        }
        println("Oh no—riddle failed🙃 this time. You can explore more and try again later.");
        return false;
    }


   //Royal Court manners puzzle for the Crown. 2 tries max.
    private boolean crownMannersPuzzle() {
        println("The Queen smiles: \"A crown for good manners! Type bow or curtsy.\"");
        int tries = 0;
        while (tries < 2) {
            String a = preprocess(control.read("> "));
            if (Control.isExitWord(a)) {
                if (confirmExit()) goodbye();
                println("No worries—try again!");
                continue;
            }
            if (a.equals("help")) {
                println("Tip: type bow or curtsy.");
                continue;
            }
            if (a.equals("bow") || a.equals("curtsy")) return true;
            tries++;
            if (tries < 2) println("Hmm… try a polite bow or curtsy!");
        }
        println("Challenge failed this round🙂‍↕️. You can come back and try again.");
        return false;
    }


   //Royal Court color puzzle for the Rose. 2 tries max.
    private boolean roseColorPuzzle() {
        println("The Queen asks: \"The roses🌹 I like best are ____.\"");
        println("Hint: It's a color hidden in the question itself😉");
        int tries = 0;
        while (tries < 2) {
            String a = preprocess(control.read("> "));
            if (Control.isExitWord(a)) {
                if (confirmExit()) goodbye();
                println("Let’s try again! ");
                continue;
            }
            if (a.equals("help")) {
                println("Hint: In some stories, we paint the roses🌹…?");
                continue;
            }
            if (a.equals("red")) return true;   // accept only “red”
            tries++;
            if (tries < 2) println("Not that color. Try again!");
        }
        println("Color quiz missed this time. You can explore and try later.");
        return false;
    }

   //Expand common glued inputs and normalize spacing/case.
    private String preprocess(String raw) {
        if (raw == null) return "";
        String s = raw.trim().toLowerCase();

        // normalize multiple spaces
        s = s.replaceAll("\\s+", " ").trim();

        // expand glued 'take...' and 'use...' for known nouns
        String[] nouns = {"bottle","cake","mushroom","crown","rose","tart"};
        for (String n : nouns) {
            if (s.equals("take" + n)) s = "take " + n;
            if (s.equals("use" + n))  s = "use " + n;
        }
        return s;
    }

    //Is this an inventory request?
    private boolean isInventoryCmd(String s) {
        if (s == null) return false;
        s = s.trim().toLowerCase();
        return s.equals("i") || s.equals("bag") || s.equals("backpack") ||
                s.equals("inventory") || s.equals("check my bag") || s.equals("check bag") ||
                s.equals("my stuff") || s.equals("what do i have");
    }

   //Show friendly inventory + size.
    private void showInventory() {
        String sizeStr = size.toString();
        println("You’re " + sizeStr + " size.");
        if (bag.isEmpty()) {
            println("Your bag🎒 is empty.");
            println("Let's continue our adventure⛰️🏞️, Read👆👆 where your at and choose to explore!");
            return;
        }
        // Pretty-print item names
        StringBuilder sb = new StringBuilder("In your bag🎒: ");
        boolean first = true;
        for (String item : bag) {
            String nice = item.replace('_',' ').toLowerCase();
            nice = nice.substring(0,1).toUpperCase() + nice.substring(1);
            if (!first) sb.append(", ");
            sb.append(nice);
            first = false;
        }
        println(sb.toString() + ".");
        println("Let's continue our adventure⛰️🏞️, Read👆👆 where your at and choose to explore!");
    }


    private boolean isLookCmd(String s) {
        if (s == null) return false;
        String x = s.trim().toLowerCase();
        return x.equals("look") || x.equals("l") ||
                x.equals("where") || x.equals("where am i") || x.equals("look around");
    }



    // GARDEN

    private void showGarden() {
        println("\n Right now you are in the Garden⛲️🪴!");
        println("Let's look around see what's hidden inside. ");
        println("Whola!...Look..A white rabbit🐇 is running past you, calling "+ playerName +" "+  playerName +"!......");
        println("It's going south towards a cozy rabbit🐰 hole.");
        println("Let's run along and find out why its calling you?");
        println("Try typing 'south' to follow the rabbit or 'exit' to leave the wonderland ☹️😞");
        println("Now go ahead and type what you wanna do??😉");

    }



    private void handleGarden() {
        String input = preprocess(control.read("\n> "));
        if (isInventoryCmd(input)) { showInventory(); return; }
        if (isLookCmd(input)) { showGarden(); return; }




        if (Control.isExitWord(input)) {
            if (confirmExit()) goodbye();
            println("Awesome😃, let's go inside the rabbit's hole! " + playerName + "!");
            println("Try typing 'south'");
            hint("south  • or may be ask for 'help' if your lost!");
            return;
        }

        if (input.equals("help")) {
            showHelp();
            hint("south  • end");
            return;
        }

        if (input.equals("south") || input.equals("s") || input.equals("go south")) {
            state = State.RABBIT_HOLE;
            showRabbitHole();
            return;
        }

        println("From the Garden, the path only goes south.");
        hint("type south  • or end");
    }


    // RABBIT HOLE
    private void showRabbitHole() {
        println("\n Rabbit Hole ");
        println("You peer into the rabbit hole and tumble gently downward...");
        println("Inside you see a tunnel which leads down, deeper into the adventure.");
        println("Should we go down and checkout the adventure?");
        println("Type in 'yes' to jump right in! OR say 'no' to exit the wonderland tour 😫");
        println("You can always type 'help' if you feel lost!");
        println("Now go a head and type what you wanna do?☺️☺️");
    }


    private void handleRabbitHole() {
        String input = preprocess(control.read("\n> "));
        if (isInventoryCmd(input)) { showInventory(); return; }
        if (isLookCmd(input)) { showRabbitHole(); return; }


        if (Control.isExitWord(input)) {
            if (confirmExit()) goodbye();
            println("Awesome, let's continue, " + playerName + "!");
            println("Do you want to go down and explore😀? (yes/no)");
            return;
        }

        if (input.equals("help")) {
            showHelp();
            println("Do you want to go down and explore😀? (yes/no)");
            return;
        }

        if (input.equals("yes") || input.equals("y")) {
            state = State.MYSTERY_ROOM;
            showMysteryRoom();
            return;
        }

        if (input.equals("no") || input.equals("n")) {
            String again = control.read("Would you like to end the game😣? (yes/no) ").toLowerCase();
            if (again.startsWith("y")) goodbye();
            println("Awesome, let's continue, " + playerName + "!");
            println("Do you want to go down and explore? (yes/no)");
            return;
        }

        println("Please answer 'yes' or 'no'.");
        hint("yes  • no  • end");
    }

    // ─────────────────────────────────────────────────────────
    // MYSTERY ROOM (Bottle → Tea Party, Cake → Royal Court, W → Forest, N → Queen’s (needs key))
    // ─────────────────────────────────────────────────────────
    private void showMysteryRoom() {
        println("\n You have entered a Mystery Room 😳");
        println("This room has 4 doors🚪 on each side, and a little table with three odd treats🤔!");
        println("The treats are as follows👇:");
        println(" • a small 'Drink Me' bottle🍶 with a note📝 that reads ➺ ' SHRINK and fit the tiny EAST door'.");
        println(" • a big 'Eat Me' cake🎂 with a note📝 that reads ➺ 'be strong for the heavy SOUTH door'.");
        println(" • a bouncy mushroom🍄 that reads📝 ➺ 'switch size later'.");
        println("The WEST path opens to a forest🌳🦌. And the NORTH door has a fancy keyhole🔐.");
        println("Lets pick something and try to enter into a door😃? Try typing in (bottle/cake/mushroom)");
        println("wanna try and use what you picked? Try it with ➺ use bottle/use cake/use mushroom ");
        println("Now go a head and type what you wanna do?☺️☺️");

    }

    private void handleMysteryRoom() {
        String input = preprocess(control.read("\n> "));
        if (isInventoryCmd(input)) { showInventory(); return; }
        if (isLookCmd(input)) { showMysteryRoom(); return; }


        if (Control.isExitWord(input)) {
            if (confirmExit()) goodbye();
            println("Awesome, let's continue, " + playerName + "!");
            hint("take bottle  • use bottle  • 'n'(to go back)  • w  • help");
            return;
        }

        if (input.equals("help")) {
            showHelp();
            hint("take bottle  • use bottle  • 'n'(to go back)  • w  • look  • end");
            return;
        }

        if (input.equals("look") || input.equals("l")) {
            showMysteryRoom();
            return;
        }

        // move west to Forest Garden
        if (input.equals("w") || input.equals("west") || input.equals("go west")) {
            state = State.FOREST_GARDEN;
            showForestGarden();
            return;
        }

        // move north to Queen's Garden
        if (input.equals("n") || input.equals("north") || input.equals("go north")) {
            if (bag.contains(BRASS_KEY)) {
                state = State.QUEENS_GARDEN;
                showQueensGarden();
            } else {
                println("The tall door is locked. It needs a special key—maybe the Queen could help.");
                hint("try: use cake → yes → Royal Court  • or explore west/east");
            }
            return;
        }

        // single-word shortcuts: bottle/cake/mushroom
        if (input.equals("bottle") || input.equals("cake") || input.equals("mushroom")) {
            takeItem(input);
            return;
        }

        // take <item>
        if (input.startsWith("take ")) {
            String what = input.substring(5).trim();
            takeItem(what);
            return;
        }

        // use / use <item>
        if (input.equals("use")) {
            String what = control.read("Sure! conform what do you wanna use? (bottle/cake/mushroom) ").toLowerCase();
            useItem(what);
            return;
        }
        if (input.startsWith("use ")) {
            String what = input.substring(4).trim();
            useItem(what);
            return;
        }

        println("Opps,🤔 I’m not sure I understood!");
        hint("Try typing: use bottle,  help or end");
    }

    private void takeItem(String raw) {
        String id = norm(raw);
        if (!id.equals(BOTTLE) && !id.equals(CAKE) && !id.equals(MUSHROOM)) {
            println("You don’t see that here. Try: bottle, cake, or mushroom.");
            hint("take bottle  • use bottle  • help");
            return;
        }
        if (bag.add(id)) println("Awesome! you picked up the " + raw + ".Now lest's try and use it");
        else println("You already have the " + raw + ".");
        hint("use " + raw + "  • look  • end");
    }

    private void useItem(String raw) {
        String id = norm(raw);
        if (!bag.contains(id)) {
            println("You don’t seem to have that.");
            hint("take " + raw + "  • help");
            return;
        }

        switch (id) {
            case BOTTLE -> {
                size = SizeState.SHRUNK;
                println(playerName+"! now take a brave sip. Notice shrinking😅? ");
                askGoEastNow();
            }
            case CAKE -> {
                size = SizeState.GROWN;
                println("You nibble the cake🎂 and feel strong enough to push heavy doors.");
                askGoSouthNow();
            }
            case MUSHROOM -> {
                size = (size == SizeState.NORMAL) ? SizeState.SHRUNK : SizeState.NORMAL;
                println("A curious munch! Your size shifts to: " + size);
                hint("look  • help  • end");
            }
            default -> {
                println("You try using the " + raw + ", but nothing special happens now.");
                hint("look  • help  • end");
            }
        }
    }

    // After using the bottle → ask to go EAST
    private void askGoEastNow() {
        while (true) {
            println("Wait! Did you listen to the creeking sound?");
            println("Yay! The tiny door to the east is now unlocked 😃");
            String a = control.read(" now you perfectly fit 😉,Let's go? Type: (yes/no) ").toLowerCase();
            if (a.equals("yes") || a.equals("y")) {
                state = State.TEA_PARTY;
                showTeaParty();
                return;
            }
            if (a.equals("no") || a.equals("n")) {
                String end = control.read("Would you like to end the game? (yes/no) ").toLowerCase();
                if (end.equals("yes") || end.equals("y")) goodbye();
                println("Awesome, let's continue, " + playerName + "!");
                hint("look  • help  • end");
                return;
            }
            if (Control.isExitWord(a)) {
                if (confirmExit()) goodbye();
                println("Awesome, let's continue, " + playerName + "!");
                hint("look  • help  • end");
                return;
            }
            println("Please answer 'yes' or 'no'.");
        }
    }

    // After using the cake → ask to go SOUTH
    private void askGoSouthNow() {
        while (true) {
            String a = control.read("The heavy door to the south looks possible now. Go through it? (yes/no) ").toLowerCase();
            if (a.equals("yes") || a.equals("y")) {
                state = State.ROYAL_COURT;
                showRoyalCourt();
                return;
            }
            if (a.equals("no") || a.equals("n")) {
                String end = control.read("Would you like to end the game? (yes/no) ").toLowerCase();
                if (end.equals("yes") || end.equals("y")) goodbye();
                println("Awesome, let's continue, " + playerName + "!");
                hint("look  • help  • end");
                return;
            }
            if (Control.isExitWord(a)) {
                if (confirmExit()) goodbye();
                println("Awesome, let's continue, " + playerName + "!");
                hint("look  • help  • end");
                return;
            }
            println("Please answer 'yes' or 'no'.");
        }
    }


    // TEA PARTY

    private void showTeaParty() {
        println("\nThe Tea Party 🎊☕️🎉");
        println("Looks like you have entered the Hatters Tea Party!");
        println("A long, wiggly table stretches with teacups and giggles. With a sweet tart🍮 sitting on a plate.");
        println("Do you want chat with the Hatter? Try typing:'talk'.");
        println("Or you can get a treat from him too.Try typing:'take tart'.");
        println("Do you feel the party boring🥱?Try typing: 'west' to go back to mystery room");
        println("Don't forget to always check whats in your bag🎒, look👀 around or ask for help if needed.");
        println("Now go ahead and type what you wanna do?");
    }

    private void handleTeaParty() {
        String input = preprocess(control.read("\n> "));
        if (isInventoryCmd(input)) { showInventory(); return; }
        if (isLookCmd(input)) { showTeaParty(); return; }


        if (Control.isExitWord(input)) {
            if (confirmExit()) goodbye();
            println("Awesome, let's continue, " + playerName + "!");
            hint("talk  • take tart  • w");
            return;
        }

        if (input.equals("help")) {
            showHelp();
            hint("talk  • take tart  • w  • end");
            return;
        }

        if (input.equals("look") || input.equals("l")) {
            showTeaParty();
            return;
        }

        // TALK → MAP_FRAGMENT
        if (input.equals("talk")) {
            if (!bag.contains(MAP_FRAGMENT)) {
                bag.add(MAP_FRAGMENT);
                println("The Mad Hatter grins, 'Here you go! A map piece for a brave traveler!'");
                println(" You receive a MAP FRAGMENT 🗺️.");
                println("You will need this later in the forest.");
                println("Wanna get the tart😋??");
            } else {
                println("The Hatter winks, \"You already have the map piece! Maybe try the tart, or explore more.\"");
                println("Don't forget to always check whats in your bag🎒, look👀 around or ask for help if needed.");
            }
            hint("take tart  • 'w' (to go back to the mystery room)  • help");
            println("Now go ahead and type what you wanna do😉😉?");
            return;
        }

        // Single-word : shortcut
        if (input.equals("tart")) {
            takeTart();
            return;
        }

        // TAKE TART
        if (input.startsWith("take ")) {
            String what = input.substring(5).trim().toLowerCase();
            if (what.equals("tart")) {
                takeTart();
            } else {
                println("You don’t see that here. Try: take tart.");
                hint("talk  • take tart  • w");
            }
            return;
        }

        // Go back west
        if (input.equals("w") || input.equals("west") || input.equals("go west")) {
            state = State.MYSTERY_ROOM;
            showMysteryRoom();
            return;
        }

        println("You can 'talk' to the Hatter, 'take tart', or go 'w' back to the Mystery Room.");
        hint("talk  • take tart  • w  • help  • end");
    }

    private void takeTart() {
        if (bag.contains(TART)) {
            println("You already took the tart.");
            println("Don't forget to always check whats in your bag🎒, look👀 around or ask for help if needed.");
            hint("to get the map 'talk'  • to go back 'w'  • help • 'i' to check your bag");
            return;
        }

        // Block retries in the Tea Party if player already failed here
        if (tartLocked) {
            println("The Hatter shrugs, \"We tried that riddle already today.\"");
            println("You can earn the tart later in the Queen’s Garden catch-up.");
            hint("w  • look  • help");
            return;
        }

        println("The Hatter grins, \"A tart for a riddle-solver!\"");
        if (tartRiddlePuzzle()) {
            bag.add(TART);
            println("Correct! The answer is CLOCK🕰️. The Hatter slides the tart to you. Yum!");
            println("Now let's go back west to the mystery room?");
        } else {
            tartLocked = true; // lock further attempts here
            println("Oh no—riddle failed this time. The tart challenge is closed for now.");
            println("Tip: The Queen’s Garden can offer a quick challenge later to earn this curio.");
        }
        hint("take tart • w  • help");
    }




    // ROYAL COURT

    private void showRoyalCourt() {
        println("\n The Royal Court👑 ");
        println("You now stepped into the Queen’s garden court with 🌹red roses and card 🃏soldiers.");
        println("A croquet mallet and ball rest nearby; the Queen is watching with a smile.");
        println("May be we can play a game with the Queen🤼‍♀️");
        if (!bag.contains(BRASS_KEY)) {
            println("Win a quick game to earn a prize! ");
            println("Wanna try and type 'hit' three times. WIN a surprise😃(I suspect🤨 it's the key🗝️)");
            println(" Wait! The Queen holds a crown👑 and a rose🌹, Excited!! ");
            hint(" 'hit' (for a quick challenge with the queen)  • take crown  • take rose  • n (to go back)");
            println("Now go a head and type what you wanna do?☺️☺️");
        } else {
            println("You could win a crown👑 or take rose🌹 form the Queen here, or type 'n' back to the Mystery Room.");
            println("Now go a head and type what you wanna do?☺️☺️");
            hint(" 'hit' (for a quick challenge with the queen)  • take crown  • take rose  • n (to go back)");
        }
        println("Reminder to check your bag or look around if you need help☺️");
    }

    private void handleRoyalCourt() {
        String input = preprocess(control.read("\n> "));
        if (isInventoryCmd(input)) { showInventory(); return; }
        if (isLookCmd(input)) { showRoyalCourt(); return; }


        if (Control.isExitWord(input)) {
            if (confirmExit()) goodbye();
            println("Awesome, let's continue, " + playerName + "!");
            hint("hit  • take crown  • take rose  • n");
            return;
        }

        if (input.equals("help")) {
            showHelp();
            hint("hit  • take crown  • take rose  • n  • end");
            return;
        }

        if (input.equals("look") || input.equals("l")) {
            showRoyalCourt();
            return;
        }

        // Puzzle: type 'hit' three times in a row to get BRASS_KEY
        if (input.equals("hit")) {
            if (!bag.contains(BRASS_KEY)) {
                boolean won = croquetMiniGame();
                if (won) {
                    bag.add(BRASS_KEY);
                    println("The Queen claps! \"Splendid! Here is a BRASS KEY🗝️ for your courage.\"");
                    println("Congratulations "+ playerName+"  You can finally unlock the suspense door in the mystery room!");
                    println("What do you want do do next? Should we take the crown👑 and rose🌹 too?? or lets go back and try the door?");
                } else {
                    println("Almost! You can try again. Type 'hit' three times in a row.");
                }
            } else {
                println("You’ve already won the Brass Key. Nicely done!");
            }
            hint("take crown  • take rose  • 'n' to go back to mystery room  • help");
            return;
        }

        // TAKE crown / rose
        if (input.equals("crown")) { takeCrown(); return; }
        if (input.equals("rose"))  { takeRose();  return; }

        if (input.startsWith("take ")) {
            String what = input.substring(5).trim().toLowerCase();
            if (what.equals("crown")) { takeCrown(); return; }
            if (what.equals("rose"))  { takeRose();  return; }
            println("You don’t see that here. Try: take crown / take rose.");
            hint("hit  • take crown  • take rose  • n");
            return;
        }

        // Go back north
        if (input.equals("n") || input.equals("north") || input.equals("go north")) {
            state = State.MYSTERY_ROOM;
            showMysteryRoom();
            return;
        }
        println("Sorry! the Queen was just tricking you 🤣");
        println("You may now type 'hit' to try the croquet challenge.");
        println("OR take a crown👑 or a rose🌹, or choose to go back? type 'n'");
        println("waiting for your response🥱");
        hint("hit  • take crown  • take rose  • n  • help  • end");
    }

    private boolean croquetMiniGame() {
        int count = 0;
        println("The Croquet challenge⛳️! Type 'hit' three times.and win a surprise😃");


        while (count < 3) {
            String in = control.read("> ").trim().toLowerCase();

            if (Control.isExitWord(in)) {
                if (confirmExit()) goodbye();
                println("Back to the game—keep trying!");
                continue;
            }
            if (in.equals("help")) {
                println("Tip: type 'hit' and press Enter three times, or type 'hit hit hit' in one go.");
                continue;
            }

            int hitsThisInput = 0;
            boolean valid = true;

            if (in.isEmpty()) {
                valid = false;
            } else if (in.contains(" ")) {
                String[] tokens = in.split("\\s+");
                for (String t : tokens) {
                    if (t.equals("hit")) hitsThisInput++;
                    else { valid = false; break; }
                }
            } else if (in.matches("(hit)+")) {
                hitsThisInput = in.length() / 3;
            } else if (in.equals("hit")) {
                hitsThisInput = 1;
            } else {
                valid = false;
            }

            if (valid && hitsThisInput > 0) {
                count += hitsThisInput;
                if (count > 3) count = 3;
                println("Nice! (" + count + "/3)");
            } else {
                println("Oops — only type the word 'hit'. The count resets. Try again!");
                count = 0;
            }
        }
        return true;
    }

    private void takeCrown() {
        if (bag.contains(CROWN)) {
            println("You already took the crown.");
            println("Don't forget to always check whats in your bag🎒, look👀 around or ask for help if needed.");
            println("Now go a head and type what you wanna do?☺️☺️");
            hint("take rose  • 'n' (to go back to mystery room)  • help");
            return;
        }
        if (!bag.contains(BRASS_KEY)) {
            println("The Queen says, \"Win my croquet game first (type hit) to earn a prize.\"");
            hint("hit  • help");
            return;
        }

        // Block retries here after a failure
        if (crownLocked) {
            println("The Queen says, \"We tested manners already. Another time, perhaps.\"");
            println("You can try for the crown later in the Queen’s Garden catch-up.");
            hint("take rose  • n  • help");
            return;
        }

        if (crownMannersPuzzle()) {
            bag.add(CROWN);
            println("Graceful! The Queen places the CROWN upon your head.");
            println("Wanna take the rose too? or go back to the mystery room and unlock the suspense?");
        } else {
            crownLocked = true; // lock further attempts here
            println("Manners test missed. The crown challenge is closed for now.");
            println("Tip: The Queen’s Garden can offer a quick challenge later to earn this curio.");
        }
        hint("take rose  • n  • help");
    }



    private void takeRose() {
        if (bag.contains(ROSE)) {
            println("You already took the rose.");
            println("Don't forget to always check whats in your bag🎒, look👀 around or ask for help if needed.");
            println("Now go a head and type what you wanna do?☺️☺️");
            hint("take crown  • 'n' (to go back to mystery room)  • help");
            return;
        }
        if (!bag.contains(BRASS_KEY)) {
            println("The Queen says, \"Win my croquet game first (type hit) to earn a prize.\"");
            hint("hit  • help");
            return;
        }

        // Block retries here after a failure
        if (roseLocked) {
            println("The Queen nods, \"We asked about the roses already.\"");
            println("You can try for the rose later in the Queen’s Garden catch-up.");
            hint("take crown  • n  • help");
            return;
        }

        if (roseColorPuzzle()) {
            bag.add(ROSE);
            println("Yes—RED! You gently take a red ROSE. It smells wonderful!");
            println("Bravo🤩 👏! " + playerName + " where should we explore next?");
        } else {
            roseLocked = true; // lock further attempts here
            println("Color quiz missed. The rose challenge is closed for now.");
            println("Tip: The Queen’s Garden can offer a quick challenge later to earn this curio.");
        }
        hint("take crown  • n  • help");
    }




    // FOREST GARDEN

    private void showForestGarden() {
        println("\n The Forest🌳 Garden 🦌");
        println("Tall trees whisper as fireflies glow. A friendly forest guide smiles.");
        if (!bag.contains(POCKET_WATCH)) {
            if (!bag.contains(MAP_FRAGMENT)) {
                println("Your map looks incomplete. Visit the Tea Party and use 'talk' for a map piece, then return here.");
            } else {
                println("The guide shows three glowing symbols in order: TREE, MUSHROOM, ROSE.");
                println("Asking to repeat them after him🤔");

                println("let's try?? type 'talk' to try out the pattern.");
            }
        } else {
            println("You already carry the Pocket Watch. Type **e** to return to the Mystery Room.");
        }
        println("Don't forget to always check whats in your bag🎒, look👀 around or ask for help if needed.");
        println("Now go a head and type what you wanna do?☺️☺️");
        hint(" 'e' (to go back to mystery room) • help");
    }

    private void handleForestGarden() {
        String input = preprocess(control.read("\n> "));
        if (isInventoryCmd(input)) { showInventory(); return; }
        if (isLookCmd(input)) { showForestGarden(); return; }


        if (Control.isExitWord(input)) {
            if (confirmExit()) goodbye();
            println("Awesome, let's continue, " + playerName + "!");
            hint("talk  • e  • help");
            return;
        }

        if (input.equals("help")) {
            showHelp();
            hint("talk  • e  • end");
            return;
        }

        if (input.equals("look") || input.equals("l")) {
            showForestGarden();
            return;
        }

        // go back east to Mystery Room
        if (input.equals("e") || input.equals("east") || input.equals("go east")) {
            state = State.MYSTERY_ROOM;
            showMysteryRoom();
            return;
        }

        // talk → either hint (no map) or run sequence puzzle
        if (input.equals("talk")) {
            if (!bag.contains(MAP_FRAGMENT)) {
                println("The guide whispers, \"Find the missing map piece🗺️ at the Tea Party (try 'talk' there).\"");
                hint("e  • help");
                return;
            }
            if (bag.contains(POCKET_WATCH)) {
                println("The guide smiles, \"You already have the Pocket Watch⏱️. The Queen will be pleased!\"");
                hint("e  • help");
                return;
            }
            boolean solved = forestSequencePuzzle();
            if (solved) {
                bag.add(POCKET_WATCH);
                println("Brilliant, " + playerName + "😍! The guide gifts you the POCKET WATCH⏱️. Keep it safe.");

            }
            hint("to go back to mystery room type 'e'  or ask for help");
            return;
        }

        println("You can 'talk' to the guide or go 'e' back to the Mystery Room.");
        hint("talk  • e  • help  • end");
    }

   //Sequence puzzle: expects (case-insensitive) tree, mushroom, rose.
     //Accepts separators: spaces and/or commas; trims extra spaces.
    private boolean forestSequencePuzzle() {
        println("The symbols glow again: TREE, MUSHROOM, ROSE.");
        while (true) {
            String line = control.read("Repeat the sequence: ").trim();
            // allow exit/help inside puzzle
            String low = line.toLowerCase();
            if (Control.isExitWord(low)) {
                if (confirmExit()) goodbye();
                println("Okay, let’s keep going!");
                continue;
            }
            if (low.equals("help")) {
                println("Type the three words in order, case doesn’t matter. Example: tree mushroom rose");
                continue;
            }

            // Split by commas and/or whitespace, remove empties, compare in order
            String[] raw = line.split("[,\\s]+");
            if (raw.length == 3) {
                String a = raw[0].trim().toLowerCase();
                String b = raw[1].trim().toLowerCase();
                String c = raw[2].trim().toLowerCase();
                if (a.equals("tree") && b.equals("mushroom") && c.equals("rose")) {
                    return true;
                }
            }
            println("Hmm… that’s not quite right. Try again! (tip: tree mushroom rose)");
        }
    }


    // QUEEN'S GARDEN

    private void showQueensGarden() {
        println("\nYou've finally reached Queen’s Garden 🥳");
        println("The Queen’s roses💐🌹 shimmer in sunlight☀️. It feels safe and calm here.");
        println("I’ll check what you’ve collected. and see if its a win");

        // Run the win check on entry
        checkForWin();
    }


    private void handleQueensGarden() {
        String input = preprocess(control.read("\n> "));
        if (isInventoryCmd(input)) { showInventory(); return; }
        if (isLookCmd(input)) { showQueensGarden(); return; }


        if (Control.isExitWord(input)) {
            if (confirmExit()) goodbye();
            println("No worries—take your time exploring.");
            hint("s  • help");
            return;
        }

        if (input.equals("help")) {
            showHelp();
            hint("s  • end");
            return;
        }

        // allow return south to continue collecting items
        if (input.equals("s") || input.equals("south") || input.equals("go south")) {
            state = State.MYSTERY_ROOM;
            showMysteryRoom();
            return;
        }

        // If they type anything else here, gently remind them:
        println("If you’re not ready yet, you can go 's' back to explore more.");
        hint("s  • help  • end");
    }

    private void checkForWin() {
        boolean hasWatch = bag.contains(POCKET_WATCH);
        int curios = countCurios();

        if (hasWatch && curios >= 3) {
            println("\n💖 The Queen👸🏻 smiles brightly, \"" + playerName + ", you’ve done it!\"");
            println("You present the Pocket Watch⏱️ and your wonderful keepsakes.");
            println("Music swells, petals twirl🥀, and a gentle breeze🍃 carries you home.");
            println("\n🏆 You win! Thank you for playing, brave traveler🗽😍!");
            System.exit(0);
            return;
        }

        // Not yet ready — explain what's missing
        println("\nAlmost there! Here’s what you still need:");
        if (!hasWatch) {
            println(" • Pocket Watch⏱️ (find it in the Forest🌳 Garden by solving the pattern).");
        }
        if (curios < 3) {
            println(" • More curios (you need 3 total). You currently have " + curios + ".");
            println("   Curios include: Tart (Tea Party), Crown (Royal Court), Rose (Royal Court).");
        }

        // Offer catch-up only for curios
        if (curios < 3) {
            String ans = preprocess(control.read("\nWould you like to try quick challenges now to earn missing curios? (yes/no) "));
            if (ans.equals("yes") || ans.equals("y")) {
                offerCurioCatchUp();          // runs the mini-puzzles here
                // After catch-up attempts, re-check for the win automatically:
                checkForWin();
                return;
            }
        }

        println("\nWhen you’re ready, head 's' back to explore more.");
        println("Tip: i (bag) to check items, look to re-read this screen.");
    }
    //Run quick challenges in the Queen's Garden to earn any missing curios.
   //Uses the same puzzles as the original rooms, with the 2-try cap you set. */
    private void offerCurioCatchUp() {
        while (countCurios() < 3) {
            // Build a simple menu of missing curios
            boolean needTart  = !bag.contains(TART);
            boolean needCrown = !bag.contains(CROWN);
            boolean needRose  = !bag.contains(ROSE);

            StringBuilder menu = new StringBuilder("\nChoose a curio to attempt here:\n");
            if (needTart)  menu.append("  - tart\n");
            if (needCrown) menu.append("  - crown\n");
            if (needRose)  menu.append("  - rose\n");
            menu.append("Or type 'stop' to cancel and come back later.");

            println(menu.toString());
            String pick = preprocess(control.read("> "));

            if (pick.equals("stop") || pick.equals("no") || pick.equals("n")) {
                println("No problem — you can try again later.");
                return;
            }

            if (pick.equals("tart") && needTart) {
                println("\nThe Hatter’s riddle echoes in your mind...");
                if (tartRiddlePuzzle()) {
                    bag.add(TART);
                    println("Correct again — CLOCK! You receive the TART.");
                } else {
                    println("Riddle missed. You can try another curio or stop for now.");
                }
            } else if (pick.equals("crown") && needCrown) {
                println("\nThe Queen expects good manners...");
                if (crownMannersPuzzle()) {
                    bag.add(CROWN);
                    println("Graceful! The Queen places the CROWN 👑upon your head.");
                } else {
                    println("Manners test missed. Try another curio or stop for now.");
                }
            } else if (pick.equals("rose") && needRose) {
                println("\nThe Queen gestures toward the rose bushes...");
                if (roseColorPuzzle()) {
                    bag.add(ROSE);
                    println("Yes — RED! You gently take the ROSE.");
                } else {
                    println("Color quiz missed. Try another curio or stop for now.");
                }
            } else {
                println("Please choose one of the listed curios (tart, crown, rose) or type 'stop'.");
            }

            // If they’ve reached 3 curios here, we’ll exit the loop and let checkForWin() handle the ending.
            if (countCurios() >= 3) {
                println("\nWonderful! You now have enough curios.");
                return;
            }
        }
    }
    //How many curios (TART, CROWN, ROSE) the player currently has.
    private int countCurios() {
        int c = 0;
        if (bag.contains(TART))  c++;
        if (bag.contains(CROWN)) c++;
        if (bag.contains(ROSE))  c++;
        return c;
    }





    // Shared helpers

    private void showHelp() {
        println("\nHelp:");
        println("  • Move: n, s, e, w, up, down  (or: go north)");
        println("  • Look around: look (or: l)");
        println("  • Take an item: take bottle/cake/mushroom (or just type the item name)");
        println("  • Use an item: use bottle/cake/mushroom");
        println("  • Tea Party: talk (get a Map Fragment), take tart");
        println("  • Royal Court: type 'hit' 3x to earn Brass Key; take crown/rose; 'n' to go back");
        println("  • Forest Garden: talk to repeat the sequence (tree, mushroom, rose) and earn Pocket Watch; 'e' to go back");
        println("  • Queen’s Garden: reach here with Pocket Watch + 3 curios to win! ('s' to leave)");
        println("  • Quit: exit / end / quit (we’ll confirm)");
        println("Goal: Find the Pocket Watch and bring it to the Queen’s Garden.");
    }

    private boolean confirmExit() {
        while (true) {
            String ans = control.read("Are you sure you want to end the game? (yes/no) ").toLowerCase();
            if (ans.equals("yes") || ans.equals("y")) return true;
            if (ans.equals("no")  || ans.equals("n")) return false;
            println("Please answer 'yes' or 'no'.");
        }
    }

    private void goodbye() {
        println("Sad to see you leave😞 Goodbye, " + playerName + "!");
        System.exit(0);
    }

    private static String norm(String raw) {
        if (raw == null) return "";
        return raw.trim().toUpperCase().replace(' ', '_');
    }

    private static void println(String s) { System.out.println(s); }
    private static void hint(String s) { System.out.println("(Try: " + s + ")"); }
}

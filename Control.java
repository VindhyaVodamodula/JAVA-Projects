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

import java.util.Scanner;

//input helper so Game stays clean.
public class Control {
    private final Scanner in = new Scanner(System.in);

    /** Read a line with an optional prompt. Never returns null; always trimmed. */
    public String read(String prompt) {
        if (prompt != null && !prompt.isBlank()) System.out.print(prompt);
        String s = in.nextLine();
        return (s == null) ? "" : s.trim();
    }

    /** True if the word is a quit attempt (exit / end / quit). */
    public static boolean isExitWord(String s) {
        if (s == null) return false;
        String x = s.trim().toLowerCase();
        return x.equals("exit") || x.equals("end") || x.equals("quit");
    }
}


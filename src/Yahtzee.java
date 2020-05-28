import java.util.InputMismatchException;
import java.util.Scanner;

public class Yahtzee {
    public static void main(String[] args){
        Scanner in = new Scanner(System.in);
        Player[] players;
        int numPlayers = 0;
        boolean[] reroll = new boolean[5];

        System.out.print("How many players? ");
        try {
            numPlayers = in.nextInt();
        }
        catch (InputMismatchException e){
            System.out.println("Invalid input. I guess you don't want to play.");
            System.exit(1);
        }
        players = new Player[numPlayers];
        String[] names = new String[numPlayers];
        for(int i = 0; i < numPlayers; i++) {
            System.out.print("What is player " + (i + 1) + "'s name? ");
            names[i] = in.next();
            players[i] = new Player();
        }


        // 13 turns
        for(int i = 0; i < 13; i++) {
            for(int j = 0; j < numPlayers; j++){
                System.out.println(names[j] + "'s turn!");
                takeTurn(players[j], in);
                System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
            }
        }
        if(numPlayers > 1) {
            System.out.println("Final Scores: ");
            int max = 0;
            for (int i = 0; i < numPlayers; i++) {
                System.out.println(names[i] + ": " + players[i].getScore());
                if (players[i].getScore()[18] > players[max].getScore()[18]) {
                    max = i;
                }
            }
            System.out.println(names[max] + " wins!");
            players[max].printScores();
        }
        else {
            players[0].printScores();
        }
        System.out.println("Good Game!");
    }

    public static boolean[] getReroll(Player p) {
        Scanner in = new Scanner(System.in);
        boolean[] reroll = new boolean[5];
        for(int i = 0; i < 5; i++) {
            System.out.print("Would you like to reroll die " + (i + 1) + "(" + p.getDice()[i] + ")? (y/n): ");
            reroll[i] = (in.next().charAt(0) == 'y');
        }
        return reroll;
    }

    public static void takeTurn(Player p, Scanner in) {
            p.printScores();
            p.roll();
            p.printRoll();
            p.roll(getReroll(p));
            p.printRoll();
            p.roll(getReroll(p));
            p.printRoll();
            boolean success;
            do {
                success = true;
                try {
                    System.out.print("At which index would you like to score this roll? ");
                    p.enterScore(in.nextInt());
                }
                catch(IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                    success = false;
                }
                catch(InputMismatchException e) {
                    System.out.println("Please enter an index number.");
                    in.next();
                    success = false;
                }
            } while(!success);
    }

}

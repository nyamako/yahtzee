import java.util.Random;

public class Player {
    private int turnNumber;
    private int rollCount;
    private int[] dice;
    private Integer[] scorecard;
    private static final String[] categories = {
            "Ones",
            "Twos",
            "Threes",
            "Fours",
            "Fives",
            "Sixes",
            "Total Score",
            "Bonus",
            "Upper Total",
            "3 of a Kind",
            "4 of a Kind",
            "Full House",
            "Small Straight",
            "Large Straight",
            "Yahtzee",
            "Bonus Yahtzees",
            "Chance",
            "Lower Total",
            "Grand Total"
    };

    public Player() {
        turnNumber = 0;
        dice = new int[5];
        scorecard = new Integer[19];
    }

    public void reset() {
        turnNumber = 0;
        rollCount = 0;
        scorecard = new Integer[19];
        dice = new int[5];
    }

    public boolean gameOver() {
        return (turnNumber > 12);
    }

    public void roll() {
        if (rollCount < 3) {
            Random rand = new Random();
            for (int i = 0; i < 5; i++) {
                dice[i] = rand.nextInt(6) + 1;
            }
        }
    }

    public void roll(boolean[] reroll) {
        if (!outOfRolls()) {
            Random rand = new Random();
            for (int i = 0; i < 5; i++) {
                if (reroll[i]) {
                    dice[i] = rand.nextInt(6) + 1;
                }
            }
            rollCount++;
        }
    }

    public boolean outOfRolls() {
        return rollCount >= 3;
    }

    public Integer[] getScore() {
        return scorecard.clone();
    }

    public int[] getDice() {
        return dice.clone();
    }

    public static String[] getType() {
        return categories.clone();
    }

    public void printRoll() {
        for (int die : dice) {
            System.out.print(die);
        }
        System.out.println();
    }

    public void printScores() {
        for (int i = 0; i < 19; i++) {
            System.out.println(i + ": " + categories[i] + ":\t" + (scorecard[i] != null ? scorecard[i] : ""));
        }
    }

    public Integer scoreValue(int index) {
        if (index < 0 || index > 16) {
            throw new IllegalArgumentException("Invalid index: " + index);
        }
        if (scorecard[index] != null) {
            throw new IllegalArgumentException("Index " + index + " already scored.");
        }
        /* JAVA 14
        scorecard[index] = switch(index) {
            case 0, 1, 2, 3, 4, 5 -> scoreTopHalf(index + 1);
            case 9, 10 -> scoreOfAKind(index - 6);
            case 11 -> scoreFullHouse();
            case 12, 13 -> scoreStraight(index - 8);
            case 14 -> scoreYahtzee();
            case 16 -> sumDice();
            default -> throw new IllegalArgumentException("Invalid index: " + index);
        };
        */
        int value;
        switch (index) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                value = scoreTopHalf(index + 1);
                break;
            case 9:
            case 10:
                value = scoreOfAKind(index - 6);
                break;
            case 11:
                value = scoreFullHouse();
                break;
            case 12:
            case 13:
                value = scoreStraight(index - 8);
                break;
            case 14:
                value = scoreYahtzee();
                break;
            case 16:
                value = sumDice();
                break;
            default:
                throw new IllegalArgumentException("Invalid index: " + index);
        }
        return value;
    }

    public void enterScore(int index) throws IllegalArgumentException {
        if (index < 0 || index > 16) {
            throw new IllegalArgumentException("Invalid index: " + index);
        }
        if (scorecard[index] != null) {
            throw new IllegalArgumentException("Index " + index + " already scored.");
        }
        scorecard[index] = scoreValue(index);
        // Check for Bonus Yahtzees
        if (index != 14 || scorecard[index] == 0) {
            scoreBonusYahtzee();
        }
        //update totals
        updateTotals(index);
        //reset roll count
        rollCount = 0;
        turnNumber++;
    }

    private void updateTotals(int index) {
        // Update Upper Total
        if (index < 6) {
            if (scorecard[6] != null) {
                scorecard[6] += scorecard[index];
            } else {
                scorecard[6] = scorecard[index];
            }
            // check upper bonus
            scoreBonus();
            scorecard[8] = scorecard[6] + (scorecard[7] != null ? scorecard[7] : 0);
        }
        // Update Lower Total
        else {
            if (scorecard[17] != null) {
                scorecard[17] += scorecard[index];
            } else {
                scorecard[17] = scorecard[index];
            }
        }
        // Update Grand Total
        scorecard[18] = (scorecard[8] != null ? scorecard[8] : 0) + (scorecard[17] != null ? scorecard[17] : 0);
    }


    // takes int parameter for number to score
    private int scoreTopHalf(int i) {
        int total = 0;
        for (int die : dice) {
            if (die == i) {
                total += i;
            }
        }
        return total;
    }

    private void scoreBonus() {
        boolean topFull = true;
        int sum = 0;
        for (int i = 0; i < 6; i++) {
            if (scorecard[i] == null) {
                topFull = false;
            }
            sum += (scorecard[i] != null ? scorecard[i] : 0);
        }
        if (!topFull && sum < 63) {
            scorecard[7] = null;
        } else {
            // Top Section full or sum >= 63
            scorecard[7] = (sum >= 63 ? 35 : 0);
        }
    }

    private int sumDice() {
        int total = 0;
        for (int die : dice) {
            total += die;
        }
        return total;
    }

    private int[] diceCounts() {
        int[] counts = {0, 0, 0, 0, 0, 0};
        for (int die : dice) {
            // number on die - 1 = index
            counts[die - 1] += 1;
        }
        return counts;
    }


    // takes int parameter n for number of matching dice
    private boolean isOfAKind(int n) {
        for (int i : diceCounts()) {
            if (i >= n) {
                return true;
            }
        }
        return false;
    }

    private int scoreOfAKind(int n) {
        return (isOfAKind(n) ? sumDice() : 0);
    }

    private int scoreYahtzee() {
        return (isOfAKind(5) ? 50 : 0);
    }

    private void scoreBonusYahtzee() {
        // add 100 if roll is yahtzee and first yahtzee already filled in
        if (scorecard[14] != null && scorecard[14] == 0) {
            scorecard[15] = 0;
        } else if (isOfAKind(5) && scorecard[14] == 50) {
            if (scorecard[15] == null) {
                scorecard[15] = 100;
            } else {
                scorecard[15] += 100;
            }
            // Update Lower Total
            scorecard[17] += 100;
        }
    }

    private boolean isFullHouse() {
        for (int i : diceCounts()) {
            // if any number appears exactly once (yahtzee counts as full house)
            if (i == 1) {
                return false;
            }
        }
        return true;
    }

    private int scoreFullHouse() {
        return (isFullHouse() ? 25 : 0);
    }

    private boolean isStraight(int size) {
        int consecutiveCount = 0;
        int[] counts = diceCounts();
        for (int i = 0; i < 6; i++) {
            if (counts[i] != 0) {
                consecutiveCount++;
            } else {
                consecutiveCount = 0;
            }
            if (consecutiveCount >= size) {
                return true;
            }
        }
        return false;
    }

    private int scoreStraight(int n) {
        return (isStraight(n) ? ((n - 1) * 10) : 0);
    }
}


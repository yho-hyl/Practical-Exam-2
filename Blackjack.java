/*
I have only used resources indicated that are allowable.
The work submitted is my/our, and only my/our work.
Signature: Yusuke Ho and person sitting at the 3rd seat from the right at the front
Date: 11/9/2026
*/
import java.util.ArrayList;
import java.util.Collections; //Used to shuffle list
import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.io.PrintWriter;

/**
 * Simulates a console-based Blackjack game featuring betting,
 * save/load state persistence, and game statistics tracking.
 */
public class Blackjack {

    /**
     * Entry point of the Blackjack application. Manages the main game loop,
     * user betting input, round dealing, player/dealer turns, and file saving.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        Deck deck = new Deck();

        Player player = new Player("Player");
        Dealer dealer = new Dealer();
        player.load(); // AI

        System.out.println("Welcome to Blackjack!");

        while (player.getBalance() > 0) {
            double betAmount = 0;
            System.out.println("Current Balance: " + player.getBalance());
            System.out.println("Enter your bet or enter 0 to quit: ");

            try {
                betAmount = input.nextDouble();
            } catch (Exception e) {
                System.out.println("Please enter a valid number.");
                input.next();
                continue;
            }

            if (betAmount == 0) { // Quit game
                break;
            }
            if (betAmount < 0 || !player.hasEnoughBalance(betAmount)) {
                System.out.println("Invalid bet! You only have $" + player.getBalance());
                continue;
            }

            // Prepare game
            deck.reset();
            deck.shuffle();
            player.clearHand();
            dealer.clearHand();

            // Deal 2 cards each
            player.addCard(deck.drawCard());
            dealer.addCard(deck.drawCard());
            player.addCard(deck.drawCard());
            dealer.addCard(deck.drawCard());

            //AI - Had AI format the print output
            System.out.println("\nDealer shows: [" + dealer.getHand().get(0) + ", ?]");
            System.out.println("Your hand: " + player.getHand() + " (Value: " + player.getHandValue() + ")");

            // Check natural Blackjacks
            if (player.isBlackjack() || dealer.isBlackjack()) {
                System.out.println("Dealer's hand: " + dealer.getHand() + " (Value: " + dealer.getHandValue() + ")");
                if (player.isBlackjack() && dealer.isBlackjack()) {
                    System.out.println("Both have Blackjack! Push (bet returned).");
                    player.pushes++;

                } else if (player.isBlackjack()) {
                    System.out.println("Blackjack! You win " + betAmount * 1.5 + "!");
                    player.win(betAmount * 1.5);

                } else {
                    System.out.println("Dealer has Blackjack! You lose.");
                    player.lose(betAmount);
                }
                continue;
            }

            // Player's turn
            while (!player.isBust()) {
                System.out.print("Hit or Stand? (h/s): ");
                String choice = input.next().trim().toLowerCase();

                if (choice.equals("h")) {
                    player.addCard(deck.drawCard());
                    System.out.println("Your hand: " + player.getHand() + " (Value: " + player.getHandValue() + ")");

                } else if (choice.equals("s")) {
                    break;

                } else {
                    System.out.println("Invalid choice. Type 'h' or 's'.");
                }
            }

            // Bust
            if (player.isBust()) {
                System.out.println("Bust! You went over 21 and lost $" + (betAmount) + "!");
                player.lose(betAmount);

            } else {
                // Dealer hits until 17 or higher
                dealer.playTurn(deck);

                System.out.println("\nDealer's final hand: " + dealer.getHand() + " (Value: " + dealer.getHandValue() + ")");

                int playerScore = player.getHandValue();
                int dealerScore = dealer.getHandValue();

                if (dealer.isBust() || playerScore > dealerScore || playerScore == 21) {
                    System.out.print("You win $" + betAmount + "\n");
                    player.win(betAmount);

                } else if (playerScore == dealerScore) {
                    System.out.println("Push! Your bet is returned.");
                    player.pushes++;

                } else {
                    System.out.print("Dealer wins! You lost " + betAmount + "\n");
                    player.lose(betAmount);
                }
            }
        }

        if (player.getBalance() <= 0) {
            System.out.println("ざーこ");
        } else {
            System.out.println("You ended with $" + player.getBalance());
        }

        player.save(); 
        player.writeStats(); // AI
        input.close();
    }

    /**
     * Represents a single playing card with a rank and a suit.
     */
    // CARD CLASS - AI
    public static class Card {

        /**
         * The four standard playing card suits:
         * Clubs (C), Diamonds (D), Hearts (H), and Spades (S).
         */
        public enum Suit {
            C, D, H, S
        }

        /** All valid card ranks from Ace (1) through King (13). */
        public static int[] numbers = {
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13
        };

        private final int rank;
        private final Suit suit;

        /**
         * Constructs a new Card with the specified rank and suit.
         *
         * @param rank The numerical rank of the card (1–13).
         * @param suit The suit of the card.
         */
        public Card(int rank, Suit suit) {
            this.rank = rank;
            this.suit = suit;
        }

        /**
         * Gets the numerical rank of the card.
         *
         * @return The rank (1–13).
         */
        public int getRank() {
            return rank;
        }

        /**
         * Gets the suit of the card.
         *
         * @return The card's Suit.
         */
        public Suit getSuit() {
            return suit;
        }

        /**
         * Returns a compact string representation of the card (e.g., "H10").
         *
         * @return The suit followed by the rank.
         */
        @Override
        public String toString() {
            return suit + "" + rank;
        }
    }


    
    /**
     * Represents a standard 52-card deck that can be reset, shuffled, and drawn from.
     */
    public static class Deck {

        private final List<Card> cards = new ArrayList<>();

        /**
         * Constructs a new Deck populated with all 52 cards.
         */
        public Deck() {
            reset();
        }

        /**
         * Clears the deck and repopulates it with all 52 unique suit-and-rank combinations.
         */
        public void reset() {        
            cards.clear();

            Card.Suit[] allSuits = Card.Suit.values();
            int[] allNumbers = Card.numbers;

            for (int i = 0; i < allSuits.length; i++) {
                for (int j = 0; j < allNumbers.length; j++) {
                    Card.Suit currentSuit = allSuits[i];
                    int currentRank = allNumbers[j];

                    Card newCard = new Card(currentRank, currentSuit);
                    addCard(newCard);
                }
            }
        }

        /**
         * Adds a single card to the deck.
         *
         * @param card The Card object to add.
         * @return true once the card has been added.
         */
        public boolean addCard(Card card) {
            cards.add(card);
            return true;
        }

        /**
         * Randomizes the order of the cards remaining in the deck.
         */
        public void shuffle() {
            Collections.shuffle(cards);
        }

        /**
         * Removes and returns the top card from the deck.
         *
         * @return The drawn Card, or null if the deck is empty.
         */
        public Card drawCard() {
            if (cards.isEmpty()) {
                return null;
            }
            Card topCard = cards.remove(cards.size() - 1);
            return topCard;
        }
    }

    /**
     * Represents a Blackjack player, tracking their hand, score, betting balance,
     * and cumulative game statistics.
     */
    public static class Player {
        private final String name;
        private final List<Card> hand = new ArrayList<>();

        /**
         * Constructs a new Player with the given name.
         *
         * @param name The display name of the player.
         */
        public Player(String name) {
            this.name = name;
        }

        /**
         * Gets the player's name.
         *
         * @return The player's name.
         */
        public String getName() {
            return name;
        }

        /**
         * Adds a drawn card to the player's current hand.
         *
         * @param card The Card to add.
         */
        public void addCard(Card card) {
            hand.add(card);
        }

        /**
         * Gets the list of cards currently held by the player.
         *
         * @return A List of Card objects in the player's hand.
         */
        public List<Card> getHand() {
            return hand;
        }

        /** Current monetary balance available for betting. */
        public double balance = 100.00;
        public int wins = 0;
        public int losses = 0;
        public int pushes = 0;
        public double net = 0;

        /**
         * Gets the player's current balance.
         *
         * @return The current balance.
         */
        public double getBalance() {
            return balance;
        }

        /**
         * Sets the player's balance to a specific amount.
         *
         * @param balance The new balance value.
         */
        public void setBalance(double balance) {
            this.balance = balance;
        }

        /**
         * Records a win by increasing the balance and net earnings, and incrementing wins.
         *
         * @param amount The amount won.
         */
        public void win(double amount) {
            this.balance += amount;
            this.net += amount;
            this.wins++;
        }

        /**
         * Records a loss by decreasing the balance and net earnings, and incrementing losses.
         *
         * @param amount The amount lost.
         */
        public void lose(double amount) {
            this.balance -= amount;
            this.net -= amount; 
            this.losses++; 
        }

        /**
         * Checks whether the player has enough balance to cover a given bet.
         *
         * @param amount The wager amount to check.
         * @return true if the balance is greater than or equal to the amount; false otherwise.
         */
        public boolean hasEnoughBalance(double amount) {
            return this.balance >= amount;
        }

        /**
         * Deducts a bet amount from the player's balance if funds are sufficient.
         *
         * @param betAmount The amount to wager.
         * @return The wagered bet amount.
         */
        public double bet(double betAmount) {
            if (hasEnoughBalance(betAmount)) {
                this.balance -= betAmount;
            } else {
                System.out.println("Not enough balance to bet.");
            }
            return betAmount;
        }

        /**
         * Removes all cards from the player's hand at the start of a new round.
         */
        public void clearHand() {
            hand.clear();
        }

        /**
         * Gets the number of cards currently in the player's hand.
         *
         * @return The card count in the hand.
         */
        public int getHandSize() {
            return hand.size();
        }

        /**
         * Calculates the optimal Blackjack score of the player's hand, counting face cards as 10 and Aces as 11 or 1 to avoid busting.
         *
         * @return The total point value of the hand.
         */
        public int getHandValue() {
            int value = 0;
            int aces = 0;

            for (int i = 0; i < hand.size(); i++) {
                Card currentCard = hand.get(i);
                int rank = currentCard.getRank();

                if (rank == 1) {
                    aces++;
                    value += 11;
                } else if (rank >= 10) {
                    value += 10;
                } else {
                    value += rank;
                }
            }

            while (value > 21 && aces > 0) {
                value -= 10;
                aces--;
            }
            return value;
        }
        
        /**
         * Determines whether the player's hand value exceeds 21.
         *
         * @return true if the hand value is over 21; false otherwise.
         */
        public boolean isBust() {
            return getHandValue() > 21;
        }

        /**
         * Determines whether the player was dealt a natural Blackjack (21 with 2 cards).
         *
         * @return true if the hand has 2 cards totaling 21; false otherwise.
         */
        public boolean isBlackjack() {
            return getHandSize() == 2 && getHandValue() == 21;
        }

        /**
         * Saves the current balance and game statistics to "save.txt".
         */
        public void save() {
            try {
                PrintWriter out = new PrintWriter("save.txt");
                out.println(balance + " " + wins + " " + losses + " " + pushes + " " + net);
                out.close();
            } catch (Exception e) {}
        }

        /**
         * Loads the saved balance and game statistics from "save.txt" if available. Resets balance to $100.00 if the saved balance was deleted.
         */
        // AI - didn't know how to import data from .txt
        public void load() {
            try {
                Scanner inport = new Scanner(new File("save.txt"));
                balance = inport.nextDouble();
                wins = inport.nextInt();
                losses = inport.nextInt();
                pushes = inport.nextInt();
                net = inport.nextDouble();
                inport.close(); //AI - I dont usually close Scanner
                if (balance <= 0) balance = 100.00;
            } catch (Exception e) {}
        }

        /**
         * Writes a formatted summary of total games, win/loss/push counts, net monetary result, and win percentage to "stats.txt".
         */
        // AI - I wanted to check on my average stats
        public void writeStats() {
            try {
                int total = wins + losses + pushes;
                double winRate = (total > 0) ? (wins * 100.0 / total) : 0;
                PrintWriter out = new PrintWriter("stats.txt");
                out.println("Total Games: " + total);
                out.println("Wins: " + wins + " | Losses: " + losses + " | Pushes: " + pushes);
                out.println("Net Result: $" + net);
                out.println("Win Rate: " + winRate + "%");
                out.close();
            } catch (Exception e) {}
        }
    }

    /**
     * Represents the Blackjack dealer, inheriting hand management from Player and automating the dealer's drawing rules.
     */
    // Dealer subclass
    // Semi-AI Had AI help me come up with ways to use subclasses
    public static class Dealer extends Player {

        /**
         * Constructs a new Dealer named "Dealer".
         */
        public Dealer() {
            super("Dealer");
        }

        /**
         * Executes the dealer's turn by drawing cards until the hand value reaches at least 17.
         *
         * @param deck The Deck from which to draw cards.
         */
        public void playTurn(Deck deck) {
            while (getHandValue() < 17) {
                addCard(deck.drawCard());
            }
        }
    }
}
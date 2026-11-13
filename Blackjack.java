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

public class Blackjack {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Deck deck = new Deck();

        Player player = new Player("Player");
        Player dealer = new Player("Dealer");

        System.out.println("Welcome to Blackjack!");

        while (player.getBalance() > 0) {
            System.out.print("\nCurrent Balance: "+ player.getBalance() + "\n");
            System.out.print("Enter your bet or enter 0 to quit: ");

            if (!scanner.hasNextDouble()) {
                System.out.println("Please enter a valid number.");
                scanner.next();
                continue;
            }

            double betAmount = scanner.nextDouble();
            if (betAmount == 0) {
                break;
            }
            if (betAmount < 0 || !player.hasEnoughBalance(betAmount)) {
                System.out.println("Invalid bet! You only have $" + player.getBalance());
                continue;
            } 

            // Prepare round
            deck.reset();
            deck.shuffle();
            player.clearHand();
            dealer.clearHand();

            // Deal 2 cards each
            player.addCard(deck.drawCard());
            dealer.addCard(deck.drawCard());
            player.addCard(deck.drawCard());
            dealer.addCard(deck.drawCard());

            System.out.println("\nDealer shows: [" + dealer.getHand().get(0) + ", ?]");
            System.out.println("Your hand: " + player.getHand() + " (Value: " + player.getHandValue() + ")");

            // Check natural Blackjacks
            if (player.isBlackjack() || dealer.isBlackjack()) {
                System.out.println("Dealer's hand: " + dealer.getHand() + " (Value: " + dealer.getHandValue() + ")");
                if (player.isBlackjack() && dealer.isBlackjack()) {
                    System.out.println("Both have Blackjack! Push (bet returned).");
                    
                } else if (player.isBlackjack()) {
                    System.out.print("Blackjack! You win " + betAmount * 1.5 + "!\n");
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
                String choice = scanner.next().trim().toLowerCase();

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
                while (dealer.getHandValue() < 17) {
                    dealer.addCard(deck.drawCard());

                }

                System.out.println("\nDealer's final hand: " + dealer.getHand() + " (Value: " + dealer.getHandValue() + ")");

                int playerScore = player.getHandValue();
                int dealerScore = dealer.getHandValue();

                if (dealer.isBust() || playerScore > dealerScore || playerScore == 21) {
                    System.out.print("You win $" + betAmount);
                    player.win(betAmount);

                } else if (playerScore == dealerScore) {
                    System.out.println("Push! Your bet is returned.");

                } else {
                    System.out.print("Dealer wins! You lost " + betAmount + "\n");
                    player.lose(betAmount);

                }
            }
        }

        if (player.getBalance() <= 0) {
            System.out.println("ざーこ");

        } else {
            System.out.print("You ended with $" + player.getBalance());
            
        }

        scanner.close();
    }

    // CARD CLASS   -   AI 
    public static class Card {

        public enum Suit {
            C, D, H, S
        }

        public static int[] numbers = {
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13
        };

        private final int rank;
        private final Suit suit;

        public Card(int rank, Suit suit) {
            this.rank = rank;
            this.suit = suit;
        }

        public int getRank() {
            return rank;
        }

        public Suit getSuit() {
            return suit;
        }

        @Override
        public String toString() {
            return suit + "" + rank;
        }
    }

    // DECK CLASS
    public static class Deck {

        private final List<Card> cards = new ArrayList<>();

        public Deck() {
            reset();
        }

        public void reset() {
            cards.clear();

            for (Card.Suit suit : Card.Suit.values()) {
                for (int rank : Card.numbers) {
                    addCard(new Card(rank, suit));
                }
            }
        }

        public boolean addCard(Card card) {
            cards.add(card);
            return true;
        }

        public void shuffle() {
            Collections.shuffle(cards);
        }

        public Card drawCard() {
            if (cards.isEmpty()) {
                return null;
            }
            Card topCard = cards.remove(cards.size() - 1);
            return topCard;
        }
    }

    // Player class
    public static class Player {
        private final String name;
        private final List<Card> hand = new ArrayList<>();

        public Player(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void addCard(Card card) {
            hand.add(card);
        }

        public List<Card> getHand() {
            return hand;
        }

        // Betting (Money)
        public double balance = 100.00; 
        public double getBalance() {
            return balance;
        }

        public void setBalance(double balance) {
            this.balance = balance;
        }
        public void win(double amount) {
            this.balance += amount;
        }

        public void lose(double amount) {
            this.balance -= amount;
        }

        public boolean hasEnoughBalance(double amount) {
            return this.balance >= amount;
        }

        public double bet(double betAmount) {
            if (hasEnoughBalance(betAmount)) {
                this.balance -= betAmount;
            } else {
                System.out.println("Not enough balance to bet.");
            }
            return betAmount;
        }

        public void clearHand() {
            hand.clear();
        }

        public int getHandSize() {
            return hand.size();
        }


        public int getHandValue() {
            int value = 0;
            int aces = 0;
            for (Card card : hand) {
                int rank = card.getRank();
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

        public boolean isBust() {
            return getHandValue() > 21;
        }   
        public boolean isBlackjack() {
            return getHandSize() == 2 && getHandValue() == 21;
        }


        
    }
}
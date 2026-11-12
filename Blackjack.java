/*
I have only used resources indicated that are allowable.
The work submitted is my/our, and only my/our work.
Signature: Yusuke Ho and person sitting at the 3rd seat from the right at the front
Date: 11/9/2026
*/
import java.util.ArrayList;
import java.util.Collections; //Used to shuffle list
import java.util.List;

public class Blackjack {

    public static void main(String[] args) {
        // Now works because Deck is a static inner class
        Deck deck = new Deck();
        deck.shuffle();
        String[] names = {"Player", "Dealer"};
    
        List<Player> players = new ArrayList<>();
        
        

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
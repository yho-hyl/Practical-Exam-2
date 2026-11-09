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
String[] names = new String[3];
        
        System.out.println("Drawn card: " + deck.drawCard());
        System.out.println("Drawn card: " + deck.drawCard());
    }

    // 1. CARD CLASS (Must be static when defined inside another class)
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

    // 2. DECK CLASS (Added 'static' keyword so main method can use it)
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
}
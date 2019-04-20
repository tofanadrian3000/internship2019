package DTO;

import Model.Card;

import java.time.LocalDateTime;
import java.util.Map;

public class CardTransactionDTO {
    private Card card;
    private Map<Card, Double> cardsMoneyAmount;

    public CardTransactionDTO(Card card, Map<Card, Double> cardsMoneyAmount) {
        this.card = card;
        this.cardsMoneyAmount = cardsMoneyAmount;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public Map<Card, Double> getCardsMoneyAmount() {
        return cardsMoneyAmount;
    }

    public void setCardsMoneyAmount(Map<Card, Double> cardsMoneyAmount) {
        this.cardsMoneyAmount = cardsMoneyAmount;
    }

    @Override
    public String toString() {
        return "CardTransactionDTO{" +
                "card=" + card +
                ", cardsMoneyAmount=" + cardsMoneyAmount +
                '}';
    }
}

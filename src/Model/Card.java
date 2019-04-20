package Model;

import java.time.LocalDate;
import java.util.Objects;

public class Card implements Identifiable<String>{
    //required
    private String creditCard; // identifier for the class
    private Double fee, availableAmount;

    //optional
    private Double withdrawLimit;
    private LocalDate expirationDate;

    public Card(Card card){
        this.creditCard = card.creditCard;
        this.fee = card.fee;
        this.availableAmount = card.availableAmount;
        this.withdrawLimit = card.withdrawLimit;
        this.expirationDate = card.expirationDate;
    }

    public Card(CardBuilder cardBuilder) {
        this.creditCard = cardBuilder.creditCard;
        this.fee = cardBuilder.fee;
        this.withdrawLimit = cardBuilder.withdrawLimit;
        this.availableAmount = cardBuilder.availableAmount;
        this.expirationDate = cardBuilder.expirationDate;
    }

    @Override
    public String getId() {
        return creditCard;
    }

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }

    public Double getAvailableAmount() {
        return availableAmount;
    }

    public void setAvailableAmount(Double availableAmount) {
        this.availableAmount = availableAmount;
    }

    public Double getWithdrawLimit() {
        return withdrawLimit;
    }

    public void setWithdrawLimit(Double withdrawLimit) {
        this.withdrawLimit = withdrawLimit;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return Objects.equals(creditCard, card.creditCard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(creditCard);
    }

    @Override
    public String toString() {
        return "Card{" +
                "creditCard='" + creditCard + '\'' +
                ", fee=" + fee +
                ", availableAmount=" + availableAmount +
                ", withdrawLimit=" + withdrawLimit +
                ", expirationDate=" + expirationDate +
                '}';
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public static class CardBuilder{
        //required
        private String creditCard;
        private Double fee, availableAmount;

        //optional
        private Double withdrawLimit;
        private LocalDate expirationDate;

        public CardBuilder(String creditCard, Double fee, Double availableAmount){
            this.creditCard = creditCard;
            this.fee = fee;
            this.availableAmount = availableAmount;
            this.withdrawLimit = 10000.0;//default withdraw limit
            this.expirationDate = LocalDate.of(2024,12,31);//default expiration data
        }

        public CardBuilder setWithdrawLimit(Double withdrawLimit){
            this.withdrawLimit = withdrawLimit;
            return this;
        }

        public CardBuilder setExpirationDate(LocalDate expirationDate){
            this.expirationDate = expirationDate;
            return this;
        }

        public Card build(){
            return new Card(this);
        }
    }
}

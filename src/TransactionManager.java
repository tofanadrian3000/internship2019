import DTO.CardTransactionDTO;
import Model.Atm;
import Model.Card;
import Model.Cost;
import Model.WalkingTime;
import Service.AtmService;
import Service.CardService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class TransactionManager implements ITransactionManager{
    private AtmService atmService;
    private CardService cardService;
    private static LocalDateTime currentDateTime;
    private static Double productValue = 10000.0;//default product value
    private static Double tvaProduct = 0.19;
    public TransactionManager(AtmService atmService, CardService cardService) {
        this.atmService = atmService;
        this.cardService = cardService;
    }

    private void setCurrentDateTime(){
        currentDateTime = LocalDateTime.of(2019,3,19,11,30);//default current date and time
    }

    public TransactionManager(){
        atmService = new AtmService();
        cardService = new CardService();
        setCurrentDateTime();
    }

    /**
     * In my opinion, all cards except the one which is the best to complete the transaction from should
     *      not have TVA. TVA is charged just when the transaction is completed, not before from every card.
     *      But then the method getCardsCost() would've been inconsistent since all TVAs from every Cost would've
     *      been 0 except one. So I've chose to think like this:
     *
     * Gets tva amount and fee amount payed from each card for the product
     * It's considered that tva is payed proportionally from each card
     * It's considered that the fee for the final transaction is the card's fee
     * @return all the cards used for the transaction, associated with their Cost(tva,fee)
     */
    @Override
    public Map<Card, Cost> getCardsCost() {
        CardTransactionDTO cardTransactionDTO = getBestCardForTransaction();
        if(cardTransactionDTO==null)
            return null;
        Map<Card,Cost> cardsCost = new HashMap<>();
        Card bestCard = cardTransactionDTO.getCard();
        Double realFeeAmountBestCard = bestCard.getFee()*productValue;
        Double proportionallyFeeAmountBestCard = getFeeFromValue(bestCard,bestCard.getAvailableAmount());
        Double tvaAmountBestCard = getTVAFromValue(bestCard.getAvailableAmount()-proportionallyFeeAmountBestCard,tvaProduct);
        cardsCost.put(bestCard, new Cost(tvaAmountBestCard,realFeeAmountBestCard));
        bestCard.setAvailableAmount(0.0);
        cardTransactionDTO.getCardsMoneyAmount().forEach((k,v)->{
            Double feeAmount = getFeeFromValue(k,v);
            Double tvaAmount = getTVAFromValue(v-feeAmount,tvaProduct);
            cardsCost.put(k, new Cost(tvaAmount,feeAmount));
        });
        return cardsCost;
    }

    /**
     * Gets just the fee amount from a total value with fee
     * @param card - card used
     * @param value - total value
     * @return the value of the fee
     */
    private Double getFeeFromValue(Card card, Double value){
        return (value/(1+card.getFee()))*card.getFee();
    }

    /**
     * Gets just the TVA amount from a total value without fee
     * @param value - total value
     * @param tva - tva (percentage)
     * @return the value of the TVA
     */
    private Double getTVAFromValue(Double value, Double tva){
        return (value/(1+tva))*tva;
    }

    /**
     * Gets the best card to make the transaction from and all the others cards used,
     *      considering that we are using a path through the Atms, keeping in mind
     *      the time between them and cards expiration date relative to the current date.
     * Best card to be used is selected by choosing the card with the lowest total fee amount
     * @return best card to complete the transaction along with all the others cards used
     * @return null if the transaction is impossible to be completed
     */
    private CardTransactionDTO getBestCardForTransaction(){
        List<WalkingTime> bestPath = atmService.getBestPath(currentDateTime);
        Double minimumTotalAmount = null;
        Map<Card,Double> bestCardsMoney = new HashMap<>();
        Card bestCard = null;
        for (Card card: cardService.getAll().values()) {
            Map<Card, Double> cardsMoney = getTotalAmountForMovingMoneyToCard(card, bestPath);
            if(cardsMoney==null)
                continue;
            Double totalAmount = cardsMoney.values().stream().mapToDouble(x -> x).sum();
            if ((minimumTotalAmount==null || totalAmount < minimumTotalAmount) && card.getExpirationDate().isAfter(currentDateTime.toLocalDate())) {
                minimumTotalAmount = totalAmount;
                bestCard = new Card(card);
                bestCardsMoney = cardsMoney;
            }
            setCurrentDateTime();
        }
        if(bestCard==null)
            return null;
        return new CardTransactionDTO(bestCard,bestCardsMoney);
    }

    /**
     * Gets total amount of money needed from the others cards to complete the transaction
     *      using a given path (sequence of Atms and durations between them) and a given card
     *      for the transaction
     * It's considered that once arrived at an Atm, all the withdraws are instant
     * @param movingToCard - card we are going to complete the transaction from
     * @param bestPath - best path of Atms to follow
     * @return money amount for every other card used.
     *      This amount contains TVA and Fee for every card
     */
    private Map<Card,Double> getTotalAmountForMovingMoneyToCard(Card movingToCard, List<WalkingTime> bestPath){
        Map<Card,Double> cardsMoney = new HashMap<>();
        Double neededMoney = productValue + productValue*movingToCard.getFee() - movingToCard.getAvailableAmount();
        List<Card> sortedCards = cloneList(cardService.getSortedCards());
        sortedCards.remove(movingToCard);
        List<WalkingTime> walkingTimes = cloneList(bestPath);

        while(neededMoney>0 && !sortedCards.isEmpty() && !walkingTimes.isEmpty()){
            Atm currentAtm = walkingTimes.get(0).getId().getTo();
            walkToAtm(walkingTimes.get(0));
            Card currentCard = sortedCards.get(0);
            if(isValid(currentCard)) {
                Double currentCardAmount = manageCurrentWithdraw(currentAtm,currentCard);
                neededMoney -= currentCardAmount;
                if(cardsMoney.get(currentCard)==null)
                    cardsMoney.put(currentCard,currentCardAmount);
                else cardsMoney.put(currentCard,cardsMoney.get(currentCard)+currentCardAmount);

                if(currentCard.getAvailableAmount().equals(0.0))
                    sortedCards.remove(0);
                if(currentAtm.getMoneyAmount().equals(0.0))
                    walkingTimes.remove(0);
            }
            else sortedCards.remove(0);
        }
        if(neededMoney>0)
            return null;
        if(neededMoney<0){
            Card highestFeeCard = getHighestFeeCard(cardsMoney.keySet());
            Double highestFeeCardSum = cardsMoney.get(highestFeeCard);
            highestFeeCard.setAvailableAmount(highestFeeCard.getAvailableAmount()-neededMoney);
            cardsMoney.put(highestFeeCard,highestFeeCardSum+neededMoney);
        }
        return cardsMoney;
    }

    /**
     * Gets the maximum amount of money that the user can withdraw from an Atm,
     *      using a given card
     * @param currentAtm - the Atm used
     * @param currentCard - the card used
     * @return the amount of money user has withdrawn from the atm
     */
    private Double manageCurrentWithdraw( Atm currentAtm, Card currentCard){
        Double withdrawLimit = getWithdrawWithFee(currentCard,currentCard.getWithdrawLimit());
        Double cardAvailableAmount = currentCard.getAvailableAmount();
        Double atmAvailableAmount = currentAtm.getMoneyAmount();

        if (cardAvailableAmount > withdrawLimit) {
            if(atmAvailableAmount > currentCard.getWithdrawLimit()){
                currentAtm.setMoneyAmount(atmAvailableAmount-currentCard.getWithdrawLimit());
                currentCard.setAvailableAmount(cardAvailableAmount-withdrawLimit);
                return withdrawLimit;
            }
            else{
                Double withdrawWithFee = getWithdrawWithFee(currentCard,atmAvailableAmount);
                currentAtm.setMoneyAmount(0.0);
                currentCard.setAvailableAmount(cardAvailableAmount-withdrawWithFee);
                return withdrawWithFee;
            }
        }
        else { //cardAvailableAmount <= withdrawLimit
            Double atmAvailableWithFee = getWithdrawWithFee(currentCard,atmAvailableAmount);
            if(atmAvailableWithFee<cardAvailableAmount){
                currentAtm.setMoneyAmount(0.0);
                currentCard.setAvailableAmount(cardAvailableAmount-atmAvailableWithFee);
                return atmAvailableWithFee;
            }
            else{
                Double actualWithdraw = getWithdrawWithoutFee(currentCard,cardAvailableAmount);
                currentAtm.setMoneyAmount(atmAvailableAmount-actualWithdraw);
                currentCard.setAvailableAmount(0.0);
                return cardAvailableAmount;
            }
        }
    }

    private Card getHighestFeeCard(Set<Card> cards){
        if(cards==null || cards.isEmpty())
            return null;
        AtomicReference<Card> maximumFeeCard = new AtomicReference<>(cards.iterator().next());
        cards.forEach(x->{
            if(x.getFee()> maximumFeeCard.get().getFee())
                maximumFeeCard.set(x);
        });
        return maximumFeeCard.get();
    }

    private Double getWithdrawWithoutFee(Card card, Double sum){
        return sum/(1.0+card.getFee());
    }

    private Double getWithdrawWithFee(Card card, Double sum){
        return sum+sum*card.getFee();
    }

    private boolean isValid(Card card){
        LocalDate currentDate = LocalDate.of(currentDateTime.getYear(),
                currentDateTime.getMonth(),currentDateTime.getDayOfMonth());
        return card.getExpirationDate().isAfter(currentDate);
    }

    private void walkToAtm(WalkingTime walkingTime){
        currentDateTime = currentDateTime.plusMinutes(walkingTime.getDuration());
    }

    public <E> List<E> cloneList(List<E> list){
        List<E> clone = new ArrayList<>();
        list.forEach(x->{
            if(x==null)
                clone.add(null);
            else {
                if (x instanceof Card)
                    clone.add((E) new Card((Card) x));
                else if (x instanceof WalkingTime)
                    clone.add((E) new WalkingTime((WalkingTime) x));
                else if (x instanceof Atm)
                    clone.add((E) new Atm((Atm) x));
            }
        });
        return clone;
    }
}

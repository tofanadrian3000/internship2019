package Repository;

import Model.Card;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CardRepository extends FileRepository<String, Card> {

    public CardRepository(String filename){
        super(filename);
    }

    /**
     * Default constructor
     */
    public CardRepository(){
        this("./src/cards");
    }

    @Override
    protected Card createEntity(String line) {
        String[] data = line.split(",");
        String creditCard = data[0];
        Double fee = Double.parseDouble(data[1]);
        Double withdrawLimit = Double.parseDouble(data[2]);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate expirationDate = LocalDate.parse(data[3],formatter);

        Double availableAmount = Double.parseDouble(data[4]);

        return new Card.CardBuilder(creditCard,fee,availableAmount)
                .setWithdrawLimit(withdrawLimit)
                .setExpirationDate(expirationDate).build();
    }
}

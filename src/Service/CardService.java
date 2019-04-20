package Service;
import Model.Card;
import Repository.Repository;
import Repository.CardRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CardService {
    private Repository<String, Card> cardRepository;

    public CardService(){
        cardRepository = new CardRepository();
    }

    /**
     *
     * @return list of the cards sorted by the fee and expiration date
     */
    public List<Card> getSortedCards() {
        return cardRepository.getAll().values().stream().sorted(new Comparator<Card>() {
            @Override
            public int compare(Card o1, Card o2) {
                if (o1.getFee().equals(o2.getFee()))
                    return o1.getExpirationDate().compareTo(o2.getExpirationDate());
                return o1.getFee().compareTo(o2.getFee());
            }
        }).collect(Collectors.toList());
    }

    public Map<String,Card> getAll(){
        return cardRepository.getAll();
    }
}

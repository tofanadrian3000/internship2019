import Model.Card;
import Model.Cost;

import java.util.Map;

public interface ITransactionManager {
    public Map<Card, Cost> getCardsCost();
}

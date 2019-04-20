
public class Main {

    public static void main(String[] args) {
        ITransactionManager transactionManager = new TransactionManager();
        transactionManager.getCardsCost().forEach((k,v)-> System.out.println(k.toString()+"\t\t"+v.toString()));
    }
}

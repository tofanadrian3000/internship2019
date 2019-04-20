package Model;

public class Cost {
    private Double tva, fee;

    public Cost(Double tva, Double fee) {
        this.tva = tva;
        this.fee = fee;
    }

    public Double getTva() {
        return tva;
    }

    public void setTva(Double tva) {
        this.tva = tva;
    }

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }

    @Override
    public String toString() {
        return "Cost{" +
                "tva=" + tva +
                ", fee=" + fee +
                '}';
    }
}

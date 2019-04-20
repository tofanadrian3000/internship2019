package Model;

import java.time.LocalTime;
import java.util.Objects;

public class Atm implements Identifiable<String> {
    private String atmName;
    private LocalTime openingTime, closingTime;
    private Double moneyAmount;

    public Atm(Atm atm){
        this.atmName = atm.atmName;
        this.openingTime = atm.openingTime;
        this.closingTime = atm.closingTime;
        this.moneyAmount = atm.moneyAmount;
    }

    public Atm(String atmName, LocalTime openingTime, LocalTime closingTime) {
        this.atmName = atmName;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        moneyAmount = 5000.0; //default money amount
    }

    @Override
    public String getId() {
        return atmName;
    }

    public LocalTime getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(LocalTime openingTime) {
        this.openingTime = openingTime;
    }

    public LocalTime getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(LocalTime closingTime) {
        this.closingTime = closingTime;
    }

    public Double getMoneyAmount() {
        return moneyAmount;
    }

    public void setMoneyAmount(Double moneyAmount) {
        this.moneyAmount = moneyAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Atm atm = (Atm) o;
        return Objects.equals(atmName, atm.atmName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(atmName);
    }

    @Override
    public String toString() {
        return "Atm{" +
                "atmName='" + atmName + '\'' +
                ", openingTime=" + openingTime +
                ", closingTime=" + closingTime +
                '}';
    }
}

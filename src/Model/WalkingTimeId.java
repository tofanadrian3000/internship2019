package Model;

import java.util.Objects;

public class WalkingTimeId {
    private Atm from, to;

    public WalkingTimeId(WalkingTimeId walkingTimeId){
        if(walkingTimeId.from==null)
            this.from=null;
        else this.from = new Atm(walkingTimeId.from);
        this.to = new Atm(walkingTimeId.to);
    }

    public WalkingTimeId(Atm from, Atm to) {
        this.from = from;
        this.to = to;
    }

    public Atm getFrom() {
        return from;
    }

    public void setFrom(Atm from) {
        this.from = from;
    }

    public Atm getTo() {
        return to;
    }

    public void setTo(Atm to) {
        this.to = to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WalkingTimeId that = (WalkingTimeId) o;
        return (Objects.equals(from, that.from) &&
                Objects.equals(to, that.to));
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }

    @Override
    public String toString() {
        String out="from = ";
        if(from!=null)
            out+= from.toString();
        else out+= "User starting point";
        return out + ", to= " + to.toString();
    }
}

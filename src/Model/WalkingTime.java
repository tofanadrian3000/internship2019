package Model;

import java.util.Objects;

public class WalkingTime implements Identifiable<WalkingTimeId> {
    private WalkingTimeId id;
    private Integer duration;

    public WalkingTime(WalkingTime walkingTime){
        this.id = new WalkingTimeId(walkingTime.id);
        this.duration = walkingTime.duration;
    }

    public WalkingTime(Atm from, Atm to, Integer duration){
        id = new WalkingTimeId(from,to);
        this.duration = duration;
    }

    @Override
    public WalkingTimeId getId() {
        return id;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WalkingTime that = (WalkingTime) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id.toString() + ", duration=" + duration;
    }
}

package Service;

import Model.Atm;
import Model.WalkingTime;
import Model.WalkingTimeId;
import Repository.Repository;
import Repository.AtmRepository;
import Repository.WalkingTimeRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class AtmService {
    private Repository<String, Atm> atmRepository;
    private Repository<WalkingTimeId, WalkingTime> walkingTimeRepository;

    public AtmService() {
        atmRepository = new AtmRepository();
        WalkingTimeRepository.setAtms(atmRepository.getAll());
        walkingTimeRepository = new WalkingTimeRepository();
    }

    public AtmService(AtmRepository atmRepository, WalkingTimeRepository walkingTimeRepository) {
        this.atmRepository = atmRepository;
        this.walkingTimeRepository = walkingTimeRepository;
    }

    public Map<String, Atm> getAllAtms() {
        return atmRepository.getAll();
    }

    public Map<WalkingTimeId, WalkingTime> getAllWalkingTimes() {
        return walkingTimeRepository.getAll();
    }

    /**
     * Gets the best path overall to go through
     * @param currentDateTime - starting time
     * @return bestPath - list with all the paths that we are going to go through
     *      that contains durations from one to another and waiting times.
     * The time spent between the locations is not taken into consideration
     */
    public List<WalkingTime> getBestPath(LocalDateTime currentDateTime) {
        List<WalkingTime> bestPath = new ArrayList<>();
        Map<String, Atm> availableAtm = new HashMap<>();
        availableAtm.putAll(getAllAtms());
        LocalDateTime current = currentDateTime;
        Atm from = null; //User starting point at the beginning
        while (!availableAtm.isEmpty()) {
            WalkingTime shortestWalkingTime = getShortestWalkingTime(from, availableAtm, current);
            from = shortestWalkingTime.getId().getTo();
            bestPath.add(shortestWalkingTime);
            current = current.plusMinutes(shortestWalkingTime.getDuration());
            availableAtm.remove(from.getId());
        }
        return bestPath;
    }

    /**
     * Gets best walking time - contains the best Atm that we could go to (Greedy approach)
     * @param from - current location from where we are going to start
     * @param availableAtms - list o Atms that are available, we have to choose from these
     * @param currentDateTime - current time, when we are going to start
     * @return minimumWalkingTime - best local path to go, which has the best duration and waiting time for the Atm to be opened
     */
    private WalkingTime getShortestWalkingTime(Atm from, Map<String, Atm> availableAtms, LocalDateTime currentDateTime) {
        Map<WalkingTimeId, WalkingTime> fromWalkingTimes = getWalkingTimesFrom(from, availableAtms);
        if (fromWalkingTimes.isEmpty())
            return null;
        AtomicReference<WalkingTime> minimumWalkingTime = new AtomicReference<>(fromWalkingTimes.entrySet().iterator().next().getValue());
        fromWalkingTimes.forEach((k, v) -> {
            Integer waitingTime = getWaitingTime(v.getId().getTo(), currentDateTime.plusMinutes(v.getDuration())).intValue();
            if (waitingTime < minimumWalkingTime.get().getDuration()) {
                v.setDuration(v.getDuration() + waitingTime); //Duration to get to the ATM and the waiting time for the ATM to be opened
                minimumWalkingTime.set(v);
            }
        });
        return minimumWalkingTime.get();
    }

    /**
     * Gets waiting time from currentTime to the opening time of the Atm
     * @param atm - the Atm we are waiting to be opened
     * @param currentDateTime - arrival to the Atm time
     * @return waitingTime - number of minutes
     */
    private Long getWaitingTime(Atm atm, LocalDateTime currentDateTime) {
        Long waitingTime;
        LocalTime currentTime = LocalTime.of(currentDateTime.getHour(), currentDateTime.getMinute());

        if (atm.getOpeningTime().isAfter(atm.getClosingTime())) { //Ex: 17:00-04:00
            if (currentTime.isBefore(atm.getOpeningTime()) && currentTime.isAfter(atm.getClosingTime())) { //We've arrived before
                waitingTime = Duration.between(currentTime, atm.getOpeningTime()).toMinutes();
            } else waitingTime = 0L;
        } else { //Ex: 17:00 - 19:00
            if (currentTime.isBefore(atm.getOpeningTime()) || currentTime.isAfter(atm.getClosingTime())) {
                waitingTime = Duration.between(currentTime, atm.getOpeningTime()).toMinutes();
            } else waitingTime = 0L;
        }

        return waitingTime;
    }


    /**
     * Gets all the possible paths(WalkingTime) from a certain point (Atm)
     * @param from - the Atm from which we are looking for paths
     * @param availableAtms - all the paths have to go to one of these available Atms, not null
     * @return all the paths we are looking for
     */
    private Map<WalkingTimeId, WalkingTime> getWalkingTimesFrom(Atm from, Map<String, Atm> availableAtms) {
        return walkingTimeRepository.getAll().entrySet().stream()
                .filter(x -> {
                    if (x.getKey().getFrom() == null) {
                        if (from == null)
                            return true;
                        return false;
                    } else {
                        if (x.getKey().getFrom().equals(from) && availableAtms.containsKey(x.getKey().getTo().getId()))
                            return true;
                        return false;
                    }
                })
                .collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue()));
    }
}

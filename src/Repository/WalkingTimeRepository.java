package Repository;

import Model.Atm;
import Model.WalkingTime;
import Model.WalkingTimeId;
import java.util.Map;

public class WalkingTimeRepository extends FileRepository<WalkingTimeId, WalkingTime> {
    private static Map<String, Atm> existentAtms;

    public WalkingTimeRepository(String filename) {
        super(filename);
    }

    /**
     * Default constructor
     */
    public WalkingTimeRepository(){
        this("./src/walkingTimes");

    }

    public static void setAtms(Map<String, Atm> atms){
        existentAtms = atms;
    }

    @Override
    protected WalkingTime createEntity(String line) {
        String[] data = line.split(",");
        Atm from, to;
        if(data[0].equals("USP"))//USP=User starting point
            from=null;
        else from=existentAtms.get(data[0]);
        to = existentAtms.get(data[1]);
        Integer duration = Integer.parseInt(data[2]);
        return new WalkingTime(from,to,duration);
    }
}

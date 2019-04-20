package Repository;

import Model.Atm;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class AtmRepository extends FileRepository<String, Atm> {

    public AtmRepository(String filename) {
        super(filename);
    }

    /**
     * Default constructor
     */
    public AtmRepository(){
        this("./src/atms");
    }

    @Override
    protected Atm createEntity(String line) {
        String[] data = line.split(",");
        String atmName = data[0];

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime openingTime = LocalTime.parse(data[1],formatter);
        LocalTime closingTime = LocalTime.parse(data[2],formatter);

        return new Atm(atmName,openingTime,closingTime);
    }
}

package Repository;

import Model.Identifiable;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class FileRepository<Id, Elem extends Identifiable<Id>> implements Repository<Id,Elem> {

    private String filename;
    private Map<Id,Elem> elements;

    public FileRepository(String filename){
        this.filename = filename;
        elements = new HashMap<>();
        loadDataFromFile();
    }

    @Override
    public Map<Id,Elem> getAll(){
        return elements;
    }

    /**
     * Loading all the data from file
     */
    private void loadDataFromFile(){
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(this.filename))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if(!line.isEmpty()) {
                    Elem elem = createEntity(line);
                    elements.put(elem.getId(),elem);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Used to create an element from a line of the file
     * @param line
     * @return the element created
     */
    protected abstract Elem createEntity(String line);
}

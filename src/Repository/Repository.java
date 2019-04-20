package Repository;

import Model.Identifiable;

import java.util.Map;

public interface Repository<Id, Elem extends Identifiable<Id>> {
    Map<Id,Elem> getAll();
}

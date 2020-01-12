package depindr.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractRegistry<E extends Entity<ID>, ID> {
    private Map<ID, E> map = new HashMap<>();

    public Optional<E> getById(ID id) {
        return Optional.ofNullable(map.get(id));
    }

    public E add(E entity) {
        if (map.containsKey(entity.getID()))
            return map.get(entity.getID());
        map.put(entity.getID(), entity);
        return entity;
    }
}

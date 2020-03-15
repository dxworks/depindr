package depindr.model;

import java.util.*;

public abstract class AbstractRegistry<E extends Entity<ID>, ID> {
    protected Map<ID, E> map = new HashMap<>();

    public Optional<E> getById(ID id) {
        return Optional.ofNullable(map.get(id));
    }

    public E add(E entity) {
        if (map.containsKey(entity.getID()))
            return map.get(entity.getID());
        map.put(entity.getID(), entity);
        return entity;
    }

    public void addAll(List<E> entities) {
        entities.forEach(this::add);
    }

    public Collection<E> getAll() {
        return map.values();
    }
}

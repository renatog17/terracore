package com.nhs.ecs;

import java.util.*;

public class EntityManager {

    private int nextId = 0;

    private final Map<Integer, Map<Class<?>, Component>> entities = new HashMap<>();

    public Entity createEntity() {
        int id = nextId++;
        entities.put(id, new HashMap<>());
        return new Entity(id);
    }

    public <T extends Component> void addComponent(Entity e, T component) {
        entities.get(e.id).put(component.getClass(), component);
    }

    // ===== ACESSO POR ENTITY =====
    public <T extends Component> T getComponent(Entity e, Class<T> type) {
        return getComponent(e.id, type);
    }

    // ===== ACESSO POR ID (RECOMENDADO) =====
    public <T extends Component> T getComponent(int id, Class<T> type) {
        Map<Class<?>, Component> comps = entities.get(id);
        if (comps == null) return null;

        Component c = comps.get(type);
        if (c == null) return null;

        return type.cast(c);
    }

    public boolean hasComponent(int id, Class<? extends Component> type) {
        Map<Class<?>, Component> comps = entities.get(id);
        return comps != null && comps.containsKey(type);
    }

    public Collection<Integer> getAllEntities() {
        return entities.keySet();
    }

    // ===== OPCIONAL (ÚTIL PARA LIMPEZA FUTURA) =====
    public void removeEntity(int id) {
        entities.remove(id);
    }

    public <T extends Component> void removeComponent(int id, Class<T> type) {
        Map<Class<?>, Component> comps = entities.get(id);
        if (comps != null) {
            comps.remove(type);
        }
    }
}

package engine;


import java.util.*;
import java.util.stream.Stream;

import com.google.gson.Gson;
import engine.graphics.view_.View;
import game.GameUtils;

/**
 * Created by eirik on 13.06.2017.
 *
 * Contains all entities and components
 */
public class WorldContainer {

    private static int ENTITY_COUNT = 400;



    //an overview of entity id's in use
    private boolean[] entities = new boolean[ENTITY_COUNT];

    //A mapping between entities and components for each component type.
    //A TreeMap is used to keep the map sorted on its keyValues.
    private Map< Class<? extends Component >, TreeMap<Integer, Component>> activeComponents = new HashMap<>();
    private Map< Class<? extends Component >, TreeMap<Integer, Component>> inactiveComponents = new HashMap<>();

    //a maping from entities to its components
    private Map<Integer, ArrayList< Class<? extends Component> >> entityComponents = new HashMap<>();
    //a mapping from entities to their names
    private Map< Integer, String > entityNames = new HashMap<>();


    private List<Sys> systems = new ArrayList<>();

    private View view; //a view_ into the world



    public WorldContainer(View view) {
        this.view = view;

    }
    @Deprecated
    public WorldContainer(float viewWidth, float viewHeight) {
        this(new View(viewWidth, viewHeight));
    }

    /**
     * init a Container with no view_. Render systems then uses the default view_ wich is equal to screen size
     */
    @Deprecated
    public WorldContainer() {
        view = new View(GameUtils.VIEW_WIDTH, GameUtils.VIEW_HEIGHT);
    }

    //---------VIEW
    public View getView() {
        return view;
    }

    //---------SETUP

    //assign component types to be used during execution
    public void assignComponentType(Class<? extends Component> compType) {
        activeComponents.put(compType, new TreeMap<>());
        inactiveComponents.put(compType, new TreeMap<>());

    }

    //add system instances to be updated/run on each update frame
    public void addSystem(Sys system) {
        system.setWorldContainer(this);
        systems.add(system);
    }


    //----------EXECUTION

    public void updateSystems() {
        for (Sys s : systems) {
            s.update();
        }
    }


    //----------ENTITY HANDLING

    public String getEntityName(int entity) {
        return entityNames.get(entity);
    }
    public void setEntityName(int entity, String name) {
        entityNames.put(entity, name);
    }

    public int createEntity(String name) {
        //retrieve unique id
        int e = retrieveEntityId();

        //create entry in entity-compType map
        entityComponents.put(e, new ArrayList<>());

        //create entry in name map
        setEntityName(e, name);

        return e;
    }
    public int createEntity() {
        return createEntity("");
    }

    /**
     * Use with caution. Better to deactivate than to destroy if a similar entity is used again
     * @param entity
     */
    public void destroyEntity(int entity) {
        if (! entityExists(entity)) throw new IllegalArgumentException("Trying to destroy an entity that doesnt exist");

        //remove components
        ArrayList<Class<? extends Component>> compTypes = entityComponents.get(entity);
        while( !compTypes.isEmpty() ) {
            removeComponent(entity, compTypes.get(0));
        }

        //remove entry in entity-compType map
        entityComponents.remove(entity);

        //release entity id
        releaseEntityId(entity);
    }

    /**
     * Deactivate all components corresponding to the given entity
     * @param entity
     */
    public void deactivateEntity(int entity) {
        //deactivation is done on component level
        for (Class<? extends  Component> compType : entityComponents.get(entity)) {
            deactivateComponent(entity, compType);
        }
    }

    /**
     * Activates all components corresponding to the given entity. Assumes all components inactive
     * @param entity
     */
    public void activateEntity(int entity) {
        //activation is done on component level
        for (Class<? extends  Component> compType : entityComponents.get(entity)) {
            activateComponent(entity, compType);
        }
    }


    /**
     * Retrieve the entities that contains a given component  in ascending order based on the entities id.
     * That is, the set's values are ordered
     * @param compType
     * @return
     */
    public Set<Integer> getEntitiesWithComponentType(Class<? extends Component> compType) {
        return activeComponents.containsKey(compType)
                ? activeComponents.get(compType).keySet()
                : new HashSet<>(0);
    }
    public Stream<Integer> entitiesOfComponentTypeStream(Class<? extends Component> compType) {
        return getEntitiesWithComponentType(compType).stream();
    }

    private int retrieveEntityId() {
        for (int i = 0; i < ENTITY_COUNT; i++) {
            if (!entities[i]) {
                entities[i] = true;
                return i;
            }
        }
        throw new IllegalStateException("There is not allocated enough space for more entities");
    }
    private void releaseEntityId(int entity) {
        //release id
        entities[entity] = false;

    }
    private boolean entityExists(int entity) {
        return entities[entity];
    }



    //----------COMPONENT HANDLING

    /**
     * Add a component, it will be active even though deactivate entity is previously called
     * @param entity
     * @param comp
     */
    public void addComponent(int entity, Component comp) {
        validateComponentType(comp); //check if component type is assigned

        activeComponents.get(comp.getClass()).put(entity, comp);

        //update entityComponent with compType
        entityComponents.get(entity).add(comp.getClass());
    }
    public void addInactiveComponent(int entity, Component comp) {
        addComponent(entity, comp);
        deactivateComponent(entity, comp.getClass());

    }

    /**
     * Deactivates a component if it is active. If it is inactive, nothing is changed.
     * @param entity
     * @param compType
     * @return true if a component was set active
     */
    public boolean deactivateComponent(int entity, Class<? extends Component> compType) {
        //swap a comp instance from active to inactive list
        Component c = activeComponents.get(compType).remove(entity);
        if (c != null) {
            inactiveComponents.get(compType).put(entity, c);
            return true;
        }
        else {
            //System.err.println("CompType "+compType+" is already inactive");
            return false;
        }
    }

    /**
     * Activates a component if it is inactive. If it is active, change nothing
     * @param entity
     * @param compType
     * @return
     */
    public boolean activateComponent(int entity, Class<? extends Component> compType) {
        //swap a comp instance from inactive to active list
        Component c = inactiveComponents.get(compType).remove(entity);

        if (c != null) {
            activeComponents.get(compType).put(entity, c);
            return true;
        }
        else {
            //System.err.println("CompType "+compType+" is already active");
            return false;
        }
    }

    /**
     * Removes an active or inactive component corresponding to the given entity
     * Use with caution, better to deactivate component if it is to be used again
     * @param entity
     * @param compType
     */
    public void removeComponent(int entity, Class<? extends Component> compType) {
        //remove if in active list
        Component c = activeComponents.get(compType).remove(entity);
        if (c == null) {
            //remove if in inactive list
            c = inactiveComponents.get(compType).remove(entity);

            if (c == null) {
                //the component doesnt exist
                throw new IllegalStateException("Trying to remove a component that doesn't exist");
            }
        }

        //remove component from entity in entityComponent list
        boolean removed = entityComponents.get(entity).remove(compType);

        if (!removed) throw new IllegalStateException("Tried to remove compType from entitiesComponent list, but it didnt exist");
    }
    /**
     * Use with caution, better to deactivate component if it is to be used again
     * @param entity
     * @param comp
     */
    public void removeComponent(int entity, Component comp) {
        removeComponent(entity, comp.getClass());
    }
    public void removeComponents(int entity, Class<? extends Component>... compTypes) {
        for (Class<? extends Component> compType : compTypes) {
            removeComponent(entity, compType);
        }
    }

    public Map<Integer, Component> getComponentsOfType(Class<? extends Component> compType) {
        //validateComponentType(compType);
        return activeComponents.containsKey(compType)
                ? activeComponents.get(compType)
                : new HashMap<>();
    }
    public Map<Integer, Component> getInactiveComponentsOfType(Class<? extends Component> compType) {
        validateComponentType(compType);

        return inactiveComponents.get(compType);
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> T getComponent(int entity, Class<T> compType) {
        Component c = getComponentsOfType(compType).get(entity);
        if (c == null) throw new IllegalStateException("No component of the given type is assigned to the given entity,\ncompType="+compType+ " entity=" + entityNames.get(entity) +" entity number=" + entity);
        return (T)c;
    }
//    public <T extends Component> T getComponent(int entity, Class<? extends Component> compType, boolean b) {
//        return (T) getComponent(entity, compType);
//    }
    public Component getInactiveComponent(int entity, Class<? extends Component> compType) {
        Component c = getInactiveComponentsOfType(compType).get(entity);
        if (c == null) throw new IllegalStateException("No component of the given type is assigned to the given entity, type="+compType);
        return c;
    }
    public boolean hasComponent(int entity, Class<? extends Component> compType) {
        return getComponentsOfType(compType).containsKey(entity);
   }

    private void validateComponentType(Component comp) {
        validateComponentType(comp.getClass());
    }
    private void validateComponentType(Class<? extends Component> compType) {
        if (!activeComponents.containsKey(compType)) throw new IllegalStateException("Trying to use a component of a type that is not assigned, type="+compType);
    }


    //---------TERMINATION
    public void terminate() {
        for (Sys s : systems) {
            s.terminate();
        }
    }


    //--------STUFF TO STRING + PRINTS

    public String systemsToString() {
        String s =
                "/////////////////\n"+
                "//// Systems ////\n";
        for (Sys sys : systems) {
            s += sys.getClass().getSimpleName() + "\n";
        }
        return s;
    }

    public String componentToString(int entity, Class<? extends Component> compType) {
        String compTypeName = compType.getSimpleName();

        //Get comp by reflection:
        Gson defaultGson = new Gson();

        if (hasComponent(entity, compType)) {
            //If comp is active
            String compStr = defaultGson.toJson(getComponent(entity, compType));
            return "\t\t" + String.format("%-30s",compTypeName) + "instance: " + compStr + "\n";
        }
        else {
            //if com is inactive
            String inactiveCompString = defaultGson.toJson(getInactiveComponent(entity, compType));
            return "\t\t/inactive/ " + String.format("%-30s",compTypeName) + "instance: " + inactiveCompString + "\n";
        }
    }
    public String entityToString(int entity) {
        String entityName = getEntityName(entity);
        String firstLineStr = "[ "+entity + "   " + (entityName.isEmpty()? "___": entityName)+" entity" + "]\n";
        String activeCompsStr = "";
        String inactiveCompsStr = "";

        for (Class<? extends Component> compType : entityComponents.get(entity)) {
            if (hasComponent(entity, compType)) {
                activeCompsStr += componentToString(entity, compType);
            }
            else {
                inactiveCompsStr += componentToString(entity, compType);
            }
        }
        return firstLineStr + activeCompsStr + inactiveCompsStr;
    }

    public String entitiesToString() {
        String s =
                "//////////////////\n"+
                "//// Entities ////\n";
        for (int entity : entityComponents.keySet()) {
            s += entityToString(entity) + "\n";
        }
        return s;
    }

    public void printEntities() {
        System.out.println(entitiesToString());
    }

    @Override
    public String toString() {
        String s = "/////////////////////////////////////////////////\n"+
                "////////// The mighty World Container! //////////\n"+
                "\nAssigned components:\n"+
                Stream.concat(activeComponents.keySet().stream(), inactiveComponents.keySet().stream())
                        .distinct()
                    .map(Class::getSimpleName)
                    .reduce("", (res, el) -> res + el+ "\n") +
                "\n"+
                systemsToString()+
                "\n"+
                entitiesToString();
        return s;
    }
}


//
//    public int createEntity() {
//        int e = allocateEntity();
//        resetEntityMask(e); // should not be needed
//        initAllocatedEntity(e);
//
//        return e;
//    }
//    public void destroyEntity(int entity) {
//        if (! entityExists(entity)) throw new IllegalArgumentException("Trying to destroy an entity that doesnt exist");
//
//        resetEntityMask(entity);
//    }
//
//
//    private int allocateEntity() {
//        for (int i = 0; i < ENTITY_COUNT; i++) {
//            if (!entityExists(i)) {
//                return i;
//            }
//        }
//        throw new IllegalStateException("There is not allocated enough space for more entities");
//    }
//    private void initAllocatedEntity(int entity) {
//        addEntityMask(entity, COMPMASK_ENTITY_EXISTS); //have to make sure that hasComponent operates on mask-level
//    }
//    public boolean entityExists(int entity) {
//        if (getEntityMask(entity) == 0) {
//            return true;
//        }
//        //debug test
//        else if (! hasComponent(entity, COMPMASK_ENTITY_EXISTS)) {
//            throw new IllegalStateException("An entity has a nonzero mask, but no exist component");
//        }
//
//        return false;
//    }
//    private int getEntityMask(int entity) {
//        return entityMask[entity];
//    }
//    private void addEntityMask(int entity, int mask) {
//        entityMask[entity] = entityMask[entity] | mask;
//    }
//    private void removeEntityMask(int entity, int mask) {
//        entityMask[entity] = entityMask[entity] & ~mask;
//    }
//
//
//    /**
//     * Update entity mask and components map
//     * If no mapping is assigned for given compmask, create one
//     * @param entity
//     * @param comp
//     */
//
//    public void addComponent(int entity, Component comp) {
//        int compmask = comp.getMask();
//
//        addEntityMask(entity, compmask);
//        if (!components.containsKey(compmask)) {
//            components.put(compmask, new HashMap<Integer, Component>() );
//        }
//        components.get(compmask).put(entity, comp);
//    }
//
//    /**
//     * Update entity mask and components map
//     * @param entity
//     * @param compmask
//     */
//    public void removeComponent(int entity, int compmask) {
//        removeEntityMask(entity, compmask);
//        components.get( compmask ).remove( entity );
//        //Maybe remove mapping if no entities are left
//    }
//
////    public boolean hasComponent(int compmask, int entity) {
////        if (components.containsKey(component)) { //if there is at least one entity with this component
////            if (components.get(component).containsKey(entity)) {
////                return true;
////            }
////        }
////        return false;
////    }
//    private boolean hasComponent(int entity, int compmask) {
//        return (entityMask[entity] & compmask) == compmask;
//    }
//
//    public Component getComponent(int compmask, int entity) {
//        return components.get(entity).get(compmask);
//    }
//
//    public Map<Integer, Component> getComponents(int compmask) {
//        return components.get(compmask);
//    }
//
//
//
//
//
//
//
//    private void resetEntityMask(int entity) { //cleans entity masks ++
//        entityMask[entity] = 0;
//    }
//}
//

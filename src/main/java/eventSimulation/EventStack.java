package eventSimulation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class EventStack {
    ArrayList<Event> events;

    public EventStack() {
        events = new ArrayList<>();
    }

    public void addEvent(Event event) {
        events.add(event);
        events.sort(Comparator.comparingDouble(Event::getExecTime));
        /*if (event.getEventType() == EventType.ARRIVAL) {
            EventSimulation.incNumberOfEvents();
        }*/
    }

    public void removeEvent(Event event) {
        //int id = -1;
        int size = events.size();
        if (events.isEmpty()) {
            System.out.println("ERROR: Cannot remove event from empty EventStack!");
        } else {
            /*int id = events.indexOf(event);
            if (id < 0) { // returned by indexOf in case event is not found
                System.out.println("ERROR: Event " + event.getIndex() + " to be removed was not found in EventStack!");
            } else {
                removeEvent(id); // is index correct?
                //events.remove(event);
            }*/
            events.remove(event);
        }
        size -= events.size();
        if (size == 0) {
            System.out.println("ERROR: Event to be removed was not removed from EventStack!");
        }
    }
    public void removeFirstEvent() {
        int size = events.size();
        events.remove(0);
        size -= events.size();
        if (size == 0) {
            System.out.println("ERROR: First Event was not removed from EventStack!");
        }
    }
    public void removeEvent(int index) {
        int size = events.size();
        events.remove(index);
        size -= events.size();
        if (size == 0) {
            System.out.println("ERROR: Event with index " + index + " was not removed from EventStack!");
        }
    }

    public void removeEvent(double execTime) {
        int size = events.size();
        int id = 0;
        Event event;
        Iterator<Event> iterator = events.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getExecTime() == execTime) {
                iterator.remove();
            }
        }
        size -= events.size();
        if (size == 0) {
            System.out.println("ERROR: Event to be removed was not removed from EventStack!");
        } else if (size > 1) {
            System.out.println("ERROR: " + size + " Events were removed -- only one should be removed!");
        }
        //System.out.println("ERROR: Event to be removed was not found in EventStack!");
    }

    public Event getNextEvent() {
        if (events.isEmpty()) {
            return null;
        }
        events.sort(Comparator.comparingDouble(Event::getExecTime));
        return events.get(0);
    }

    public boolean isEmpty() {
        return events.isEmpty();
    }

    public int getEventID(Event event) {
        return events.indexOf(event);
    }
    public List<Event> getEvents() {
        return events;
    }

    public void clear() {
        events.clear();
    }
}
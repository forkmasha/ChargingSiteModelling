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
    }

    public void removeEvent(Event event) {
        int size = events.size();
        if (events.isEmpty()) {
            System.out.println("ERROR: Cannot remove event from empty EventStack!");
        } else {
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
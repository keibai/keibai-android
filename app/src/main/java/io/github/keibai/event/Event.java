package io.github.keibai.event;

/**
 * Event class.
 */

public class Event {

    private long id;
    private String name;
    private String location;

    public Event(long id, String name, String location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }
}

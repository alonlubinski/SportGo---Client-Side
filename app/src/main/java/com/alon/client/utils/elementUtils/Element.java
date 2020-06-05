package com.alon.client.utils.elementUtils;


import com.alon.client.utils.LocationUtil;

import java.util.HashMap;
import java.util.Map;

public class Element {

    private String id,name, type;
    private Boolean active;
    private LocationUtil location;
    private Map<String, Object> elementAttributes;

    // Constructors.
    public Element() {
        elementAttributes = new HashMap<>();
    }

    public Element(String name, String type, Boolean active, LocationUtil location, Map<String, Object> elementAttributes) {
        this.name = name;
        this.type = type;
        this.active = active;
        this.location = location;
        this.elementAttributes = elementAttributes;
    }

    // Getters and setters.
    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocationUtil getLocationUtil() {
        return location;
    }

    public void setLocationUtil(LocationUtil location) {
        this.location = location;
    }

    public Map<String, Object> getElementAttributes() {
        return elementAttributes;
    }

    public void setElementAttributes(Map<String, Object> elementAttributes) {
        this.elementAttributes = elementAttributes;
    }
}

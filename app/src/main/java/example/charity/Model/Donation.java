package example.charity.Model;

import java.lang.reflect.Array;

import example.charity.R;

public class Donation {
    String id,doner,charity,type,city,name,state,description;

    public Donation() {
    }

    public Donation(String id, String doner, String charity, String type, String city, String name, String state,String description) {
        this.id = id;
        this.doner = doner;
        this.charity = charity;
        this.type = type;
        this.city = city;
        this.name = name;
        this.state = state;
        this.description=description;
    }

    public String getState() {
        return state;
    }

    public String getId() {
        return id;
    }

    public String getDoner() {
        return doner;
    }

    public String getCharity() {
        return charity;
    }

    public String getType() {
        return type;
    }

    public String getCity() {
        return city;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setCharity(String charity) {
        this.charity = charity;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

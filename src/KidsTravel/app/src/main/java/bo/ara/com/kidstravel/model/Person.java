package bo.ara.com.kidstravel.model;

import java.util.List;

/**
 * Created by LENOVO on 7/9/2016.
 */
public class Person {
    private String _id;
    private String firstName;
    private String lastName;
    private int CI;
    private String personType;
    private User user;
    private Person partner;
    private List<Person> minors;

    public Person() {
        firstName = "";
        lastName = "";
        CI = 0;
        personType = "";
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getCI() {
        return CI;
    }

    public void setCI(int CI) {
        this.CI = CI;
    }

    public String getPersonType() {
        return personType;
    }

    public void setPersonType(String personType) {
        this.personType = personType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Person getPartner() {
        return partner;
    }

    public void setPartner(Person partner) {
        this.partner = partner;
    }

    public List<Person> getMinors() {
        return minors;
    }

    public void setMinors(List<Person> minors) {
        this.minors = minors;
    }

    public String getFullName(){
        return String.format("%s %s", firstName, lastName);
    }
}

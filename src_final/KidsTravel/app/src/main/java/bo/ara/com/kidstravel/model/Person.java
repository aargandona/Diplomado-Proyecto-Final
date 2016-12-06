package bo.ara.com.kidstravel.model;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LENOVO on 7/9/2016.
 */
public class Person extends SugarRecord {
    private String _id;
    private String firstName;
    private String lastName;
    private int CI;
    private String personType;
    @Ignore
    private User user;
    @Ignore
    private Person partner;
    @Ignore
    private List<Person> minors;

    //<< Reserved for local DB - Persintence
    private long userId;
    private long partnerId;
    //>>

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

    //<< Reserved for local DB - Persintence
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(long partnerId) {
        this.partnerId = partnerId;
    }
    //>>

    //Getting models from DB
    public void fillRelationshipData(){
        getUserDB();
        getPartnerDB();
        getMinorsDB();
    }

    public User getUserDB()
    {
        user = User.findById(User.class, userId);
        return user;
    }

    public Person getPartnerDB(){
        partner = Person.findById(Person.class, partnerId);
        return partner;
    }

    public List<Person> getMinorsDB(){
        List<PersonMinor> person_minors = PersonMinor.find(PersonMinor.class, "person_id = ?", getId().toString());

        minors = new ArrayList<>();
        for(PersonMinor personMinor: person_minors) {
            Person minor = Person.findById(Person.class, personMinor.getMinorId());
            minors.add(minor);
        }

        return minors;
    }
    //>>

    //Persist Person into DB
    public void savePerson(){

        //Save Person
        Person currentPerson = null;
        currentPerson = this.findPersonById();
        long person_id;
        if(currentPerson == null) {
            person_id = this.save();
            setId(person_id);
        }
        else{
            person_id = currentPerson.getId();
        }

        //Save User
        User mUser = user.findUserById();
        if (mUser == null) {
            userId = user.save();
            user.setPersonId(person_id);
            user.save();
        }
        else
            userId = mUser.getId();

        //Save Partner
        Person mPerson = null;
        if(partner != null) {
            mPerson = partner.findPersonById();
            if (mPerson == null) {
                partnerId = partner.save();
                partner.setId(partnerId);
                partner.setPartnerId(person_id);
                partner.save();
            } else
                partnerId = mPerson.getId();
        }

        //Update Person
        if(currentPerson == null)
            this.save();
        else {
            currentPerson.setUserId(userId);
            currentPerson.setPartnerId(partnerId);
            currentPerson.save();
        }
        //>>

        //Save Minors
        long minor_id;
        for (Person minor : minors) {
            mPerson = minor.findPersonById();
            if (mPerson == null)
                minor_id = minor.save();
            else
                minor_id = mPerson.getId();

            PersonMinor personMinor = findPersonMinorById(person_id, minor_id);
            if (personMinor == null) {
                personMinor = new PersonMinor();
                personMinor.setPersonId(person_id);
                personMinor.setMinorId(minor_id);
                personMinor.save();
            }
        }
    }
    //>>

    public Person findPersonById(){
        Person person = null;
        List<Person> persons = Person.find(Person.class, "_id = ?", get_id());
        if(persons.size() > 0)
            person = persons.get(0);

        return person;
    }

    public PersonMinor findPersonMinorById(long person_id, long minor_id){
        PersonMinor personMinor = null;
        List<PersonMinor> personMinors = PersonMinor.find(PersonMinor.class, "PERSON_ID = ? and MINOR_ID = ?", String.valueOf(person_id), String.valueOf(minor_id));
        if(personMinors.size() > 0)
            personMinor = personMinors.get(0);

        return personMinor;
    }
}

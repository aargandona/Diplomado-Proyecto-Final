package bo.ara.com.kidstravel.model;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.util.List;

/**
 * Created by LENOVO on 7/9/2016.
 */
public class User extends SugarRecord{
    private String _id;
    private String username;
    private String password;
    private int userLevel;
    private String imageUrl;
    private String token;
    @Ignore
    private Person person;

    //<< Reserved for local DB - Persintence
    private long personId;
    //>>

    public User() {
        username = "";
        password = "";
        userLevel = 2;
        imageUrl = "";
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(int userLevel) {
        this.userLevel = userLevel;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    //<< Reserved for local DB - Persintence
    public long getPersonId() {
        return personId;
    }

    public void setPersonId(long personId) {
        this.personId = personId;
    }
    //>>

    //Getting models from DB
    public void fillRelationshipData(){
        getPersonDB();
    }

    public Person getPersonDB(){
        person = Person.findById(Person.class, personId);
        return person;
    }
    //>>

    //Persist User into DB
    public void saveUser(){
        User currentUser = null;
        currentUser = this.findUserById();
        if(currentUser == null) {
            long user_id = this.save();
            setId(user_id);

            //Save Person
            Person mPerson = person.findPersonById();
            if (mPerson == null) {
                personId = person.save();
                person.setUserId(user_id);
                person.save();
            }
            else{
                personId = mPerson.getId();
            }

            //Update User
            this.save();
        }
    }

    public User findUserById(){
        User user = null;
        List<User> users = User.find(User.class, "_id = ?", get_id());
        if(users.size() > 0)
            user = users.get(0);

        return user;
    }

}
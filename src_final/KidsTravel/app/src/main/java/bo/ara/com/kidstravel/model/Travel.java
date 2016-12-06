package bo.ara.com.kidstravel.model;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LENOVO on 8/6/2016.
 */
public class Travel extends SugarRecord {
    private String _id;
    private String travelRoute;
    private String startDate;
    private String endDate;
    private String status;
    private String creationDate;
    private String updateDate;
    @Ignore
    private User user;
    @Ignore
    private Person applicant;
    @Ignore
    private Person authorizer;
    @Ignore
    private List<Person> minors;

    //<< Reserved for local DB - Persintence
    private long userId;
    private long applicantId;
    private long authorizerId;
    //>>

    public Travel() {
        travelRoute = "";
        startDate = "";
        endDate = "";
        status = "";
        creationDate = "";
        updateDate = "";
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getTravelRoute() {
        return travelRoute;
    }

    public void setTravelRoute(String travelRoute) {
        this.travelRoute = travelRoute;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public Person getApplicant() {
        return applicant;
    }

    public void setApplicant(Person applicant) {
        this.applicant = applicant;
    }

    public Person getAuthorizer() {
        return authorizer;
    }

    public void setAuthorizer(Person authorizer) {
        this.authorizer = authorizer;
    }

    public List<Person> getMinors() {
        return minors;
    }

    public void setMinors(List<Person> minors) {
        this.minors = minors;
    }

    //<< Reserved for local DB - Persintence
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(long applicantId) {
        this.applicantId = applicantId;
    }

    public long getAuthorizerId() {
        return authorizerId;
    }

    public void setAuthorizerId(long authorizerId) {
        this.authorizerId = authorizerId;
    }
    //>>

    //Getting models from DB
    public void fillRelationshipData(){
        getUserDB();
        getApplicantDB();
        getAuthorizerDB();
        getMinorsDB();
    }

    public User getUserDB()
    {
        user = User.findById(User.class, userId);
        return user;
    }

    public Person getApplicantDB(){
        applicant = Person.findById(Person.class, applicantId);
        return applicant;
    }

    public Person getAuthorizerDB(){
        authorizer = Person.findById(Person.class, authorizerId);
        return authorizer;
    }

    public List<Person> getMinorsDB(){
        List<TravelMinor> travel_minors = TravelMinor.find(TravelMinor.class, "travel_id = ?", getId().toString());

        minors = new ArrayList<>();
        for(TravelMinor travelMinor: travel_minors) {
            Person minor = Person.findById(Person.class, travelMinor.getMinorId());
            minors.add(minor);
        }

        return minors;
    }
    //>>

    //Persist Travel into DB
    public void saveTravel(){
        //Save Travel
        Travel currentTravel = null;
        currentTravel = this.findTravelById();
        if(currentTravel == null) {
            long travel_id = this.save();
            setId(travel_id);

            //Save User
            User mUser = user.findUserById();
            if (mUser == null)
                userId = user.save();
            else
                userId = mUser.getId();

            //Save Applicant
            Person mPerson = applicant.findPersonById();
            if (mPerson == null)
                applicantId = applicant.save();
            else
                applicantId = mPerson.getId();

            //Save Authorizer
            mPerson = authorizer.findPersonById();
            if (mPerson == null)
                authorizerId = authorizer.save();
            else
                authorizerId = mPerson.getId();

            //Update Travel
            this.save();

            //Save Travel Minors
            long minor_id;
            for (Person minor : minors) {
                mPerson = minor.findPersonById();
                if (mPerson == null)
                    minor_id = minor.save();
                else
                    minor_id = mPerson.getId();

                TravelMinor travelMinor = findTravelMinorById(getId(), minor_id);
                if (travelMinor == null) {
                    travelMinor = new TravelMinor();
                    travelMinor.setTravelId(getId());
                    travelMinor.setMinorId(minor_id);
                    travelMinor.save();
                }
            }
        }
        else{
            //Update Travel
            currentTravel.save();
        }
    }
    //>>

    public Travel findTravelById(){
        Travel travel = null;
        List<Travel> travels = Travel.find(Travel.class, "_id = ?", get_id());
        if(travels.size() > 0)
            travel = travels.get(0);

        return travel;
    }

    public TravelMinor findTravelMinorById(long travel_id, long minor_id){
        TravelMinor travelMinor = null;
        List<TravelMinor> travelMinors = TravelMinor.find(TravelMinor.class, "TRAVEL_ID = ? and MINOR_ID = ?", String.valueOf(travel_id), String.valueOf(minor_id));
        if(travelMinors.size() > 0)
            travelMinor = travelMinors.get(0);

        return travelMinor;
    }
}

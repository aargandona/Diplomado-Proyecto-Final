package bo.ara.com.kidstravel.model;

import java.util.List;

/**
 * Created by LENOVO on 8/6/2016.
 */
public class Travel {
    private String _id;
    private String travelRoute;
    private String startDate;
    private String endDate;
    private User user;
    private String status;
    private String creationDate;
    private String updateDate;
    private Person applicant;
    private Person authorizer;
    private List<Person> minors;

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
}

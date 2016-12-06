package bo.ara.com.kidstravel.model;

import com.orm.SugarRecord;

/**
 * Created by LENOVO on 10/13/2016.
 */
public class PersonMinor extends SugarRecord {
    private long personId;
    private long minorId;

    public PersonMinor() {
    }

    public long getPersonId() {
        return personId;
    }

    public void setPersonId(long personId) {
        this.personId = personId;
    }

    public long getMinorId() {
        return minorId;
    }

    public void setMinorId(long minorId) {
        this.minorId = minorId;
    }
}

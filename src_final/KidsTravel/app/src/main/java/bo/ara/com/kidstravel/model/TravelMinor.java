package bo.ara.com.kidstravel.model;

import com.orm.SugarRecord;

/**
 * Created by LENOVO on 10/13/2016.
 */
public class TravelMinor extends SugarRecord {
    private long travelId;
    private long minorId;

    public TravelMinor() {
    }

    public long getTravelId() {
        return travelId;
    }

    public void setTravelId(long travelId) {
        this.travelId = travelId;
    }

    public long getMinorId() {
        return minorId;
    }

    public void setMinorId(long minorId) {
        this.minorId = minorId;
    }
}

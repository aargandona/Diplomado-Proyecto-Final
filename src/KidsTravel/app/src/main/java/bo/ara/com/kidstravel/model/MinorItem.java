package bo.ara.com.kidstravel.model;

/**
 * Created by LENOVO on 8/23/2016.
 */
public class MinorItem {
    private String _id;
    private String minorFullName;
    private Boolean minorSelected;
    private Boolean enabled;

    public MinorItem(String _id, String minorFullName, Boolean minorSelected, Boolean enabled) {
        this._id = _id;
        this.minorFullName = minorFullName;
        this.minorSelected = minorSelected;
        this.enabled = enabled;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getMinorFullName() {
        return minorFullName;
    }

    public void setMinorFullName(String minorFullName) {
        this.minorFullName = minorFullName;
    }

    public Boolean getMinorSelected() {
        return minorSelected;
    }

    public void setMinorSelected(Boolean minorSelected) {
        this.minorSelected = minorSelected;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}

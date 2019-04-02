
package io.ideaction.stori.db;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.RealmObject;

public class CreatedAt extends RealmObject {

    @SerializedName("date")
    @Expose
    private Date date;
    @SerializedName("timezone_time")
    @Expose
    private Integer timezoneTime;
    @SerializedName("timezone")
    @Expose
    private String timezone;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getTimezoneTime() {
        return timezoneTime;
    }

    public void setTimezoneTime(Integer timezoneTime) {
        this.timezoneTime = timezoneTime;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

}

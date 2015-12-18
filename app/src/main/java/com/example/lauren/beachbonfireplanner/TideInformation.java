package com.example.lauren.beachbonfireplanner;

/**
 * Created by Lauren on 11/4/2015.
 */
public class TideInformation {
    private String mHighTideTime;
    private String mLowTideTime;
    private String mSunsetTime;
    private String mDataType;
    private String mGoodOrBad;
    private String mHighTideHour;
    private String mSunsetHour;
    private String mLowTideHour;

    public int getSunsetHourInt() {
        int sunset = Integer.parseInt(getSunsetHour());
        return sunset;
    }

    public int getLowTideHourInt() {
        int lowTide = Integer.parseInt(getLowTideHour());
        return lowTide;
    }

    public int getHighTideHourInt(){
        int highTide = Integer.parseInt(getHighTideHour());
        return highTide;
    }

    public String getVerdict(){
        int highTide = getHighTideHourInt();
        int sunset = getSunsetHourInt();
        int lowTide = getLowTideHourInt();
        String verdict;

        if ((sunset-lowTide <= 3 && sunset - lowTide >= 0) || (lowTide - sunset <=3 && lowTide - sunset >= 0)){
            verdict = "Yes, tonight is a good night for a beach bonfire. The tide will be out near sunset.";
        }
        else {
            verdict = "No, tonight is not a good night for a beach bonfire. The tide will be in near sunset.";
        }
        return verdict;
    }
    public String getDataType() {
        return mDataType;
    }

    public void setDataType(String dataType) {
        mDataType = dataType;
    }

    public String getHighTideTime() {
        return mHighTideTime;
    }

    public void setHighTideTime(String highTideTime) {
        mHighTideTime = highTideTime;
    }

    public String getLowTideTime() {
        return mLowTideTime;
    }

    public void setLowTideTime(String lowTideTime) {
        mLowTideTime = lowTideTime;
    }

    public String getSunsetTime() {
        return mSunsetTime;
    }

    public void setSunsetTime(String sunsetTime) {
        mSunsetTime = sunsetTime;
    }

    public String getGoodOrBad() {
        return mGoodOrBad;
    }

    public void setGoodOrBad(String goodOrBad) {
        mGoodOrBad = goodOrBad;
    }

    public String getHighTideHour() {
        return mHighTideHour;
    }

    public void setHighTideHour(String highTideHour) {
        mHighTideHour = highTideHour;
    }

    public String getSunsetHour() {
        return mSunsetHour;
    }

    public void setSunsetHour(String sunsetHour) {
        mSunsetHour = sunsetHour;
    }

    public String getLowTideHour() {
        return mLowTideHour;
    }

    public void setLowTideHour(String lowTideHour) {
        mLowTideHour = lowTideHour;
    }
/*
    public String getInfoType(){
        String infoType = null;

        if (mDataType.equals("High Tide") && mHighTideTime.equals(null)){
            infoType = "high";
        } else if (mDataType.equals("Low Tide") && mLowTideTime.equals(null)){
            infoType = "low";
        } else if (mDataType.equals("Sunset") && mSunsetTime.equals(null)){
            infoType = "sunset";
        }
        return infoType;
    }*/

}

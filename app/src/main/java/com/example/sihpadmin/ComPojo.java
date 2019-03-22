package com.example.sihpadmin;

import android.os.Parcel;
import android.os.Parcelable;

class ComPojo implements Parcelable {

    public String problem;
    public double lattitude;
    public double longitude;

    public ComPojo() {

    }

    public ComPojo(String problem, double lattitude, double longitude) {
        this.problem = problem;
        this.lattitude = lattitude;
        this.longitude = longitude;
    }

    protected ComPojo(Parcel in) {
        problem = in.readString();
        lattitude = in.readDouble();
        longitude = in.readDouble();
    }

    public static final Creator<ComPojo> CREATOR = new Creator<ComPojo>() {
        @Override
        public ComPojo createFromParcel(Parcel in) {
            return new ComPojo(in);
        }

        @Override
        public ComPojo[] newArray(int size) {
            return new ComPojo[size];
        }
    };

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public double getLattitude() {
        return lattitude;
    }

    public void setLattitude(double lattitude) {
        this.lattitude = lattitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(problem);
        dest.writeDouble(lattitude);
        dest.writeDouble(longitude);
    }
}


import java.sql.Date;
import java.util.List;
import java.util.ArrayList;

public class Apartment {
//    private String ID;
    private String aptName;
    private int posterID;
    private String address;
    private double area;
    private double price;
    private String availableDate;
    private String Type;
    private String Descr; // Description
    // private String

//    public String getID() {
//        return ID;
//    }
//
//    public void setID(String iD) {
//        ID = iD;
//    }

    public int getPosterID() {
        return posterID;
    }

    public void setPosterID(int posterID) {
        this.posterID = posterID;
    }

    public String getAddress() {
        return address;
    }

    public String getAptName() {
        return aptName;
    }

    public void setAptName(String aptName) {
        this.aptName = aptName;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getAvailableDate() {
        return availableDate;
    }

    public void setAvailableDate(String availableDate) {
        this.availableDate = availableDate;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public void setDescr(String descr) {
        Descr = descr;
    }

    public String getDescr() {
        return Descr;
    }

}

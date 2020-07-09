package example.charity.Model;

public class Charity {
    String name;
    String number;
    String mobileNum;
    String password;
    String id;


    public Charity() {
    }

    public Charity(String id,String name, String number, String mobileNum, String password) {
        this.id=id;
        this.name = name;
        this.number = number;
        this.mobileNum = mobileNum;
        this.password = password;
    }


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public String getMobileNum() {
        return mobileNum;
    }

    public String getPassword() {
        return password;
    }



}

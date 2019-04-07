package test.yespinoza.androidproject.Model.Entity;

import java.io.Serializable;

public class  Person implements Serializable {
    private String id;
    private String idNumber;
    private String name;
    private String lastName;
    private String email;
    private String phone;
    private String birthday;
    private String address;
    private String picture;

    public Person(){
        id = "";
        idNumber = "";
        name = "";
        lastName = "";
        email = "";
        phone = "";
        birthday = "";
        address = "";
    }

    public Person(String pId, String pName, String pLastName, String pEmail, String pPhone, String pDateOfBirth, String pAddress){
        idNumber = pId;
        name = pName;
        lastName = pLastName;
        email = pEmail;
        phone = pPhone;
        birthday = pDateOfBirth;
        address = pAddress;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }


    public String getFullName(){
        try {
            return name.concat(" ").concat(lastName);
        }catch (Exception oException){
            return "";
        }
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDateOfBirth() {
        return birthday;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.birthday = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPicture() {
        if(picture == null || picture.isEmpty())
            return "iVBORw0KGgoAAAANSUhEUgAAAGAAAABgCAYAAADimHc4AAADyklEQVR42u2caU8TURiFwV3c95hoNLGx4iRtp3Qj4UM/mbggRCxoNCT+gIIKohIjdQFUVAJx/beek1zCYEhp6XTu0Hue5M2UhLQz59z1vfdOV5cQQgghhBBCCCGEEEIIIerR19e3lyElIiCVSh0qlUq92Wx2FPEUMYf4DAM+Id4iJnO53P1CoXAVn3ukWHil/BiEHqPQuP6ByH/rBf8H/7uAzyPJZPKIFNwmiURiP0S8CzFXthK9TiwjbqmZahI0I6cg2lQLwm8IfNfEwMDACSnbAPl8/iIE+xCW+IGo4XvPS+E6sJSyrW+D+GsxXywWj0rpzTvbnjCbnXrNUblcPiDF/wOjl8F2ix8w4YYUD+D7/hkzYonKgK/qlDd2vI+jEj9gQkXKr6cTViwY8K1Wq+1S25/NpqIWPxBJNT8Wmh81Q+t0Q4RpiwZMOJ/vYVbTlgH47VnMCfY4awBTzEwpWzTgo9OTMhoAIb5Y7ITn+/v7DzprgOd5+9QEWe6EIcILi53wpPPDUAgxbrEGjDpvQKFQSFusAdecN4BDUQixaqH0f3e9/Q+mI55YqAEPpbyhWCyeizodzXVnKb+xMx6JsPm5I8U3n5TNRlD6p52efNUDwpxsZ2qCG7a0KL91h5wwO9tCz/uUSqXLUrjBTplpghDFn4Gpp6Vsk/MDbrZtZXTEsT6EH9bWxBZyRawNvu+Pc+jYxObcJVwfcbcFv0MyhgB3OkNgn7kj06Swn/hhYh4xw+VNrjF7nndYigkhhBBCCNFZkzOuZJmVtB5mUXnl39psG7LQXDTBbPY6JlllxAN8rnLCBcHfIN4hFs054QVzBuwVospZMA9hcL0ZBp3VbLgBsTOZzPF0Ou1ByCHEszDXifld5vjTCGbLGaa9nTeFCTKUzgu43uZCDBNnjeR6QsiM8jeW8buvuTKGWnWJzZczwpvk2k0KgPhlcVviWu34bV57MMh760jRK5XKbjxgLx72eRSlvJXawXtk39ERKWw+hHmY6TiU9mZqBTt61NTcjjUCN36FI5Y4l/gGzZhiP7FjhDeHLsZ2UolvwISfeKZ7sa8NHLdzCNkpwm9iRJXD5ViKz4PPNvf6R3mmIHZbW7jRyQXxAzEbp7kDD1mMOiT+WnM0HIvZNGezZmHcNQNWTZ7JLvl83rnSH4ihODQ/c64agFrw0qr6PGNr+ZipbQMWrZ6y4XCMbx1x2IAlq+8olQEyQAbIABkgA2SADHDTAO7Zxw28pwkuBrfC2D5x2U0TWBNcDPN6fO05EkIIIYQQQgghhBBCCBF3/gEj/FflW2H1ZgAAAABJRU5ErkJggg==";
        else
            return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}

package amalhichri.androidprojects.com.kotlinlearning.models;

/**
 * Created by Amal on 14/11/2017.
 */

public class User {

    private String id,emailAddress,pictureUrl,firstName,lastName,password;

    public User() {
    }

    /** to get entity from linkedin api **/
    public User(String id, String emailAddress, String pictureUrl, String firstName, String lastName) {
        this.id = id;
        this.emailAddress = emailAddress;
        this.pictureUrl = pictureUrl;
        this.firstName = firstName;
        this.lastName = lastName;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // just for test

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", pictureUrl='" + pictureUrl + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}

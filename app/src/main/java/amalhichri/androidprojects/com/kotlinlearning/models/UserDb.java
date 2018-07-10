package amalhichri.androidprojects.com.kotlinlearning.models;

import java.util.Calendar;

/**
 * Created by Amal on 14/11/2017.
 */

public class UserDb {

    private String id,username,emailAddress,pictureUrl,firstName,lastName;
    private Calendar created,last_loggued;
    private boolean confirmed;
    private int skill_learner,skill_challenger,skill_coder;

    public UserDb() {
    }

    public UserDb(String id, String username, String emailAddress, Calendar last_loggued,
                  String pictureUrl, int skill_learner, int skill_challenger, int skill_coder, boolean confirmed, Calendar created) {
        this.id = id;
        this.username = username;
        this.emailAddress = emailAddress;
        this.pictureUrl = pictureUrl;
        this.created = created;
        this.last_loggued = last_loggued;
        this.confirmed = confirmed;
        this.skill_learner = skill_learner;
        this.skill_challenger = skill_challenger;
        this.skill_coder = skill_coder;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return emailAddress;
    }

    public void setEmail(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPictureURL() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public Calendar getCreated() {
        return created;
    }

    public void setCreated(Calendar created) {
        this.created = created;
    }

    public Calendar getLast_loggued() {
        return last_loggued;
    }

    public void setLast_loggued(Calendar last_loggued) {
        this.last_loggued = last_loggued;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public int getSkill_learner() {
        return skill_learner;
    }

    public void setSkill_learner(int skill_learner) {
        this.skill_learner = skill_learner;
    }

    public int getSkill_challenger() {
        return skill_challenger;
    }

    public void setSkill_challenger(int skill_challenger) {
        this.skill_challenger = skill_challenger;
    }

    public int getSkill_coder() {
        return skill_coder;
    }

    public void setPicUrl(String pictureUrl) {
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

    public void setSkill_coder(int skill_coder) {
        this.skill_coder = skill_coder;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", pictureUrl='" + pictureUrl + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", created=" + created +
                ", last_loggued=" + last_loggued +
                ", confirmed=" + confirmed +
                ", skill_learner=" + skill_learner +
                ", skill_challenger=" + skill_challenger +
                ", skill_coder=" + skill_coder +
                '}';
    }
}

package amalhichri.androidprojects.com.kotlinlearning.models;

import java.util.Calendar;

/**
 * Created by Amal on 14/11/2017.
 */

public class User {

    private String id,username,emailAddress,pictureUrl,firstName,lastName;
    private Calendar created,last_loggued;
    private boolean confirmed;
    private int skill_learner,skill_challenger,skill_coder;

    public User() {
    }

    public User(String id, String username, String emailAddress, Calendar last_loggued,
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

    public Calendar getCreated() {
        return created;
    }

    public void setCreated(Calendar created) {
        this.created = created;
    }

}

package amalhichri.androidprojects.com.kotlinlearning.models;

import java.util.Calendar;

/**
 * Created by Amal on 12/01/2018.
 */

public class CompetitionAnswer {

    private int id;
    private String userId;
    private String content;
    private Calendar created;
    private int competitionId;
    private int competitionLevel;
    private String competitionTitle;
    private String profilePicture;
    private String username;


    public CompetitionAnswer() {
    }

    public CompetitionAnswer(int id, Calendar created) {
        this.id = id;
        this.created = created;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Calendar getCreated() {
        return created;
    }

    public void setCreated(Calendar created) {
        this.created = created;
    }

    public int getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(int competitionId) {
        this.competitionId = competitionId;
    }

    public int getCompetitionLevel() {
        return competitionLevel;
    }

    public void setCompetitionLevel(int competitionLevel) {
        this.competitionLevel = competitionLevel;
    }

    public String getCompetitionTitle() {
        return competitionTitle;
    }

    public void setCompetitionTitle(String competitionTitle) {
        this.competitionTitle = competitionTitle;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCreated_string(){

        long days=(long) Math.ceil((float) (Calendar.getInstance().getTimeInMillis() - created.getTimeInMillis()) / (24 * 60 * 60 * 1000))-1;
        if (days<1) return "Today "+created.get(Calendar.HOUR_OF_DAY)+":"+created.get(Calendar.MINUTE);
        else if(days<2 ) return days+" day";
        else if(days<30) return days+" days";
        else {
            return ((int) days/30)+" month and "+((int)days%30)+" day";
        }
    }

    @Override
    public String toString() {
        return "CompetitionAnswer{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", content='" + content + '\'' +
                ", created=" + created +
                ", competitionId=" + competitionId +
                ", competitionLevel=" + competitionLevel +
                ", competitionTitle='" + competitionTitle + '\'' +
                ", profilePicture='" + profilePicture + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}

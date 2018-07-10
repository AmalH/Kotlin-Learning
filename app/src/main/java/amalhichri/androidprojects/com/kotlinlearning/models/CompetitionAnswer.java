package amalhichri.androidprojects.com.kotlinlearning.models;

import java.util.Calendar;

/**
 * Created by Amal on 12/01/2018.
 */

public class CompetitionAnswer {
    private int id;
    private String id_user;
    private String content;
    private Calendar created;
    private int id_competition;
    private int competiton_level;
    private String competition_title;
    private String profile_picture;
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

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
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

    public int getId_competition() {
        return id_competition;
    }

    public void setId_competition(int id_competition) {
        this.id_competition = id_competition;
    }

    public int getCompetiton_level() {
        return competiton_level;
    }

    public void setCompetiton_level(int competiton_level) {
        this.competiton_level = competiton_level;
    }

    public String getCompetition_title() {
        return competition_title;
    }

    public void setCompetition_title(String competition_title) {
        this.competition_title = competition_title;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
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
                ", id_user='" + id_user + '\'' +
                ", content='" + content + '\'' +
                ", created=" + created +
                ", id_competition=" + id_competition +
                ", competiton_level=" + competiton_level +
                ", competition_title='" + competition_title + '\'' +
                ", profile_picture='" + profile_picture + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}

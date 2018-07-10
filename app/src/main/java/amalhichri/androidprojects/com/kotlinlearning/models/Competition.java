package amalhichri.androidprojects.com.kotlinlearning.models;

import java.util.Calendar;

/**
 * Created by Amal on 11/01/2018.
 */

public class Competition {
    private int id;
    private String id_user;
    private String content;
    private long solved;
    private Calendar created;
    private int level;
    private String title;
    private String profile_picture;
    private String username;

    public Competition() {
    }

    public Competition(int id, String id_user, String content, Calendar created, int level, String title, String username) {
        this.id = id;
        this.id_user = id_user;
        this.content = content;
        this.created = created;
        this.level = level;
        this.title = title;
        this.username = username;
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

    public long getSolved() {
        return solved;
    }

    public void setSolved(long solved) {
        this.solved = solved;
    }

    public Calendar getCreated() {
        return created;
    }

    public void setCreated(Calendar created) {
        this.created = created;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getSolvedString() {
        if(solved>1000) return ((int)(solved/1000))+"."+(solved%1000)+"k";
        return String.valueOf(solved);
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

    public String getUser_name_captalized(){return username.substring(0, 1).toUpperCase() + username.substring(1);}

    @Override
    public String toString() {
        return "Competition{" +
                "id=" + id +
                ", id_user=" + id_user +
                ", content='" + content + '\'' +
                ", solved=" + solved +
                ", created=" + created +
                ", level=" + level +
                ", title='" + title + '\'' +
                ", profile_picture='" + profile_picture + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}

package amalhichri.androidprojects.com.kotlinlearning.models;

import java.util.Calendar;

/**
 * Created by Odil on 21/12/2017.
 */

public class ForumAnswer {
    private int id;
    private String content;
    private Calendar created;
    private long rating;
    private String userid;
    private String username;
    private String userpicture;

    public ForumAnswer(int id, String content, Calendar created, long rating, String userid, String username, String userpicture) {
        this.id = id;
        this.content = content;
        this.created = created;
        this.rating = rating;
        this.userid = userid;
        this.username = username;
        this.userpicture = userpicture;
    }
    public ForumAnswer(int id, String content, Calendar created, long rating, String userid, String username) {
        this.id = id;
        this.content = content;
        this.created = created;
        this.rating = rating;
        this.userid = userid;
        this.username = username;
        this.userpicture = null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public long getRating() {
        return rating;
    }

    public String getRatingString() {
        String sign;
        if(rating>0) sign="+"; else if(rating<0) sign="-"; else sign="";
        return sign+ String.valueOf(rating);
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

    public void setRating(long rating) {
        this.rating = rating;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserpicture() {
        return userpicture;
    }

    public void setUserpicture(String userpicture) {
        this.userpicture = userpicture;
    }

    @Override
    public String toString() {
        return "ForumAnswer{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", created=" + created +
                ", rating=" + rating +
                ", userid='" + userid + '\'' +
                ", username='" + username + '\'' +
                ", userpicture='" + userpicture + '\'' +
                '}';
    }
}

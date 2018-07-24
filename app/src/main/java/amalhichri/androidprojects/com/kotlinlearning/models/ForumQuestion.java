package amalhichri.androidprojects.com.kotlinlearning.models;

import java.util.Calendar;

/**
 * Created by Amal on 24/11/2017.
 */

public class ForumQuestion {


    private int id;
    private String subject;
    private int rating;
    private String tags;     // in database its one string for many tags !!;
    private Calendar created;

    // will not be used !! to be deleted from database !
    private String content;
    private int views;

    private String id_User;
    private String user_name;
    private String user_picture_url;
    private String code;
    private Calendar edited;

    // to get from user ID
    //private String postedBy;
   // private String postedBy_picUrl;

    // to get from created
    //private int postedSince;

    public ForumQuestion(){}

    public ForumQuestion(int id, String subject, int rating, String tags,
                         Calendar created, String content, int views, String id_User,
                         String user_name, String user_picture_url) {
        this.id = id;
        this.subject = subject;
        this.rating = rating;
        this.tags = tags;
        this.created = created;
        this.content = content;
        this.views = views;
        this.id_User = id_User;
        this.user_name = user_name;
        this.user_picture_url = user_picture_url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getRating() {
        return rating;
    }

    public String getRatingString() {
        if(rating>1000) return ("+"+(int)(rating/1000))+"."+(rating%1000)+"k";
        if (rating>0) return "+"+ String.valueOf(rating);
        return String.valueOf(rating);
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Calendar getCreated() {
        return created;
    }

    public String getCreated_string(){

        long days=(long) Math.ceil((float) (Calendar.getInstance().getTimeInMillis() - created.getTimeInMillis()) / (24 * 60 * 60 * 1000))-1;
        if (days<1) return "Today "+created.get(Calendar.HOUR_OF_DAY)+":"+created.get(Calendar.MINUTE);
        else if(days<2 ) return days+" day";
        else if(days<30) return days+" days";
        else {
            return ((int) days/30)+" months, "+((int)days%30)+" day";
        }
    }

    public void setCreated(Calendar created) {
        this.created = created;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public String getId_User() {
        return id_User;
    }

    public void setId_User(String id_User) {
        this.id_User = id_User;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getUser_name_captalized(){return user_name.substring(0, 1).toUpperCase() + user_name.substring(1);}

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_picture_url() {
        return user_picture_url;
    }

    public void setUser_picture_url(String user_picture_url) {
        this.user_picture_url = user_picture_url;
    }

    public String getViews_string() {
        return views+"";
    }

    public String getRating_string() {
        return rating+"";
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Calendar getEdited() {
        return edited;
    }

    public String getEditedString(){
        long days=(long) Math.ceil((float) (Calendar.getInstance().getTimeInMillis() - edited.getTimeInMillis()) / (24 * 60 * 60 * 1000))-1;
        if (days<1) return "Today "+edited.get(Calendar.HOUR_OF_DAY)+":"+edited.get(Calendar.MINUTE);
            else if(days<2 ) return days+" day";
            else if(days<30) return days+" days";
            else {
                return ((int) days/30)+" month and "+((int)days%30)+" day";
            }

    }

    public void setEdited(Calendar edited) {
        this.edited = edited;
    }

    @Override
    public String toString() {
        return "ForumQuestion{" +
                "id=" + id +
                ", subject='" + subject + '\'' +
                ", rating=" + rating +
                ", tags='" + tags + '\'' +
                ", created=" + created +
                ", content='" + content + '\'' +
                ", views=" + views +
                ", id_User='" + id_User + '\'' +
                ", user_name='" + user_name + '\'' +
                ", user_picture_url='" + user_picture_url + '\'' +
                ", code='" + code + '\'' +
                ", edited=" + edited +
                '}';
    }
}

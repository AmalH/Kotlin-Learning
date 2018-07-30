package amalhichri.androidprojects.com.kotlinlearning.models;

import java.util.Calendar;

/**
 * Created by Amal on 24/11/2017.
 */

public class ForumQuestion {


    private int id;
    private String subject;
    private int rating;
    private String tags;
    private Calendar created;

    private String content;
    private int views;

    private String userId;
    private String userName;
    private String userPictureUrl;
    private String code;
    private Calendar edited;

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
        this.userId = id_User;
        this.userName = user_name;
        this.userPictureUrl = user_picture_url;
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


    public String getUserId() {
        return userId;
    }


    public String getUserName() {
        return userName;
    }

    public String getUser_name_captalized(){return userName.substring(0, 1).toUpperCase() + userName.substring(1);}


    public String getUserPictureUrl() {
        return userPictureUrl;
    }


    public String getViews_string() {
        return views+"";
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


}

package amalhichri.androidprojects.com.kotlinlearning.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;

import amalhichri.androidprojects.com.kotlinlearning.models.UserDb;

/**
 * Created by Amal on 19/11/2017.
 */

public class DataBaseHandler extends SQLiteOpenHelper {

    //Database version
    public static final int DATABASE_VERSION=2;
    //Database name
    public static final String DATABASE_NAME="ikotlin";
    //table user
    public static final String TABLE_USER="user";
    //table course
    public static final String TABLE_COURSE="course";
    //singleton
    public static DataBaseHandler sInstance;

    //Table user columns
    public static final String KEY_ID="id";
    public static final String KEY_UID="uid";
    public static final String KEY_USERNAME="username";
    public static final String KEY_EMAIL="email";
    public static final String KEY_LASTLOGGED="last_logged";
    public static final String KEY_PICTURE="picture_url";
    public static final String KEY_SKILL_LEARNER="skill_learner";
    public static final String KEY_SKILL_CHALLENGER="skill_challenger";
    public static final String KEY_SKILL_CODER="skill_coder";
    public static final String KEY_CONFIRMED_ACCOUNT="confirmed";
    public static final String KEY_CREATED="created";
    public static final String KEY_COURSE_ID="courseid";

    public DataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DataBaseHandler getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DataBaseHandler(context.getApplicationContext());
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_ARTICLE_TABLE="CREATE TABLE "+TABLE_USER+"("+
                KEY_ID+" INTEGER PRIMARY KEY, "+
                KEY_UID+" TEXT, "+
                KEY_USERNAME+" TEXT, "+
                KEY_EMAIL+" TEXT, "+
                KEY_CONFIRMED_ACCOUNT+" INTEGER, "+
                KEY_LASTLOGGED+" INTEGER, "+
                KEY_PICTURE+" TEXT, "+
                KEY_SKILL_LEARNER+" INTEGER, "+
                KEY_SKILL_CHALLENGER+" INTEGER, "+
                KEY_SKILL_CODER+" INTEGER ,"+
                KEY_CREATED+" INTEGER "+
                ")";

        String CREATE_ARTICLE_COURSE="CREATE TABLE "+TABLE_COURSE+"("+
                KEY_ID+" INTEGER PRIMARY KEY, "+
                KEY_UID+" TEXT, "+
                KEY_COURSE_ID+" INTEGER )";

        sqLiteDatabase.execSQL(CREATE_ARTICLE_TABLE);
        sqLiteDatabase.execSQL(CREATE_ARTICLE_COURSE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //enlever la table si existe
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_USER);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_COURSE);
        //creation de la table
        onCreate(sqLiteDatabase);
    }

    //USER METHODS
    public void saveUser (UserDb user){
        FirebaseAuth.getInstance().getCurrentUser().reload();
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_UID, user.getId());
        values.put(KEY_USERNAME, user.getUsername());
        values.put(KEY_EMAIL, user.getEmail());
        values.put(KEY_CONFIRMED_ACCOUNT, FirebaseAuth.getInstance().getCurrentUser().isEmailVerified());
        values.put(KEY_LASTLOGGED, user.getLast_loggued().getTimeInMillis());
        values.put(KEY_PICTURE, user.getPictureURL());
        values.put(KEY_SKILL_LEARNER, user.getSkill_learner());
        values.put(KEY_SKILL_CHALLENGER, user.getSkill_challenger());
        values.put(KEY_SKILL_CODER, user.getSkill_coder());
        values.put(KEY_CREATED, user.getCreated().getTimeInMillis());
        //INSERT
        db.insert(TABLE_USER,null,values);
        //CLOSE CONNECTION
        db.close();
    }


    public void updateUser(UserDb user){
        FirebaseAuth.getInstance().getCurrentUser().reload();
        if(userExists()){
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_UID, user.getId());
            values.put(KEY_USERNAME, user.getUsername());
            values.put(KEY_EMAIL, user.getEmail());
            values.put(KEY_CONFIRMED_ACCOUNT, FirebaseAuth.getInstance().getCurrentUser().isEmailVerified());
            values.put(KEY_LASTLOGGED, user.getLast_loggued().getTimeInMillis());
            values.put(KEY_PICTURE, user.getPictureURL());
            values.put(KEY_SKILL_LEARNER, user.getSkill_learner());
            values.put(KEY_SKILL_CHALLENGER, user.getSkill_challenger());
            values.put(KEY_SKILL_CODER, user.getSkill_coder());
            values.put(KEY_CREATED, user.getCreated().getTimeInMillis());
            //UPDATE
            db.update(TABLE_USER,values,KEY_UID+" = ?", new String[] {String.valueOf(user.getId())});
            //CLOSE CONNECTION
            // Log.e("base ",o.toString());
            db.close();
        }
        else
            saveUser(user);

    }

    public void deleteUser(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER,null,null);
        db.close();
    }

    public boolean userExists(){
        SQLiteDatabase db= this.getReadableDatabase();
        String selectQuery="SELECT * FROM "+TABLE_USER;
        Cursor cursor = db.rawQuery(selectQuery, null);
        int cnt =cursor.getCount();
        cursor.close();
        if (cnt>0) return true;
        return false;
    }

    public UserDb getUser(){
        FirebaseAuth.getInstance().getCurrentUser().reload();
        if(userExists()){

            SQLiteDatabase db= this.getReadableDatabase();
            String selectQuery="SELECT * FROM "+TABLE_USER;
            Cursor cursor = db.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            Calendar created= Calendar.getInstance();
            created.setTimeInMillis(cursor.getLong(5));
            Calendar last_log= Calendar.getInstance();
            last_log.setTimeInMillis(cursor.getLong(10));

            UserDb user=new UserDb(cursor.getString(1),cursor.getString(2),cursor.getString(3),
                    last_log, cursor.getString(6), cursor.getInt(7),cursor.getInt(8),
                    cursor.getInt(9),FirebaseAuth.getInstance().getCurrentUser().isEmailVerified(),created);
            // Log.e("getting user", user.toString());
            return user;
        }
        return null;
    }

    public void setUserConfirmed(Boolean c){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CONFIRMED_ACCOUNT, c);
        db.update(TABLE_USER,values,"id = ?", new String[] {String.valueOf(getUser().getId())});
    }

    public void reset_table_user(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        this.onCreate(this.getWritableDatabase());
    }


    public void addCourse(String uid,int courseid){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_UID, uid);
        values.put(KEY_COURSE_ID, courseid);
        //INSERT
        db.insert(TABLE_COURSE,null,values);
        //CLOSE CONNECTION
        db.close();
    }
    public int[] getCourses(String uid){
        SQLiteDatabase db= this.getReadableDatabase();
        String selectQuery="SELECT "+KEY_COURSE_ID+" FROM "+TABLE_COURSE+" WHERE "+KEY_UID+" = '"+uid+"'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        int[] array = new int[cursor.getCount()];
        int i = 0;
        while(cursor.moveToNext()){
            int uname = cursor.getInt(cursor.getColumnIndex(KEY_COURSE_ID));
            array[i] = uname;
            i++;
        }
        return array;
    }
    public void deleteCourse(String uid,int courseid) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("position :",courseid+"");
        db.delete(TABLE_COURSE,KEY_COURSE_ID+"="+courseid+" and "+KEY_UID+"= '"+uid+"'",null);
        db.close();
    }
    public boolean courseExist(String uid,int courseid) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_COURSE + " WHERE " + KEY_UID + "= '" + uid + "' and " + KEY_COURSE_ID + "=" + courseid;
        Cursor cursor = db.rawQuery(selectQuery, null);
        boolean i = cursor.getCount()>0;
        //Log.d("cursor",cursor.toString()+"    -      "+cursor.getCount());
        db.close();
        return i;
    }
}

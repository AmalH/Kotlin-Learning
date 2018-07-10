package amalhichri.androidprojects.com.kotlinlearning.utils;

import java.util.ArrayList;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.models.Chapter;
import amalhichri.androidprojects.com.kotlinlearning.models.Course;

/**
 * Created by Amal on 25/11/2017.
 */

public class AllCourses {


    /**** course 1 ****/

    private static ArrayList<Chapter> chapters;
    private static Course course;

    public static Course getCourse(int i){
        chapters=new ArrayList<>();
        switch (i){
            case 0:
                chapters.add(AllChapters.course1_chapter1);
                chapters.add(AllChapters.course1_chapter2);
                chapters.add(AllChapters.course1_chapter3);
                chapters.add(AllChapters.course1_chapter4);
                course=new Course(chapters,"Overview","This course containes 4 chapters. Introducing the 4 major applications of Kotlin language", R.drawable.ic_overview);
                break;
            case 1:
                chapters.add(AllChapters.course2_chapter1);
                chapters.add(AllChapters.course2_chapter2);
                chapters.add(AllChapters.course2_chapter3);
                course=new Course(chapters,"Getting started","This course containes 4 chapters. Introducing the 4 major applications of Kotlin language",R.drawable.ic_start);
                break;
            case 2:
                chapters.add(AllChapters.course3_chapter1);
                chapters.add(AllChapters.course3_chapter2);
                chapters.add(AllChapters.course3_chapter3);
                chapters.add(AllChapters.course3_chapter4);
                course=new Course(chapters,"Basics","This course containes 4 chapters. Introducing the 4 major applications of Kotlin language",R.drawable.ic_basics);
                break;
            case 3:
                chapters.add(AllChapters.course4_chapter1);chapters.add(AllChapters.course4_chapter2);
                chapters.add(AllChapters.course4_chapter3);chapters.add(AllChapters.course4_chapter4);chapters.add(AllChapters.course4_chapter5);
                chapters.add(AllChapters.course4_chapter6);chapters.add(AllChapters.course4_chapter7);chapters.add(AllChapters.course4_chapter8);
                chapters.add(AllChapters.course4_chapter9);chapters.add(AllChapters.course4_chapter10);chapters.add(AllChapters.course4_chapter11);
                chapters.add(AllChapters.course4_chapter12); chapters.add(AllChapters.course4_chapter13);
                course=new Course(chapters,"Classes and objects","This course containes 4 chapters. Introducing the 4 major applications of Kotlin language",R.drawable.ic_classesobjects);
                break;
            case 4:
                chapters.add(AllChapters.course5_chapter1);
                chapters.add(AllChapters.course5_chapter2);
                chapters.add(AllChapters.course5_chapter3);
                chapters.add(AllChapters.course5_chapter4);
                course=new Course(chapters,"Functions and Lambdas","This course containes 4 chapters. Introducing the 4 major applications of Kotlin language",R.drawable.ic_functions);
                break;
            case 5:
                chapters.add(AllChapters.course6_chapter1);chapters.add(AllChapters.course6_chapter2);
                chapters.add(AllChapters.course6_chapter3);chapters.add(AllChapters.course6_chapter4);chapters.add(AllChapters.course6_chapter5);
                chapters.add(AllChapters.course6_chapter6);chapters.add(AllChapters.course6_chapter7);chapters.add(AllChapters.course6_chapter8);
                chapters.add(AllChapters.course6_chapter9);chapters.add(AllChapters.course6_chapter10);chapters.add(AllChapters.course6_chapter11);
                chapters.add(AllChapters.course6_chapter12); chapters.add(AllChapters.course6_chapter13);chapters.add(AllChapters.course6_chapter14);
                chapters.add(AllChapters.course6_chapter14);
                course=new Course(chapters,"Others","This course containes 4 chapters. Introducing the 4 major applications of Kotlin language",R.drawable.ic_others);
                break;
            case 6:
                chapters.add(AllChapters.course7_chapter1);
                chapters.add(AllChapters.course7_chapter2);
                course=new Course(chapters,"Java Interop","This course containes 4 chapters. Introducing the 4 major applications of Kotlin language",R.drawable.ic_java);
                break;
            case 7:
                chapters.add(AllChapters.course8_chapter1);
                chapters.add(AllChapters.course8_chapter2);
                chapters.add(AllChapters.course8_chapter3);
                chapters.add(AllChapters.course8_chapter4);
                chapters.add(AllChapters.course8_chapter5);
                chapters.add(AllChapters.course8_chapter6);
                course=new Course(chapters,"Javascript","This course containes 4 chapters. Introducing the 4 major applications of Kotlin language",R.drawable.ic_javascript);
                break;
            default:
                break;
        }

        return course;
    }

}

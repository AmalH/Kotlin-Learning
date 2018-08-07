package amalhichri.androidprojects.com.kotlinlearning.utils;

import java.util.ArrayList;

import amalhichri.androidprojects.com.kotlinlearning.R;
import amalhichri.androidprojects.com.kotlinlearning.models.Chapter;
import amalhichri.androidprojects.com.kotlinlearning.models.Course;

/**
 * Created by Amal on 25/11/2017.
 */

public class AllCourses {


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
                course=new Course(chapters,"Overview","This course introduces the 4 major applications of Kotlin programming language.", 40,R.drawable.ic_overview);
                course.setAdvancement(40);
                break;
            case 1:
                chapters.add(AllChapters.course2_chapter1);
                chapters.add(AllChapters.course2_chapter2);
                chapters.add(AllChapters.course2_chapter3);
                course=new Course(chapters,"Getting started","Basic syntax and the coding conventions of Kotlin language.",60,R.drawable.ic_start);
                course.setAdvancement(20);
                break;
            case 2:
                chapters.add(AllChapters.course3_chapter1);
                chapters.add(AllChapters.course3_chapter2);
                chapters.add(AllChapters.course3_chapter3);
                chapters.add(AllChapters.course3_chapter4);
                course=new Course(chapters,"Basics","Basics of Kotlin programming language from types to control flow.",60,R.drawable.ic_basics);
                break;
            case 3:
                chapters.add(AllChapters.course4_chapter1);chapters.add(AllChapters.course4_chapter2);
                chapters.add(AllChapters.course4_chapter3);chapters.add(AllChapters.course4_chapter4);chapters.add(AllChapters.course4_chapter5);
                chapters.add(AllChapters.course4_chapter6);chapters.add(AllChapters.course4_chapter7);chapters.add(AllChapters.course4_chapter8);
                chapters.add(AllChapters.course4_chapter9);chapters.add(AllChapters.course4_chapter10);chapters.add(AllChapters.course4_chapter11);
                chapters.add(AllChapters.course4_chapter12); chapters.add(AllChapters.course4_chapter13);
                course=new Course(chapters,"Classes and objects","Inheritance, interfaces, delegation, and more on Object Oriented Kotlin on this course.",60,R.drawable.ic_classesobjects);
                break;
            case 4:
                chapters.add(AllChapters.course5_chapter1);
                chapters.add(AllChapters.course5_chapter2);
                chapters.add(AllChapters.course5_chapter3);
                chapters.add(AllChapters.course5_chapter4);
                course=new Course(chapters,"Functions and Lambdas","Writing first-class functions and their lambdas alternatives in Kotlin.",45,R.drawable.ic_functions);
                course.setAdvancement(80);
                break;
            case 5:
                chapters.add(AllChapters.course6_chapter1);chapters.add(AllChapters.course6_chapter2);
                chapters.add(AllChapters.course6_chapter3);chapters.add(AllChapters.course6_chapter4);chapters.add(AllChapters.course6_chapter5);
                chapters.add(AllChapters.course6_chapter6);chapters.add(AllChapters.course6_chapter7);chapters.add(AllChapters.course6_chapter8);
                chapters.add(AllChapters.course6_chapter9);chapters.add(AllChapters.course6_chapter10);chapters.add(AllChapters.course6_chapter11);
                chapters.add(AllChapters.course6_chapter12); chapters.add(AllChapters.course6_chapter13);chapters.add(AllChapters.course6_chapter14);
                chapters.add(AllChapters.course6_chapter14);
                course=new Course(chapters,"Other","Collections, exceptions, reflection and more various concepts on this course.",90,R.drawable.ic_others);
                break;
            case 6:
                chapters.add(AllChapters.course7_chapter1);
                chapters.add(AllChapters.course7_chapter2);
                course=new Course(chapters,"Java Interop","This course is on Kotlin from Java / Java from Kotlin calls.",30,R.drawable.ic_java);
                break;
            case 7:
                chapters.add(AllChapters.course8_chapter1);
                chapters.add(AllChapters.course8_chapter2);
                chapters.add(AllChapters.course8_chapter3);
                chapters.add(AllChapters.course8_chapter4);
                chapters.add(AllChapters.course8_chapter5);
                chapters.add(AllChapters.course8_chapter6);
                course=new Course(chapters,"Javascript","Kotlin - Javascript interoperability",60,R.drawable.ic_javascript);
                break;
            default:
                break;
        }

        return course;
    }

}

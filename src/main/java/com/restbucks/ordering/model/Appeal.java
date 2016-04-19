package com.restbucks.ordering.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

public class Appeal {
    
    private final int studentID;
    //private int id;
    @XmlTransient
    private final List<String> comments;
    @XmlTransient
    private String title;
    @XmlTransient
    private AppealStatus appealStatus = AppealStatus.SUBMITTED;

    public Appeal(int sid, String title) {
      this(sid, title, AppealStatus.CREATED);
    }
    

    public Appeal(int sid, String title, AppealStatus appealStatus) {
        this.comments = new ArrayList<String>();
        this.studentID = sid;
        this.title = title;
        this.appealStatus = appealStatus;
    }

    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }

    public int getStudentID() {
        return studentID;
    }
    
    public void setAppealStatus(AppealStatus status) {
        this.appealStatus = status;
    }

    public AppealStatus getStatus() {
        return appealStatus;
    }

    public void addComment(String comment) {
        this.comments.add(comment);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Title: " + title + "\n" );
        sb.append("Student ID: " + studentID + "\n");
        sb.append("Status: " + appealStatus + " \n");
        sb.append("Comments: " + comments.toString());
        return sb.toString();
    }
    
//    public String toString() {
//        StringBuilder sb = new StringBuilder();
//        sb.append("Location: " + location + "\n");
//        sb.append("Status: " + status + "\n");
//        for(Item i : items) {
//            sb.append("Item: " + i.toString()+ "\n");
//        }
//        return sb.toString();
//    }
    
    
}
package com.java.course_management.model;


public class Schedule {

  private long weekNum;
  private long week;
  private long timeQuantum;
  private String classroom;
  private String classId;


  public long getWeekNum() {
    return weekNum;
  }

  public void setWeekNum(long weekNum) {
    this.weekNum = weekNum;
  }


  public long getWeek() {
    return week;
  }

  public void setWeek(long week) {
    this.week = week;
  }


  public long getTimeQuantum() {
    return timeQuantum;
  }

  public void setTimeQuantum(long timeQuantum) {
    this.timeQuantum = timeQuantum;
  }


  public String getClassroom() {
    return classroom;
  }

  public void setClassroom(String classroom) {
    this.classroom = classroom;
  }


  public String getClassId() {
    return classId;
  }

  public void setClassId(String classId) {
    this.classId = classId;
  }

}

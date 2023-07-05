package com.java.course_management.model;


public class Blog {

  private String commentTime;
  private String comment;
  private long value;
  private String studentId;
  private String courseName;
  private String teacherName;


  public String getCommentTime() {
    return commentTime;
  }

  public void setCommentTime(String commentTime) {
    this.commentTime = commentTime;
  }


  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }


  public long getValue() {
    return value;
  }

  public void setValue(long value) {
    this.value = value;
  }


  public String getStudentId() {
    return studentId;
  }

  public void setStudentId(String studentId) {
    this.studentId = studentId;
  }


  public String getCourseName() {
    return courseName;
  }

  public void setCourseName(String courseName) {
    this.courseName = courseName;
  }


  public String getTeacherName() {
    return teacherName;
  }

  public void setTeacherName(String teacherName) {
    this.teacherName = teacherName;
  }

}

package com.java.course_management.model;


public class Student {

  private String studentId;
  private String studentName;
  private String gender;
  private long enrollmentYear;
  private String departmentName;
  private String majorName;
  private String password;


  public String getStudentId() {
    return studentId;
  }

  public void setStudentId(String studentId) {
    this.studentId = studentId;
  }


  public String getStudentName() {
    return studentName;
  }

  public void setStudentName(String studentName) {
    this.studentName = studentName;
  }


  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }


  public long getEnrollmentYear() {
    return enrollmentYear;
  }

  public void setEnrollmentYear(long enrollmentYear) {
    this.enrollmentYear = enrollmentYear;
  }


  public String getDepartmentName() {
    return departmentName;
  }

  public void setDepartmentName(String departmentName) {
    this.departmentName = departmentName;
  }


  public String getMajorName() {
    return majorName;
  }

  public void setMajorName(String majorName) {
    this.majorName = majorName;
  }


  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

}

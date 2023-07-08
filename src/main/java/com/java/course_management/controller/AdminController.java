package com.java.course_management.controller;

import com.java.course_management.model.AllAttributes;
import com.java.course_management.model.Result;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class AdminController {

    @Resource
    JdbcTemplate jdbcTemplate;

    /**
     * 管理员获取学院名称列表
     *
     * @return
     */
    @GetMapping("adminGetDepartmentName")
    public Result adminGetDepartmentName() {
        String sql = "select distinct departmentName                                " +
                "from requirement;                                                  ";
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
        return Result.ok(result);
    }

    /**
     * 管理员获取对应学院中的专业名称列表
     *
     * @param departmentName 学院名称
     * @return
     */
    @GetMapping("adminGetMajorName")
    public Result adminGetMajorName(String departmentName) {
        String sql = "select distinct majorName                                     " +
                "from requirement where departmentName=?;                           ";
        Object[] args = new Object[]{departmentName};
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, args);
        return Result.ok(result);
    }

    /**
     * 管理员查询课程信息
     *
     * @param keyword    搜索框关键词
     * @param courseType 课程类型
     * @param compulsory 课程性质
     * @return
     */
    @GetMapping("adminGetCourse")
    public Result adminGetCourse(
            String keyword,
            String departmentName,
            String majorName,
            String courseType,
            String compulsory) {
        String sql = "select *                                                      " +
                "from requirement                                                   " +
                "where (courseName like concat('%', ?, '%')                         " +
                "       or courseId like concat('%', ?, '%'))                       " +
                "       and (departmentName=? or ?)                                 " +
                "       and (majorName=? or ?)                                      " +
                "       and (courseType=? or ?)                                     " +
                "       and (compulsory=? or ?)                                     " +
                "order by semester;                                                 ";
        Object[] args = new Object[]{
                keyword,
                keyword,
                departmentName,
                departmentName.isEmpty(),
                majorName,
                majorName.isEmpty(),
                courseType,
                courseType.isEmpty(),
                compulsory,
                compulsory.isEmpty()};
        System.out.println(Arrays.toString(args));
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, args);
        return Result.ok(result);
    }

    /**
     * 管理员查询学生信息
     *
     * @param keyword 搜索框关键词
     * @return
     */
    @GetMapping("adminGetStudent")
    public Result adminGetStudent(String keyword) {
        String sql = "select *                                                      " +
                "from student                                                       " +
                "where studentId like concat('%', ?, '%')                           " +
                "       or studentName like concat('%', ?, '%')                     " +
                "order by studentId;                                                ";
        Object[] args = new Object[]{keyword, keyword};
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, args);
        return Result.ok(result);
    }

    /**
     * 管理员查询教师信息
     *
     * @param keyword 查询框关键词
     * @return
     */
    @GetMapping("adminGetTeacher")
    public Result adminGetTeacher(String keyword) {
        String sql = "select distinct *                                             " +
                "from teacher                                                       " +
                "where teacherId like concat('%', ?, '%')                           " +
                "       or teacherName like concat('%', ?, '%')                     " +
                "       or departmentName like concat('%', ?, '%')                  " +
                "order by teacherId;                                                ";
        Object[] args = new Object[]{keyword, keyword, keyword};
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, args);
        return Result.ok(result);
    }

    /**
     * 管理员增加学生信息
     *
     * @param studentId
     * @param studentName
     * @param departmentName
     * @param majorName
     * @param gender
     * @param enrollmentYear
     * @return
     */
    @PostMapping("adminInsertStudent")
    public Result adminInsertStudent(@RequestBody AllAttributes allAttributes) {
        String sql = "insert into student (                                         " +
                "studentId,                                                         " +
                "studentName,                                                       " +
                "departmentName,                                                    " +
                "majorName,                                                         " +
                "gender,                                                            " +
                "enrollmentYear                                                     " +
                ") values (?, ?, ?, ?, ?, ?);                                       ";
        Object[] args = new Object[]{
                allAttributes.getStudentId(),
                allAttributes.getStudentName(),
                allAttributes.getDepartmentName(),
                allAttributes.getMajorName(),
                allAttributes.getGender(),
                allAttributes.getEnrollmentYear()};
        int result = jdbcTemplate.update(sql, args);
        if (result == 0) {
            return Result.fail("Args: " + Arrays.toString(args));
        }
        return Result.ok();
    }

    /**
     * 管理员添加课程信息
     *
     * @param courseId
     * @param courseName
     * @param departmentName
     * @param majorName
     * @param semester
     * @param courseType
     * @param compulsory
     * @param credit
     * @return
     */
    @PostMapping("adminInsertCourse")
    public Result adminInsertCourse(@RequestBody AllAttributes allAttributes) {
        String sql = "insert into requirement (                                     " +
                "courseId,                                                          " +
                "courseName,                                                        " +
                "departmentName,                                                    " +
                "majorName,                                                         " +
                "semester,                                                          " +
                "courseType,                                                        " +
                "compulsory,                                                        " +
                "credit                                                             " +
                ") values (?, ?, ?, ?, ?, ?, ?, ?);                                 ";
        Object[] args = new Object[]{
                allAttributes.getCourseId(),
                allAttributes.getCourseName(),
                allAttributes.getDepartmentName(),
                allAttributes.getMajorName(),
                allAttributes.getSemester(),
                allAttributes.getCourseType(),
                allAttributes.getCompulsory(),
                allAttributes.getCredit()};
        int result = jdbcTemplate.update(sql, args);
        if (result == 0) {
            return Result.fail("Args: " + Arrays.toString(args));
        }
        return Result.ok();
    }

    /**
     * 管理员删除学生选课信息
     *
     * @param studentId
     * @param classId
     * @return
     */
    @PostMapping("adminDeleteReport")
    public Result adminDeleteReport(@RequestBody AllAttributes allAttributes) {
        String sql = "delete from report                                            " +
                "where studentId=?                                                  " +
                "       and classId=?;                                              ";
        Object[] args = new Object[]{
                allAttributes.getStudentId(),
                allAttributes.getClassId()};
        int result = jdbcTemplate.update(sql, args);
        if (result == 0) {
            return Result.fail("Args: " + Arrays.toString(args));
        }
        return Result.ok();
    }

    /**
     * 管理员删除学生信息
     *
     * @param studentId
     * @return
     */
    @PostMapping("adminDeleteStudent")
    public Result adminDeleteStudent(@RequestBody AllAttributes allAttributes) {
        String sql = "delete from student                                           " +
                "where studentId=?;                                                 ";
        Object[] args = new Object[]{allAttributes.getStudentId()};
        int result = jdbcTemplate.update(sql, args);
        if (result == 0) {
            return Result.fail("Args: " + Arrays.toString(args));
        }
        return Result.ok();
    }

    /**
     * 管理员更新学生信息
     *
     * @param studentId
     * @param studentName
     * @param enrollmentYear
     * @param departmentName
     * @param majorName
     * @return
     */
    @PostMapping("adminUpdateStudent")
    public Result adminUpdateStudent(@RequestBody AllAttributes allAttributes) {
        String sql = "update student set                                            " +
                "studentName=?,                                                     " +
                "enrollmentYear=?,                                                  " +
                "departmentName=?,                                                  " +
                "majorName=?                                                        " +
                "where studentId=?;                                                 ";
        Object[] args = new Object[]{
                allAttributes.getStudentName(),
                allAttributes.getEnrollmentYear(),
                allAttributes.getDepartmentName(),
                allAttributes.getMajorName(),
                allAttributes.getStudentId()};
        int result = jdbcTemplate.update(sql, args);
        if (result == 0) {
            return Result.fail("Args: " + Arrays.toString(args));
        }
        return Result.ok();
    }

    /**
     * 新增教学班
     *
     * @param classId
     * @param capacity
     * @param campus
     * @param teacherId
     * @param couseId
     * @return
     */
    @PostMapping("adminInsertClass")
    public Result adminInsertClass(@RequestBody AllAttributes allAttributes) {
        String sql = "insert into class (                                           " +
                "classId,                                                           " +
                "capacity,                                                          " +
                "campus,                                                            " +
                "teacherId,                                                         " +
                "courseId                                                           " +
                ") values (?, ?, ?, ?, ?);                                          ";
        Object[] args = new Object[]{
                allAttributes.getClassId(),
                allAttributes.getCapacity(),
                allAttributes.getCampus(),
                allAttributes.getTeacherId(),
                allAttributes.getCourseId()};
        int result = jdbcTemplate.update(sql, args);
        if (result == 0) {
            return Result.fail("Args: " + Arrays.toString(args));
        }
        return Result.ok();
    }

    /**
     * 管理员新增老师信息
     *
     * @param teacherId
     * @param teacherName
     * @param departmentName
     * @return
     */
    @PostMapping("adminInsertTeacher")
    public Result adminInsertTeacher(@RequestBody AllAttributes allAttributes) {
        String sql = "insert into teacher (                                         " +
                "teacherId,                                                         " +
                "teacherName,                                                       " +
                "departmentName                                                     " +
                ") values (?, ?, ?);                                                ";
        Object[] args = new Object[]{
                allAttributes.getTeacherId(),
                allAttributes.getTeacherName(),
                allAttributes.getDepartmentName()};
        int result = jdbcTemplate.update(sql, args);
        if (result == 0) {
            return Result.fail("Args: " + Arrays.toString(args));
        }
        return Result.ok();
    }

    /**
     * 管理员删除教师信息
     *
     * @param teacherId
     * @return
     */
    @PostMapping("adminDeleteTeacher")
    public Result adminDeleteTeacher(@RequestBody AllAttributes allAttributes) {
        String sql = "delete from teacher                                           " +
                "where teacherId=?;                                                 ";
        Object[] args = new Object[]{allAttributes.getTeacherId()};
        int result = jdbcTemplate.update(sql, args);
        if (result == 0) {
            return Result.fail("Args: " + Arrays.toString(args));
        }
        return Result.ok();
    }


    /**
     * 管理员新增课程安排信息
     *
     * @param weekNum
     * @param week
     * @param timeQuantum
     * @param classroom
     * @param classId
     * @return
     */
    @PostMapping("adminInsertSchedule")
    public Result adminInsertSchedule(@RequestBody AllAttributes allAttributes) {
        String sql = "insert into schedule (                                        " +
                "weekNum,                                                           " +
                "week,                                                              " +
                "timeQuantum,                                                       " +
                "classroom,                                                         " +
                "classId                                                            " +
                ") values (?, ?, ?, ?, ?);                                          ";
        Object[] args = new Object[]{
                allAttributes.getWeekNum(),
                allAttributes.getWeek(),
                allAttributes.getTimeQuantum(),
                allAttributes.getClassroom(),
                allAttributes.getClassId()};
        int result = jdbcTemplate.update(sql, args);
        if (result == 0) {
            return Result.fail("Args: " + Arrays.toString(args));
        }
        return Result.ok();
    }
}

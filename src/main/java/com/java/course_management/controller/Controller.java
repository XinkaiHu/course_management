package com.java.course_management.controller;

import com.java.course_management.model.Result;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;


@RestController
@CrossOrigin(origins = "*")
public class Controller {
    @Resource
    JdbcTemplate jdbcTemplate;

    /**
     * 原 login
     * @param studentId
     * @param password
     * @return
     */
    @GetMapping("login")
    public Result login(
            String studentId,
            String password) {
        String sql = "select student_id from student where student_id=? and password=?;";
        Object[] args = new Object[]{studentId, password};
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, args);
        if (result.isEmpty()) {
            return Result.fail("Args: " + Arrays.toString(args));
        }
        return Result.ok();
    }

    /**
     * 原 getCourseByKeyword
     * @param keyword
     * @return
     */
    @GetMapping("getCourseByKeyword")
    public Result searchCourseByKeyword(
            String keyword) {
        String sql = "select * from requirement where course_id like concat('%', ?, '%') or course_name like concat('%', ?, '%') order by semester;";
        Object[] args = new Object[]{keyword, keyword};
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, args);
        return Result.ok(result);
    }

    @GetMapping("getCourse")
    public Result getCourse(
            String studentId) {
        String sql = "select * from student natural join requirement where student_id=?;";
        Object[] args = new Object[]{studentId};
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, args);
        return Result.ok(result);
    }

    @GetMapping("getClassByCourse")
    public Result getClassByCourse(
            String courseId) {
        String sql = "select * from class where course_id=?;";
        Object[] args = new Object[]{courseId};
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, args);
        return Result.ok(result);
    }

    @GetMapping("getSchedule")
    public Result getSchedule(
            String studentId,
            int weekNum) {
        return Result.ok();
    }

    @GetMapping("getReport")
    public Result getReport(
            String studentId) {
        String sql = "select * from report where student_id=?;";
        Object[] args = new Object[]{studentId};
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, args);
        return Result.ok(result);
    }

    @GetMapping("getWeightedAverageScore")
    public Result getWeightedAverageScore(String studentId) {
        String sql = "select sum(credit) from requirement natural join class natural join report where student_id=? and score is not null;";
        Object[] args = new Object[]{studentId};
        Double sumOfCredit = jdbcTemplate.queryForObject(sql, Double.class, args);
        if (sumOfCredit == null) {
            return Result.ok();
        }
        sql = "select sum(credit * score) from requirement natural join class natural join report where student_id=? and score is not null;";
        Double sumOfWeightedScore = jdbcTemplate.queryForObject(sql, double.class, args);
        if (sumOfWeightedScore == null) {
            return Result.fail("Args: " + Arrays.toString(args));
        }
        Double weightedAverageScore = sumOfWeightedScore / sumOfCredit;
        return Result.ok(weightedAverageScore);
    }

    @GetMapping("getGPA")
    public Result getGPA(String studentId) {
        String sql = "select sum(credit) from requirement natural join class natural join report where student_id=? and score is not null;";
        Object[] args = new Object[] {studentId};
        Double sumOfCredit = jdbcTemplate.queryForObject(sql, Double.class, args);
        if (sumOfCredit == null) {
            return Result.ok();
        }
        sql = "select sum(credit) from requirement natural join class natural join report where student_id=? and score is not null and score>=90;";
        Double sumOfCreditGT90 = jdbcTemplate.queryForObject(sql, Double.class, args);
        sql = "select sum(credit * score) from requirement natural join class natural join report where student_id=? and score is not null and score<90;";
        Double sumOfWeightedScoreLT90 = jdbcTemplate.queryForObject(sql, Double.class, args);
        double sumOfWeightedScore = 0.0;
        if (sumOfCreditGT90 != null) {
            sumOfWeightedScore += 90 * sumOfCreditGT90;
        }
        if (sumOfWeightedScoreLT90 != null) {
            sumOfWeightedScore += sumOfWeightedScoreLT90;
        }
        Double GPA = sumOfWeightedScore / sumOfCredit;
        return Result.ok(GPA);
    }

    @GetMapping("getBlog")
    public Result getBlog() {
        String sql = "select * from blog;";
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
        return Result.ok(result);
    }

    @PostMapping("register")
    public Result register(
            @RequestParam String studentId,
            @RequestParam String studentName,
            @RequestParam String password) {
        String sql = "update student set password=? where student_id=? and student_name=?;";
        Object[] args = new Object[]{password, studentId, studentName};
        int result = jdbcTemplate.update(sql, args);
        if (result == 1) {
            return Result.ok();
        }
        return Result.fail("Args: " + Arrays.toString(args));
    }

    @PostMapping("takeClass")
    public Result takeClass(
            @RequestParam String studentId,
            @RequestParam String classId) {
        String sql = "insert into report (student_id, class_id) values (?, ?);";
        Object[] args = new Object[]{studentId, classId};
        int result = jdbcTemplate.update(sql, args);
        if (result == 1) {
            return Result.ok();
        }
        return Result.fail("Args: " + Arrays.toString(args));
    }

    @PostMapping("dropCourse")
    public Result dropCourse(
            @RequestParam String studentId,
            @RequestParam String courseId) {
        String sql = "delete from report where student_id=? and class_id in (select class_id from class where course_id=?);";
        Object[] args = new Object[]{studentId, courseId};
        int result = jdbcTemplate.update(sql, args);
        if (result == 1) {
            return Result.ok();
        }
        return Result.fail("Args: " + Arrays.toString(args));
    }

    @PostMapping("dropClass")
    public Result dropClass(
            @RequestParam String studentId,
            @RequestParam String classId) {
        String sql = "delete from report where student_id=? and class_id=?;";
        Object[] args = new Object[]{studentId, classId};
        int result = jdbcTemplate.update(sql, args);
        if (result == 1) {
            return Result.ok();
        }
        return Result.fail("Args: " + Arrays.toString(args));
    }

    @PostMapping("updateScore")
    public Result updateScore(
            @RequestParam String studentId,
            @RequestParam String classId,
            @RequestParam long score) {
        String sql = "update report set score=? where student_id=? and class_id=?;";
        Object[] args = new Object[]{score, studentId, classId};
        int result = jdbcTemplate.update(sql, args);
        if (result == 1) {
            return Result.ok();
        }
        return Result.fail("Args: " + Arrays.toString(args));
    }

    @PostMapping("shareBlog")
    public Result shareBlog(
            @RequestParam String commentTime,
            @RequestParam String comment,
            @RequestParam String value,
            @RequestParam String studentId,
            @RequestParam String courseName,
            @RequestParam String teacherName) {
        String sql = "insert into blog (comment_time, comment, value, student_id, course_name, teacher_name) values (?, ?, ?, ?, ?, ?);";
        Object[] args = new Object[]{commentTime, comment, value, studentId, courseName, teacherName};
        int result = jdbcTemplate.update(sql, args);
        if (result == 1) {
            return Result.ok();
        }
        return Result.fail("Args: " + Arrays.toString(args));
    }
}

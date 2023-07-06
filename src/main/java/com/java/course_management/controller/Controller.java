package com.java.course_management.controller;

import com.java.course_management.model.AllAttributes;
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
     * login 原 login
     *
     * @param studentId
     * @param password
     * @return
     */
    @GetMapping("login")
    public Result login(
            String studentId,
            String password) {
        String sql = "select * from student where studentId=? and password=?;";
        Object[] args = new Object[]{studentId, password};
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, args);
        if (result.isEmpty()) {
            return Result.fail("Args: " + Arrays.toString(args));
        }
        return Result.ok(result);
    }

    /**
     * getCourseByKeyword 原 getCourseByKeyword
     *
     * @param keyword
     * @return
     */
    @GetMapping("getCourseByKeyword")
    public Result searchCourseByKeyword(
            String studentId,
            String keyword) {
        if (keyword == null) {
            keyword = "";
        }
        String sql = "select * from requirement where (courseId like concat('%', ?, '%') or courseName like concat('%', ?, '%')) and majorName in (select majorName from student where studentId=?) order by semester;";
        Object[] args = new Object[]{keyword, keyword, studentId};
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, args);
        return Result.ok(result);
    }

    /**
     * getCourse 原 course/selectByIdOrName
     *
     * @param studentId
     * @return
     */
    @GetMapping("getCourse")
    public Result getCourse(
            String studentId) {
        String sql = "select * from student natural join requirement where studentId=?;";
        Object[] args = new Object[]{studentId};
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, args);
        return Result.ok(result);
    }

    /**
     * getClassByCourse 原 getCourseInfo
     *
     * @param courseId
     * @return
     */
    @GetMapping("getClassByCourse")
    public Result getClassByCourse(
            String courseId) {
        String sql = "select distinct classId, teacherName, campus from class natural join teacher where courseId=?;";
        Object[] args = new Object[]{courseId};
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, args);
        return Result.ok(result);
    }

    /**
     * getSchedule 原 scheduleByWeekNumAndSemester
     *
     * @param studentId
     * @param weekNum
     * @return
     */
    @GetMapping("getSchedule")
    public Result getSchedule(
            String studentId,
            int weekNum) {
        List<String> sqlList = new ArrayList<>();
        sqlList.add("select courseName from requirement where courseId=(select courseId from schedule natural join class natural join report where studentId=? and week=? and weekNum=? and timeQuantum=? and score is null) and majorName=(select majorName from student where studentId=?);");
        sqlList.add("select classroom from schedule natural join class natural join report where studentId=? and week=? and weekNum=? and timeQuantum=? and score is null;");
        sqlList.add("select teacherName from schedule natural join class natural join report natural join teacher where studentId=? and week=? and weekNum=? and timeQuantum=? and score is null;");
        sqlList.add("select courseId from schedule natural join class natural join report where studentId=? and week=? and weekNum=? and timeQuantum=? and score is null;");
        List<Map<String, List<String>>> result = new ArrayList<>();
        for (int week = 1; week <= 7; ++week) {
            Map<String, List<String>> map = new HashMap<>();
            map.put("courseName", new ArrayList<>());
            map.put("classroom", new ArrayList<>());
            map.put("teacherName", new ArrayList<>());
            map.put("courseId", new ArrayList<>());
            for (int timeQuantum = 1; timeQuantum <= 5; ++timeQuantum) {
                try {
                    map.get("courseName").add(jdbcTemplate.queryForObject(sqlList.get(0), String.class, studentId, week, weekNum, timeQuantum, studentId));
                } catch (Exception e) {
                    map.get("courseName").add("");
                }
                try {
                    map.get("classroom").add(jdbcTemplate.queryForObject(sqlList.get(1), String.class, studentId, week, weekNum, timeQuantum));
                } catch (Exception e) {
                    map.get("classroom").add("");
                }
                try {
                    map.get("teacherName").add(jdbcTemplate.queryForObject(sqlList.get(2), String.class, studentId, week, weekNum, timeQuantum));
                } catch (Exception e) {
                    map.get("teacherName").add("");
                }
                try {
                    map.get("courseId").add(jdbcTemplate.queryForObject(sqlList.get(3), String.class, studentId, week, weekNum, timeQuantum));
                } catch (Exception e) {
                    map.get("courseId").add("");
                }
            }
            result.add(map);
        }
        return Result.ok(result);
    }

    /**
     * getReport 原 courseScore
     *
     * @param studentId
     * @return
     */
    @GetMapping("getReport")
    public Result getReport(
            String studentId) {
        String sql = "select * from report natural join class natural join requirement natural join student where studentId=? and score is not null;";
        Object[] args = new Object[]{studentId};
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, args);
        return Result.ok(result);
    }

    /**
     * getWeightedAverageScore 原 weighedAverageScore
     *
     * @param studentId
     * @return
     */
    @GetMapping("getWeightedAverageScore")
    public Result getWeightedAverageScore(String studentId) {
        String sql = "select sum(credit) from requirement natural join class natural join report where studentId=? and score is not null;";
        Object[] args = new Object[]{studentId};
        Double sumOfCredit = jdbcTemplate.queryForObject(sql, Double.class, args);
        if (sumOfCredit == null) {
            return Result.ok();
        }
        sql = "select sum(credit * score) from requirement natural join class natural join report where studentId=? and score is not null;";
        Double sumOfWeightedScore = jdbcTemplate.queryForObject(sql, double.class, args);
        if (sumOfWeightedScore == null) {
            return Result.fail("Args: " + Arrays.toString(args));
        }
        System.out.println(sumOfWeightedScore);
        System.out.println(sumOfCredit);
        Double weightedAverageScore = sumOfWeightedScore / sumOfCredit;
        String result = weightedAverageScore.toString();
        if (result.length() > 7) {
            result = result.substring(0, 7);
        }
        return Result.ok(result);
    }

    /**
     * getGPA 原 weightedAverageGPA
     *
     * @param studentId
     * @return
     */
    @GetMapping("getGPA")
    public Result getGPA(String studentId) {
        String sql = "select sum(credit) from requirement natural join class natural join report where studentId=? and score is not null;";
        Object[] args = new Object[]{studentId};
        Double sumOfCredit = jdbcTemplate.queryForObject(sql, Double.class, args);
        if (sumOfCredit == null) {
            return Result.ok();
        }
        sql = "select sum(credit) from requirement natural join class natural join report where studentId=? and score is not null and score>=90;";
        Double sumOfCreditGT90 = jdbcTemplate.queryForObject(sql, Double.class, args);
        sql = "select sum(credit * score) from requirement natural join class natural join report where studentId=? and score is not null and score<90;";
        Double sumOfWeightedScoreLT90 = jdbcTemplate.queryForObject(sql, Double.class, args);
        double sumOfWeightedScore = 0.0;
        if (sumOfCreditGT90 != null) {
            sumOfWeightedScore += 90 * sumOfCreditGT90;
        }
        if (sumOfWeightedScoreLT90 != null) {
            sumOfWeightedScore += sumOfWeightedScoreLT90;
        }
        System.out.println(sumOfWeightedScore);
        System.out.println(sumOfCredit);
        Double GPA = 0.1 * (sumOfWeightedScore - 50 * sumOfCredit) / sumOfCredit;
        String result = GPA.toString();
        if (result.length() > 6) {
            result = result.substring(0, 6);
        }
        return Result.ok(result);
    }

    /**
     * getBlog 原 getBlog
     *
     * @return
     */
    @GetMapping("getBlog")
    public Result getBlog() {
        String sql = "select * from blog join student on blog.studentId=student.studentId order by commentTime desc;";
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
        return Result.ok(result);
    }

    /**
     * register 原 register
     *
     * @param studentId
     * @param studentName
     * @param password
     * @return
     */
    @PostMapping("register")
    public Result register(@RequestBody AllAttributes allAttributes) {
        String sql = "update student set password=? where studentId=? and studentName=?;";
        Object[] args = new Object[]{
                allAttributes.getPassword(),
                allAttributes.getStudentId(),
                allAttributes.getStudentName()};
        int result = jdbcTemplate.update(sql, args);
        if (result == 1) {
            return Result.ok();
        }
        return Result.fail("Args: " + Arrays.toString(args));
    }

    /**
     * takeClass 选教学班
     *
     * @param studentId
     * @param classId
     * @return
     */
    @PostMapping("takeClass")
    public Result takeClass(@RequestBody AllAttributes allAttributes) {
        String sql = "select * from report natural join class where studentId=? and courseId in (select courseId from class where classId=?);";
        Object[] args = new Object[] {
                allAttributes.getStudentId(),
                allAttributes.getClassId()};
        if (!jdbcTemplate.queryForList(sql, args).isEmpty()) {
            return Result.fail("Args: " + Arrays.toString(args));
        }
        sql = "insert into report (studentId, classId) values (?, ?);";
        args = new Object[]{
                allAttributes.getStudentId(),
                allAttributes.getClassId()};
        int result = jdbcTemplate.update(sql, args);
        if (result == 1) {
            return Result.ok();
        }
        return Result.fail("Args: " + Arrays.toString(args));
    }

    /**
     * dropCourse 通过课程编号退课 原 course/deleteById
     *
     * @param studentId
     * @param courseId
     * @return
     */
    @PostMapping("dropCourse")
    public Result dropCourse(@RequestBody AllAttributes allAttributes) {
        String sql = "delete from report where studentId=? and classId in (select classId from class where courseId=?);";
        Object[] args = new Object[]{
                allAttributes.getStudentId(),
                allAttributes.getCourseId()};
        int result = jdbcTemplate.update(sql, args);
        if (result == 1) {
            return Result.ok();
        }
        return Result.fail("Args: " + Arrays.toString(args));
    }

    /**
     * dropClass 通过教学班号退课
     *
     * @param studentId
     * @param classId
     * @return
     */
    @PostMapping("dropClass")
    public Result dropClass(@RequestBody AllAttributes allAttributes) {
        String sql = "delete from report where studentId=? and classId=?;";
        Object[] args = new Object[]{
                allAttributes.getStudentId(),
                allAttributes.getClassId()};
        int result = jdbcTemplate.update(sql, args);
        if (result == 1) {
            return Result.ok();
        }
        return Result.fail("Args: " + Arrays.toString(args));
    }

    /**
     * updateScore 原 course/update
     *
     * @param studentId
     * @param classId
     * @param score
     * @return
     */
    @PostMapping("updateScore")
    public Result updateScore(@RequestBody AllAttributes allAttributes) {
        String sql = "update report set score=? where studentId=? and classId=?;";
        Object[] args = new Object[]{
                allAttributes.getScore(),
                allAttributes.getStudentId(),
                allAttributes.getClassId()};
        System.out.println(Arrays.toString(args));
        int result = jdbcTemplate.update(sql, args);
        if (result == 1) {
            return Result.ok();
        }
        return Result.fail("Args: " + Arrays.toString(args));
    }

    /**
     * shareBlog 原 shareBlog
     *
     * @param commentTime
     * @param comment
     * @param value
     * @param studentId
     * @param courseName
     * @param teacherName
     * @return
     */
    @PostMapping("shareBlog")
    public Result shareBlog(@RequestBody AllAttributes allAttributes) {
        String sql = "insert into blog (commentTime, comment, value, studentId, courseName, teacherName) values (?, ?, ?, ?, ?, ?);";
        Object[] args = new Object[]{
                allAttributes.getCommentTime(),
                allAttributes.getComment(),
                allAttributes.getValue(),
                allAttributes.getStudentId(),
                allAttributes.getCourseName(),
                allAttributes.getTeacherName()};
        int result = jdbcTemplate.update(sql, args);
        if (result == 1) {
            return Result.ok();
        }
        return Result.fail("Args: " + Arrays.toString(args));
    }

    @PostMapping("updateLike")
    public Result updateLike(@RequestBody AllAttributes allAttributes) {
        String sql = "update blog set good=? where commentTime=?;";
        Object[] args = new Object[] {allAttributes.getGood(), allAttributes.getCommentTime()};
        int result = jdbcTemplate.update(sql, args);
        if (result == 1) {
            return Result.ok();
        }
        return Result.fail("Args: " + Arrays.toString(args));
    }
}

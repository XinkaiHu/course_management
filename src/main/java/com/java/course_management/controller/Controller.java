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
     * 登录学生账号
     * 根据学生学号和密码与数据库中的密码进行对比，判断是否成功登录
     *
     * @param studentId 学生学号
     * @param password  学生登录密码
     * @return
     */
    @GetMapping("login")
    public Result login(
            String studentId,
            String password) {
        String sql = "select *                                                      " +
                "from student                                                       " +
                "where studentId=?                                                  " +
                "       and password=?;                                             ";
        Object[] args = new Object[]{studentId, password};
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, args);
        if (result.isEmpty()) {
            return Result.fail("Args: " + Arrays.toString(args));
        }
        return Result.ok(result);
    }

    /**
     * 查询选课信息
     * 根据学生学号和用户在搜索框中输入的关键词，查找与关键词相关的课程信息
     *
     * @param studentId 学生学号
     * @param keyword 搜索框关键词
     * @return
     */
    @GetMapping("getCourseByKeyword")
    public Result searchCourseByKeyword(
            String studentId,
            String keyword) {
        if (keyword == null) {
            keyword = "";
        }
        String sql = "select *                                                      " +
                "from requirement                                                   " +
                "where (                                                            " +
                "       courseId like concat('%', ?, '%')                           " +
                "       or courseName like concat('%', ?, '%'))                     " +
                "and majorName in (                                                 " +
                "       select majorName                                            " +
                "       from student                                                " +
                "       where studentId=?)                                          " +
                "and courseId not in (                                              " +
                "       select courseId                                             " +
                "       from report natural join class                              " +
                "       where studentId=?                                           " +
                "               and score is not null)                              " +
                "order by semester;                                                 ";
        Object[] args = new Object[]{
                keyword,
                keyword,
                studentId,
                studentId};
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, args);
        return Result.ok(result);
    }

    /**
     * 查询所选课程的班级信息
     *
     * @param studentId 学生学号
     * @param courseId 课程编号
     * @return
     */
    @GetMapping("getClassByCourse")
    public Result getClassByCourse(
            String studentId,
            String courseId) {
        String sql = "select distinct                                               " +
                "       class.classId,                                              " +
                "       teacher.teacherName,                                        " +
                "       teacher.teacherId,                                          " +
                "       class.campus,                                               " +
                "       class.capacity,                                             " +
                "       (class.classId in (                                         " +
                "               select report.classId                               " +
                "               from report                                         " +
                "               where studentId=?))                                 " +
                "       as taken, (                                                 " +
                "               select count(studentId)                             " +
                "               from report                                         " +
                "                       natural join class as c                     " +
                "               where c.classId=class.classId)                      " +
                "       as currentTake                                              " +
                "from class                                                         " +
                "       natural join teacher                                        " +
                "where courseId=?;                                                  ";
        Object[] args = new Object[]{studentId, courseId};
        System.out.println("Args: " + Arrays.toString(args));
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, args);
        return Result.ok(result);
    }

    /**
     * 查询课表
     *
     * @param studentId 学生学号
     * @param weekNum 上课周数
     * @return
     */
    @GetMapping("getSchedule")
    public Result getSchedule(
            String studentId,
            int weekNum) {
        String sqlCourseName = "select courseName                                   " +
                "from requirement                                                   " +
                "where courseId=(                                                   " +
                "       select courseId                                             " +
                "       from schedule                                               " +
                "               natural join class                                  " +
                "               natural join report                                 " +
                "       where studentId=?                                           " +
                "               and week=?                                          " +
                "               and weekNum=?                                       " +
                "               and timeQuantum=?                                   " +
                "               and score is null)                                  " +
                "       and majorName=(                                             " +
                "               select majorName                                    " +
                "               from student                                        " +
                "               where studentId=?);                                 ";

        String sqlClassroom = "select classroom                                     " +
                "from schedule                                                      " +
                "       natural join class                                          " +
                "       natural join report                                         " +
                "where studentId=?                                                  " +
                "       and week=?                                                  " +
                "       and weekNum=?                                               " +
                "       and timeQuantum=?                                           " +
                "       and score is null;                                          ";

        String sqlTeacherName = "select teacherName                                 " +
                "from schedule                                                      " +
                "       natural join class                                          " +
                "       natural join report                                         " +
                "       natural join teacher                                        " +
                "where studentId=?                                                  " +
                "       and week=?                                                  " +
                "       and weekNum=?                                               " +
                "       and timeQuantum=?                                           " +
                "       and score is null;                                          ";

        String sqlTeacherId = "select teacherId                                     " +
                "from schedule                                                      " +
                "       natural join class                                          " +
                "       natural join report                                         " +
                "       natural join teacher                                        " +
                "where studentId=?                                                  " +
                "       and week=?                                                  " +
                "       and weekNum=?                                               " +
                "       and timeQuantum=?                                           " +
                "       and score is null;                                          ";

        String sqlCourseId = "select courseId                                       " +
                "from schedule                                                      " +
                "       natural join class                                          " +
                "       natural join report                                         " +
                "where studentId=?                                                  " +
                "       and week=?                                                  " +
                "       and weekNum=?                                               " +
                "       and timeQuantum=?                                           " +
                "       and score is null;                                          ";

        List<Map<String, List<String>>> result = new ArrayList<>();
        for (int week = 1; week <= 7; ++week) {
            Map<String, List<String>> map = new HashMap<>();
            map.put("courseName", new ArrayList<>());
            map.put("classroom", new ArrayList<>());
            map.put("teacherName", new ArrayList<>());
            map.put("teacherId", new ArrayList<>());
            map.put("courseId", new ArrayList<>());
            for (int timeQuantum = 1; timeQuantum <= 5; ++timeQuantum) {
                try {
                    map.get("courseName").add(jdbcTemplate.queryForObject(sqlCourseName, String.class,
                            studentId, week, weekNum, timeQuantum, studentId));
                } catch (Exception e) {
                    map.get("courseName").add("");
                }
                try {
                    map.get("classroom").add(jdbcTemplate.queryForObject(sqlClassroom, String.class,
                            studentId, week, weekNum, timeQuantum));
                } catch (Exception e) {
                    map.get("classroom").add("");
                }
                try {
                    map.get("teacherName").add(jdbcTemplate.queryForObject(sqlTeacherName, String.class,
                            studentId, week, weekNum, timeQuantum));
                } catch (Exception e) {
                    map.get("teacherName").add("");
                }
                try {
                    map.get("teacherId").add(jdbcTemplate.queryForObject(sqlTeacherId, String.class,
                            studentId, week, weekNum, timeQuantum));
                } catch (Exception e) {
                    map.get("teacherId").add("");
                }
                try {
                    map.get("courseId").add(jdbcTemplate.queryForObject(sqlCourseId, String.class,
                            studentId, week, weekNum, timeQuantum));
                } catch (Exception e) {
                    map.get("courseId").add("");
                }
            }
            result.add(map);
        }
        return Result.ok(result);
    }

    /**
     * 查询成绩单
     *
     * @param studentId 学生学号
     * @return
     */
    @GetMapping("getReport")
    public Result getReport(
            String studentId) {
        String sql = "select *                                                      " +
                "from report                                                        " +
                "       natural join class                                          " +
                "       natural join requirement                                    " +
                "       natural join student                                        " +
                "where studentId=?                                                  " +
                "       and score is not null;                                      ";
        Object[] args = new Object[]{studentId};
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, args);
        return Result.ok(result);
    }

    /**
     * 查询平均分
     *
     * @param studentId 学生学号
     * @return
     */
    @GetMapping("getWeightedAverageScore")
    public Result getWeightedAverageScore(String studentId) {
        String sql = "select sum(credit)                                            " +
                "from requirement                                                   " +
                "       natural join class                                          " +
                "       natural join report                                         " +
                "       natural join student                                        " +
                "where studentId=?                                                  " +
                "       and score is not null;                                      ";
        Object[] args = new Object[]{studentId};
        Double sumOfCredit = jdbcTemplate.queryForObject(sql, Double.class, args);
        if (sumOfCredit == null) {
            return Result.ok();
        }
        sql = "select sum(credit * score)                                           " +
                "from requirement                                                   " +
                "       natural join class                                          " +
                "       natural join report                                         " +
                "       natural join student                                        " +
                "where studentId=?                                                  " +
                "       and score is not null;                                      ";
        Double sumOfWeightedScore = jdbcTemplate.queryForObject(sql, double.class, args);
        if (sumOfWeightedScore == null) {
            return Result.fail("Args: " + Arrays.toString(args));
        }
        System.out.println(sumOfWeightedScore);
        System.out.println(sumOfCredit);
        double weightedAverageScore = sumOfWeightedScore / sumOfCredit;
        String result = String.format("%.4f", weightedAverageScore);
        return Result.ok(result);
    }

    /**
     * 查询已选课程总学分
     *
     * @param studentId 学生学号
     * @return
     */
    @GetMapping("getTotalCredit")
    public Result getTotalCredit(String studentId) {
        String sql = "select sum(credit)                                            " +
                "from class                                                         " +
                "       natural join report                                         " +
                "       natural join requirement                                    " +
                "       natural join student                                        " +
                "where studentId=?                                                  " +
                "       and score is not null;                                      ";
        Object[] args = new Object[]{studentId};
        Double totalCredit = jdbcTemplate.queryForObject(sql, double.class, args);
        if (totalCredit == null) {
            return Result.fail("Args: " + Arrays.toString(args));
        }
        return Result.ok(totalCredit);
    }

    /**
     * 查询绩点
     *
     * @param studentId 学生学号
     * @return
     */
    @GetMapping("getGPA")
    public Result getGPA(String studentId) {
        String sql = "select sum(credit)                                            " +
                "from requirement                                                   " +
                "       natural join class                                          " +
                "       natural join report                                         " +
                "       natural join student                                        " +
                "where studentId=?                                                  " +
                "       and score is not null;                                      ";
        Object[] args = new Object[]{studentId};
        Double sumOfCredit = jdbcTemplate.queryForObject(sql, Double.class, args);
        if (sumOfCredit == null) {
            return Result.ok();
        }
        sql = "select sum(credit)                                                   " +
                "from requirement                                                   " +
                "       natural join class                                          " +
                "       natural join report                                         " +
                "       natural join student                                        " +
                "where studentId=?                                                  " +
                "       and score is not null                                       " +
                "       and score>=90;                                              ";
        Double sumOfCreditGT90 = jdbcTemplate.queryForObject(sql, Double.class, args);
        sql = "select sum(credit * score)                                           " +
                "from requirement                                                   " +
                "       natural join class                                          " +
                "       natural join report                                         " +
                "       natural join student                                        " +
                "where studentId=?                                                  " +
                "       and score is not null                                       " +
                "       and score<90;                                               ";
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
        double GPA = 0.1 * (sumOfWeightedScore - 50 * sumOfCredit) / sumOfCredit;
        String result = String.format("%.4f", GPA);
        return Result.ok(result);
    }

    /**
     * 查询学生已选课程
     *
     * @param studentId
     * @return
     */
    @GetMapping("getSelectedCourse")
    public Result getSelectedCourse(String studentId) {
        String sql = "select distinct *                                             " +
                "from student                                                       " +
                "       natural join report                                         " +
                "       natural join class                                          " +
                "       natural join requirement                                    " +
                "where studentId=?;                                                 ";
        Object[] args = new Object[]{studentId};
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, args);
        return Result.ok(result);
    }

    /**
     * 查询学生论坛信息
     *
     * @return
     */
    @GetMapping("getBlog")
    public Result getBlog(String studentId) {
        String sql = "" +
                "select *, (                                                        " +
                "       select count(1)                                             " +
                "       from thumb                                                  " +
                "       where thumb.commentTime=blog.commentTime                    " +
                ") as goodNum, (                                                    " +
                "       select count(1)                                             " +
                "       from thumb                                                  " +
                "       where thumb.studentId=?                                     " +
                "               and blog.commentTime=thumb.commentTime              " +
                ") as isGood                                                        " +
                "from blog join student                                             " +
                "       on blog.studentId=student.studentId                         " +
                "order by commentTime desc;                                         ";
        Object[] args = new Object[]{studentId};
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, args);
        return Result.ok(result);
    }

    @GetMapping("getBlogByTeacher")
    public Result getBlogByTeacher(String teacherName) {
        String sql = "select * from blog where teacherName=?;";
        Object[] args = new Object[]{teacherName};
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
        return Result.ok(result);
    }

    @GetMapping("getBlogByCourse")
    public Result getBlogByCourse(String courseName) {
        String sql = "select * from blog where courseName=?;";
        Object[] args = new Object[]{courseName};
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
        return Result.ok(result);
    }

    /**
     * 学生用户注册
     *
     * @param studentId
     * @param studentName
     * @param password
     * @return
     */
    @PostMapping("register")
    public Result register(@RequestBody AllAttributes allAttributes) {
        String sql = "update student                                                " +
                "set password=?                                                     " +
                "where studentId=?                                                  " +
                "       and studentName=?;                                          ";
        Object[] args = new Object[]{
                allAttributes.getPassword(),
                allAttributes.getStudentId(),
                allAttributes.getStudentName()};
        int result = jdbcTemplate.update(sql, args);
        if (result == 0) {
            return Result.fail("Args: " + Arrays.toString(args));
        }
        return Result.ok();
    }

    /**
     * 选教学班
     *
     * @param studentId
     * @param classId
     * @return
     */
    @PostMapping("takeClass")
    public Result takeClass(@RequestBody AllAttributes allAttributes) {
        String sql = "select *                                                      " +
                "from report                                                        " +
                "       natural join class                                          " +
                "where studentId=?                                                  " +
                "       and courseId in (                                           " +
                "               select courseId                                     " +
                "               from class                                          " +
                "               where classId=?);                                   ";
        Object[] args = new Object[]{
                allAttributes.getStudentId(),
                allAttributes.getClassId()};
        if (!jdbcTemplate.queryForList(sql, args).isEmpty()) {
            return Result.fail("Args: " + Arrays.toString(args));
        }
        sql = "insert into report                                                   " +
                "(studentId, classId)                                               " +
                "values (?, ?);                                                     ";
        args = new Object[]{
                allAttributes.getStudentId(),
                allAttributes.getClassId()};
        int result = jdbcTemplate.update(sql, args);
        if (result == 0) {
            return Result.fail("Args: " + Arrays.toString(args));
        }
        return Result.ok();
    }

    /**
     * 通过课程编号退课
     *
     * @param studentId
     * @param courseId
     * @return
     */
    @PostMapping("dropCourse")
    public Result dropCourse(@RequestBody AllAttributes allAttributes) {
        String sql = "delete from report                                            " +
                "where studentId=?                                                  " +
                "       and classId in (                                            " +
                "               select classId                                      " +
                "               from class                                          " +
                "               where courseId=?);                                  ";
        Object[] args = new Object[]{
                allAttributes.getStudentId(),
                allAttributes.getCourseId()};
        int result = jdbcTemplate.update(sql, args);
        if (result == 0) {
            return Result.fail("Args: " + Arrays.toString(args));
        }
        return Result.ok();
    }

    /**
     * 通过教学班号退课
     *
     * @param studentId
     * @param classId
     * @return
     */
    @PostMapping("dropClass")
    public Result dropClass(@RequestBody AllAttributes allAttributes) {
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
     * 更新学生成绩
     *
     * @param studentId
     * @param classId
     * @param score
     * @return
     */
    @PostMapping("updateScore")
    public Result updateScore(@RequestBody AllAttributes allAttributes) {
        String sql = "update report                                                 " +
                "set score=?                                                        " +
                "where studentId=?                                                  " +
                "       and classId=?;                                              ";
        Object[] args = new Object[]{
                allAttributes.getScore(),
                allAttributes.getStudentId(),
                allAttributes.getClassId()};
        int result = jdbcTemplate.update(sql, args);
        if (result == 0) {
            return Result.fail("Args: " + Arrays.toString(args));
        }
        return Result.ok();
    }

    /**
     * 在学生论坛发布博客
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
        String sql = "insert into blog (                                            " +
                "       commentTime,                                                " +
                "       comment,                                                    " +
                "       value,                                                      " +
                "       studentId,                                                  " +
                "       courseName,                                                 " +
                "       teacherName                                                 " +
                ") values (?, ?, ?, ?, ?, ?);                                       ";
        Object[] args = new Object[]{
                allAttributes.getCommentTime(),
                allAttributes.getComment(),
                allAttributes.getValue(),
                allAttributes.getStudentId(),
                allAttributes.getCourseName(),
                allAttributes.getTeacherName()};
        int result = jdbcTemplate.update(sql, args);
        if (result == 0) {
            return Result.fail("Args: " + Arrays.toString(args));
        }
        return Result.ok();
    }

    /**
     * 删除博客
     *
     * @param commentTime
     * @return
     */
    @PostMapping("deleteBlog")
    public Result deleteBlog(@RequestBody AllAttributes allAttributes) {
        String sql = "delete from thumb where commentTime=?;";
        Object[] args = new Object[]{allAttributes.getCommentTime()};
        int result;
        jdbcTemplate.update(sql, args);
        sql = "delete from blog where commentTime=?;";
        result = jdbcTemplate.update(sql, args);
        if (result == 0) {
            return Result.fail("Args: " + Arrays.toString(args));
        }
        return Result.ok();
    }

    /**
     * 更新博客点赞数
     *
     * @param studentId
     * @param commentTime
     * @return
     */
    @PostMapping("updateLike")
    public Result updateLike(@RequestBody AllAttributes allAttributes) {
        String sql = "select * from thumb where studentId=? and commentTime=?;";
        Object[] args = new Object[]{
                allAttributes.getStudentId(),
                allAttributes.getCommentTime()};
        System.out.println(Arrays.toString(args));
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, args);
        if (list.isEmpty()) {
            sql = "insert into thumb (studentId, commentTime) values (?, ?);";
            int result = jdbcTemplate.update(sql, args);
            if (result == 0) {
                return Result.fail("Args: " + Arrays.toString(args));
            }
            return Result.ok();
        } else {
            sql = "delete from thumb where studentId=? and commentTime=?;";
            int result = jdbcTemplate.update(sql, args);
            if (result == 0) {
                return Result.fail("Args: " + Arrays.toString(args));
            }
            return Result.ok();
        }
    }
}

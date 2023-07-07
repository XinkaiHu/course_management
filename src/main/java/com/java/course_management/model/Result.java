package com.java.course_management.model;


public class Result {

    /**
     * 是否成功
     */
    private Boolean isSuccess;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 状态码
     */
    private Integer status;

    /**
     * 时间戳
     */
    private Long timestamp;

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    private Object data;


    public Result(Boolean isSuccess, String errorMsg, Integer status, Long timestamp, Object data) {
        this.isSuccess = isSuccess;
        this.errorMsg = errorMsg;
        this.status = status;
        this.timestamp = timestamp;
        this.data = data;
    }

    public static Result ok() {
        return new Result(true, null, 200, System.currentTimeMillis(), null);
    }

    public static Result ok(Object data) {
        return new Result(true, null, 200, System.currentTimeMillis(), data);
    }

    public static Result fail(String errorMsg) {
        return new Result(false, errorMsg, 400, System.currentTimeMillis(), null);
    }

}

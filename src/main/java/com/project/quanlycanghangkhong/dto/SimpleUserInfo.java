package com.project.quanlycanghangkhong.dto;

/**
 * Simplified User Info - Thay thế cho nested UserDTO
 * Chỉ chứa thông tin basic cần thiết, không có nested objects
 */
public class SimpleUserInfo {
    private Integer userId;
    private String userName;
    private String userEmail;
    private String teamName;    // Optional: Có thể null
    private String roleName;    // Optional: Có thể null
    
    // Constructors
    public SimpleUserInfo() {}
    
    public SimpleUserInfo(Integer userId, String userName, String userEmail) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
    }
    
    public SimpleUserInfo(Integer userId, String userName, String userEmail, String teamName, String roleName) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.teamName = teamName;
        this.roleName = roleName;
    }

    // Getters and Setters
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
    
    @Override
    public String toString() {
        return "SimpleUserInfo{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", teamName='" + teamName + '\'' +
                ", roleName='" + roleName + '\'' +
                '}';
    }
}

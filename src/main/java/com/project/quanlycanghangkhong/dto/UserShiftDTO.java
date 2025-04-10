package com.project.quanlycanghangkhong.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.project.quanlycanghangkhong.model.Shift;
import com.project.quanlycanghangkhong.model.User;
import com.project.quanlycanghangkhong.model.UserShift;

public class UserShiftDTO {
	private Integer id;
	private Integer userId;
	private String userName;
	private String shiftCode;
	private String startTime;
	private String endTime;
	private UserDTO user;
	private LocalDate shiftDate;
	private Integer shiftId; // Nếu có ca trực, chỉ lưu id của ca
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public UserShiftDTO() {
	}

	public UserShiftDTO(Integer id, UserDTO user, LocalDate shiftDate, Integer shiftId, LocalDateTime createdAt,
			LocalDateTime updatedAt) {
		this.id = id;
		this.user = user;
		this.shiftDate = shiftDate;
		this.shiftId = shiftId;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public UserShiftDTO(UserShift userShift) {
		this.id = userShift.getId();
		// Lấy thông tin user
		User user = userShift.getUser();
		if (user != null) {
			this.userId = user.getId();
			this.userName = user.getName();
		}
		// Gán trực tiếp LocalDate nếu DTO cũng là LocalDate
		this.shiftDate = userShift.getShiftDate();

		// Lấy thông tin ca trực
		Shift shift = userShift.getShift();
		if (shift != null) {
			this.shiftCode = shift.getShiftCode();
			DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
			this.startTime = shift.getStartTime() != null ? shift.getStartTime().format(timeFormatter) : null;
			this.endTime = shift.getEndTime() != null ? shift.getEndTime().format(timeFormatter) : null;
		}
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public UserDTO getUser() {
		return user;
	}

	public void setUser(UserDTO user) {
		this.user = user;
	}

	public LocalDate getShiftDate() {
		return shiftDate;
	}

	public void setShiftDate(LocalDate shiftDate) {
		this.shiftDate = shiftDate;
	}

	public Integer getShiftId() {
		return shiftId;
	}

	public void setShiftId(Integer shiftId) {
		this.shiftId = shiftId;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

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

	public String getShiftCode() {
		return shiftCode;
	}

	public void setShiftCode(String shiftCode) {
		this.shiftCode = shiftCode;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

}

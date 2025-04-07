package com.project.quanlycanghangkhong.dto;

import java.time.LocalDate;
import java.util.List;

public class ApplyShiftMultiDTO {
	private LocalDate shiftDate;
	private List<Integer> userIds;
	private String shiftCode; // người dùng chọn "Ca Sáng", "Ca Chiều"...

	public LocalDate getShiftDate() {
		return shiftDate;
	}

	public void setShiftDate(LocalDate shiftDate) {
		this.shiftDate = shiftDate;
	}

	public List<Integer> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<Integer> userIds) {
		this.userIds = userIds;
	}

	public String getShiftCode() {
		return shiftCode;
	}

	public void setShiftCode(String shiftCode) {
		this.shiftCode = shiftCode;
	}

}

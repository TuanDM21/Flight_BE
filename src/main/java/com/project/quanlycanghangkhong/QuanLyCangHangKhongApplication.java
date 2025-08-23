package com.project.quanlycanghangkhong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.scheduling.annotation.EnableScheduling;  // ✅ COMMENTED: Import không cần thiết

@SpringBootApplication
// @EnableScheduling  // ✅ COMMENTED: Tắt toàn bộ scheduling
public class QuanLyCangHangKhongApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuanLyCangHangKhongApplication.class, args);
	}
}

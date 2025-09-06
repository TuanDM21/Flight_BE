package com.project.quanlycanghangkhong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;  // ✅ ENABLED: Import cho scheduling

@SpringBootApplication
@EnableScheduling  // ✅ ENABLED: Bật toàn bộ scheduling cho overdue task updates
public class QuanLyCangHangKhongApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuanLyCangHangKhongApplication.class, args);
	}
}

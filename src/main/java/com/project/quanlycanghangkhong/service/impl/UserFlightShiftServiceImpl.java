package com.project.quanlycanghangkhong.service.impl;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.quanlycanghangkhong.dto.ApplyFlightShiftRequest;
import com.project.quanlycanghangkhong.model.Flight;
import com.project.quanlycanghangkhong.model.User;
import com.project.quanlycanghangkhong.model.UserFlightShift;
import com.project.quanlycanghangkhong.repository.FlightRepository;
import com.project.quanlycanghangkhong.repository.UserFlightShiftRepository;
import com.project.quanlycanghangkhong.repository.UserRepository;
import com.project.quanlycanghangkhong.service.UserFlightShiftService;

@Service
public class UserFlightShiftServiceImpl implements UserFlightShiftService {

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserFlightShiftRepository userFlightShiftRepository;
    @Override
    @Transactional
    public void applyFlightShift(ApplyFlightShiftRequest request) {
        // Tìm chuyến bay theo flightId
        Flight flight = flightRepository.findById(request.getFlightId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyến bay với id: " + request.getFlightId()));

        // Lấy ngày ca trực từ Flight (giả sử flightDate đã được set)
        LocalDate shiftDate = flight.getFlightDate();
        if (shiftDate == null) {
            throw new RuntimeException("Chuyến bay chưa có thông tin ngày làm ca (flightDate is null).");
        }

        // Duyệt qua từng userId trong payload
        for (Integer userId : request.getUserIds()) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên với id: " + userId));

            // Kiểm tra xem nhân viên đã được phân công cho chuyến bay này chưa
            Optional<UserFlightShift> existing = userFlightShiftRepository.findByUserAndFlightAndShiftDate(user, flight, shiftDate);
            if (existing.isPresent()) {
                // Nếu đã tồn tại, báo lỗi cụ thể (bạn có thể bỏ qua hoặc ném ngoại lệ)
                throw new RuntimeException("Nhân viên " + user.getName() + " đã được phân công phục vụ chuyến bay này.");
            }

            // Nếu chưa có, tạo UserFlightShift mới
            UserFlightShift ufs = new UserFlightShift(user, flight, shiftDate);
            userFlightShiftRepository.save(ufs);
        }
    }

}

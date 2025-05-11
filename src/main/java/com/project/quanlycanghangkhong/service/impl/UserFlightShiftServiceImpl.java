package com.project.quanlycanghangkhong.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.quanlycanghangkhong.dto.ApplyFlightShiftRequest;
import com.project.quanlycanghangkhong.dto.UserFlightShiftResponseDTO;
import com.project.quanlycanghangkhong.dto.UserFlightShiftResponseSearchDTO;
import com.project.quanlycanghangkhong.model.Flight;
import com.project.quanlycanghangkhong.model.User;
import com.project.quanlycanghangkhong.model.UserFlightShift;
import com.project.quanlycanghangkhong.repository.FlightRepository;
import com.project.quanlycanghangkhong.repository.UserFlightShiftRepository;
import com.project.quanlycanghangkhong.repository.UserRepository;
import com.project.quanlycanghangkhong.service.UserFlightShiftService;
import com.project.quanlycanghangkhong.service.NotificationService;

@Service
public class UserFlightShiftServiceImpl implements UserFlightShiftService {

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserFlightShiftRepository userFlightShiftRepository;

    @Autowired
    private NotificationService notificationService;

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
            Optional<UserFlightShift> existing = userFlightShiftRepository.findByUserAndFlightAndShiftDate(user, flight,
                    shiftDate);
            if (existing.isPresent()) {
                // Nếu đã tồn tại, báo lỗi cụ thể (bạn có thể bỏ qua hoặc ném ngoại lệ)
                throw new RuntimeException(
                        "Nhân viên " + user.getName() + " đã được phân công phục vụ chuyến bay này.");
            }

            // Nếu chưa có, tạo UserFlightShift mới
            UserFlightShift ufs = new UserFlightShift(user, flight, shiftDate);
            userFlightShiftRepository.save(ufs);

            // Trigger notification cho user này
            notificationService.createNotification(
                userId,
                "FLIGHT_ASSIGN",
                "Bạn được phân công phục vụ chuyến bay " + flight.getFlightNumber(),
                "Ngày: " + shiftDate + ", Số hiệu: " + flight.getFlightNumber(),
                flight.getId().intValue(),
                true
            );
        }
    }

    public UserFlightShiftServiceImpl(UserFlightShiftRepository userFlightShiftRepository) {
        this.userFlightShiftRepository = userFlightShiftRepository;
    }

    @Override
    public List<UserFlightShift> getShiftsByDate(LocalDate shiftDate) {
        return userFlightShiftRepository.findByShiftDate(shiftDate);
    }

    @Override
    public List<UserFlightShift> getShiftsByUser(Integer userId) {
        return userFlightShiftRepository.findByUser_Id(userId);
    }

    @Override
    public List<UserFlightShiftResponseDTO> getShiftsByFlightAndDate(Long flightId, LocalDate shiftDate) {
        List<UserFlightShift> shifts = userFlightShiftRepository.findByFlight_IdAndShiftDate(flightId, shiftDate);
        return shifts.stream()
                .map(shift -> new UserFlightShiftResponseDTO(
                        shift.getId(),
                        shift.getUser().getId(),
                        shift.getUser().getName(),
                        shift.getFlight().getId(),
                        shift.getFlight().getFlightNumber(),
                        shift.getShiftDate()))
                .collect(Collectors.toList());
    }

    @Override
    public void removeFlightAssignment(Long flightId, LocalDate shiftDate, Integer userId) {
        Optional<UserFlightShift> opt = userFlightShiftRepository.findOneByFlightAndShiftDateAndUser(flightId,
                shiftDate, userId);
        if (opt.isPresent()) {
            userFlightShiftRepository.delete(opt.get());
        } else {
            throw new RuntimeException("Không tìm thấy ca chuyến bay với flightId="
                    + flightId + ", shiftDate=" + shiftDate
                    + ", userId=" + userId);
        }
    }

    @Override
    public boolean isUserAssignedToFlight(LocalDate shiftDate, Integer userId) {
        return userFlightShiftRepository.existsByShiftDateAndUser_Id(shiftDate, userId);
    }

    @Override
    public List<UserFlightShiftResponseSearchDTO> getFlightSchedulesByCriteria(LocalDate shiftDate, Integer teamId,
            Integer unitId, Long flightId) {
        return userFlightShiftRepository.findFlightSchedulesByCriteria(shiftDate, teamId, unitId, flightId);
    }

    @Override
    public List<UserFlightShiftResponseDTO> getAvailableShifts(Long flightId, LocalDate date) {
        // TODO: Implement logic to get available shifts
        return null;
    }

    @Override
    public List<UserFlightShiftResponseDTO> getAssignedShifts(Long flightId, LocalDate date) {
        // TODO: Implement logic to get assigned shifts
        return null;
    }

    @Override
    public List<Integer> getUserIdsByFlightAndDate(Long flightId, LocalDate shiftDate) {
        return userFlightShiftRepository.findByFlight_IdAndShiftDate(flightId, shiftDate)
                .stream()
                .map(ufs -> ufs.getUser().getId())
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<UserFlightShiftResponseDTO> getAllUserFlightShifts() {
        List<UserFlightShift> shifts = userFlightShiftRepository.findAll();
        return shifts.stream()
                .map(shift -> new UserFlightShiftResponseDTO(
                        shift.getId(),
                        shift.getUser().getId(),
                        shift.getUser().getName(),
                        shift.getFlight().getId(),
                        shift.getFlight().getFlightNumber(),
                        shift.getShiftDate()))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserFlightShiftResponseDTO> getShiftsByDateDTO(LocalDate date) {
        List<UserFlightShift> shifts = userFlightShiftRepository.findByShiftDate(date);
        return shifts.stream()
                .map(shift -> new UserFlightShiftResponseDTO(
                        shift.getId(),
                        shift.getUser().getId(),
                        shift.getUser().getName(),
                        shift.getFlight().getId(),
                        shift.getFlight().getFlightNumber(),
                        shift.getShiftDate()))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserFlightShiftResponseDTO> getShiftsByUserDTO(Integer userId) {
        List<UserFlightShift> shifts = userFlightShiftRepository.findByUser_Id(userId);
        return shifts.stream()
                .map(shift -> new UserFlightShiftResponseDTO(
                        shift.getId(),
                        shift.getUser().getId(),
                        shift.getUser().getName(),
                        shift.getFlight().getId(),
                        shift.getFlight().getFlightNumber(),
                        shift.getShiftDate()))
                .collect(Collectors.toList());
    }

    @Override
    public void updateUserFlightShift(Integer id, java.time.LocalDate shiftDate, Long flightId) {
        System.out.println("[updateUserFlightShift] id=" + id + ", shiftDate=" + shiftDate + ", flightId=" + flightId);
        UserFlightShift ufs = userFlightShiftRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch trực chuyến bay với id: " + id));
        if (shiftDate != null) {
            System.out.println("[updateUserFlightShift] Update shiftDate to " + shiftDate);
            ufs.setShiftDate(shiftDate);
        }
        if (flightId != null) {
            System.out.println("[updateUserFlightShift] Update flightId to " + flightId);
            com.project.quanlycanghangkhong.model.Flight flight = flightRepository.findById(flightId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyến bay với id: " + flightId));
            ufs.setFlight(flight);
        }
        userFlightShiftRepository.save(ufs);
        System.out.println("[updateUserFlightShift] Updated UserFlightShift: " + ufs.getId());
    }

    @Override
    public void deleteUserFlightShiftById(Integer id) {
        userFlightShiftRepository.deleteById(id);
    }

}

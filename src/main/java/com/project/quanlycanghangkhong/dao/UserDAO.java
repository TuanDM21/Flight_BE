package com.project.quanlycanghangkhong.dao;

import com.project.quanlycanghangkhong.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

@Repository
public class UserDAO {

    private DataSource dataSource;

    public UserDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<User> findUsersByCriteria(Integer teamId, Integer unitId, String searchText) throws SQLException {
        List<User> users = new ArrayList<>();

        // Xây dựng câu lệnh SQL dựa trên các tham số được truyền
        StringBuilder sql = new StringBuilder("SELECT * FROM users WHERE 1=1 ");
        if (teamId != null) {
            sql.append(" AND team_id = ? ");
        }
        if (unitId != null) {
            sql.append(" AND unit_id = ? ");
        }
        if (searchText != null && !searchText.trim().isEmpty()) {
            sql.append(" AND LOWER(name) LIKE ? ");
        }

        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int index = 1;
            if (teamId != null) {
                ps.setInt(index++, teamId);
            }
            if (unitId != null) {
                ps.setInt(index++, unitId);
            }
            if (searchText != null && !searchText.trim().isEmpty()) {
                ps.setString(index++, "%" + searchText.toLowerCase() + "%");
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    // Lưu ý: Bạn cần set thêm các thuộc tính khác nếu cần
                    users.add(user);
                }
            }
        }

        return users;
    }
}

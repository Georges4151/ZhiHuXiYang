package com.elderly.util;

import java.sql.*;

public class PasswordEncryptor {
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/elderly_care?useUnicode=true&characterEncoding=utf-8";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "your_password";
    
    public static void main(String[] args) {
        try {
            System.out.println("开始加密数据库密码...");
            
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            
            encryptAdminPasswords(conn);
            encryptUserPasswords(conn);
            
            conn.close();
            
            System.out.println("密码加密完成！");
        } catch (Exception e) {
            System.err.println("加密失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void encryptAdminPasswords(Connection conn) throws SQLException {
        String selectSQL = "SELECT id, username, password FROM admin_user WHERE password NOT LIKE '$2a$%'";
        String updateSQL = "UPDATE admin_user SET password = ? WHERE id = ?";
        
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(selectSQL);
        
        PreparedStatement pstmt = conn.prepareStatement(updateSQL);
        int count = 0;
        
        while (rs.next()) {
            Long id = rs.getLong("id");
            String plainPassword = rs.getString("password");
            
            String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
            
            pstmt.setString(1, hashedPassword);
            pstmt.setLong(2, id);
            pstmt.executeUpdate();
            
            count++;
            System.out.println("已加密管理员密码：ID=" + id);
        }
        
        pstmt.close();
        rs.close();
        stmt.close();
        
        System.out.println("管理员密码加密完成，共 " + count + " 条");
    }
    
    private static void encryptUserPasswords(Connection conn) throws SQLException {
        String selectSQL = "SELECT id, phone, password FROM elderly_user WHERE password NOT LIKE '$2a$%'";
        String updateSQL = "UPDATE elderly_user SET password = ? WHERE id = ?";
        
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(selectSQL);
        
        PreparedStatement pstmt = conn.prepareStatement(updateSQL);
        int count = 0;
        
        while (rs.next()) {
            Long id = rs.getLong("id");
            String plainPassword = rs.getString("password");
            
            String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
            
            pstmt.setString(1, hashedPassword);
            pstmt.setLong(2, id);
            pstmt.executeUpdate();
            
            count++;
            System.out.println("已加密用户密码：ID=" + id + ", 手机号=" + rs.getString("phone"));
        }
        
        pstmt.close();
        rs.close();
        stmt.close();
        
        System.out.println("用户密码加密完成，共 " + count + " 条");
    }
}

package com.powernode.bank.utils;

import java.sql.*;
import java.util.ResourceBundle;

/**
 * JDBC工具类
 * @author 老杜
 * @version 1.0
 * @since 1.0
 */
public class DBUtil {

    private static ResourceBundle bundle = ResourceBundle.getBundle("resources/jdbc");
    private static String driver = bundle.getString("driver");
    private static String url = bundle.getString("url");
    private static String user = bundle.getString("user");
    private static String password = bundle.getString("password");

    // 不让创建对象，因为工具类中的方法都是静态的。不需要创建对象。
    // 为了防止创建对象，故将构造方法私有化。
    private DBUtil(){}

    // DBUtil类加载时注册驱动
    static {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 这个对象实际上在服务器中只有一个。
    private static ThreadLocal<Connection> local = new ThreadLocal<>();

    /**
     * 这里没有使用数据库连接池，直接创建连接对象。
     * @return 连接对象
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        Connection conn = local.get();
        if (conn == null) {
            conn = DriverManager.getConnection(url, user, password);
            local.set(conn);
        }
        return conn;
    }

    /**
     * 关闭资源
     * @param conn 连接对象
     * @param stmt 数据库操作对象
     * @param rs 结果集对象
     */
    public static void close(Connection conn, Statement stmt, ResultSet rs){
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        if (conn != null) {
            try {
                conn.close();
                // 思考一下：为什么conn关闭之后，这里要从大Map中移除呢？
                // 根本原因是：Tomcat服务器是支持线程池的。也就是说一个人用过了t1线程，t1线程还有可能被其他用户使用。
                local.remove();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

}

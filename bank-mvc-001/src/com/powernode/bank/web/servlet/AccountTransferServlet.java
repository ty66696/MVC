package com.powernode.bank.web.servlet;

import com.powernode.bank.exceptions.AppException;
import com.powernode.bank.exceptions.MoneyNotEnoughException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

/**
 * 在不使用MVC架构模式的前提下，完成银行账户转账。
 * 分析这个程序存在哪些问题？
 *  缺点1> 代码的复用性太差。（代码的重用性太差）
 *  导致缺点1的原因？
 *      因为没有进行“职能分工”，没有独立组件的概念，所以没有办法进行代码复用。代码和代码之间的耦合度太高，扩展力太差。
 *  缺点2> 耦合度高，导致了代码很难扩展。
 *  缺点3> 操作数据库的代码和业务逻辑混杂在一起，很容易出错。编写代码的时候很容易出错，无法专注业务逻辑的编写。
 *
 * 分析以下AccountTransferServlet他都负责了什么？
 * 1> 负责了数据接收
 * 2> 负责了核心的业务处理
 * 3> 负责了数据库表中数据的CRUD操作（Create【增】 Retrieve【查】 Update【改】 Delete【删】）
 * 4> 负责了页面的数据展示
 * ....
 *
 * @author 老杜
 * @version 1.0
 * @since 1.0
 */
@WebServlet("/transfer")
public class AccountTransferServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 获取响应流对象
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        // 获取转账相关的信息
        String fromActno = request.getParameter("fromActno");
        String toActno = request.getParameter("toActno");
        double money = Double.parseDouble(request.getParameter("money"));

        // 编写转账的业务逻辑代码，连接数据库，进行转账操作
        // 1. 转账之前要判断转出账户的余额是否充足
        Connection conn = null;
        PreparedStatement ps = null;
        PreparedStatement ps2 = null;
        PreparedStatement ps3 = null;
        ResultSet rs = null;
        try {
            // 注册驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            // 获取连接
            String url = "jdbc:mysql://localhost:3306/mvc";
            String user = "root";
            String password = "root";
            conn = DriverManager.getConnection(url, user, password);
            // 开启事务（不再自动提交了，改为手动提交，业务完成之后再提交。）
            conn.setAutoCommit(false);
            // 获取预编译的数据库操作对象
            String sql1 = "select balance from t_act where actno = ?";
            ps = conn.prepareStatement(sql1);
            ps.setString(1, fromActno);
            // 执行SQL语句，返回结果集
            rs = ps.executeQuery();
            // 处理结果集
            if (rs.next()) {
                double balance = rs.getDouble("balance");
                if(balance < money) {
                    // 余额不足（使用异常处理机制。）
                    throw new MoneyNotEnoughException("对不起，余额不足");
                }
                // 程序能够执行到这里，说明余额一定是充足的
                // 开始转账
                // act001账户减去10000
                // act002账户加上10000
                String sql2 = "update t_act set balance = balance - ? where actno = ?";
                ps2 = conn.prepareStatement(sql2);
                ps2.setDouble(1, money);
                ps2.setString(2, fromActno);
                int count = ps2.executeUpdate();

                // 模拟异常
                /*String s = null;
                s.toString();*/

                String sql3 = "update t_act set balance = balance + ? where actno = ?";
                ps3 = conn.prepareStatement(sql3);
                ps3.setDouble(1, money);
                ps3.setString(2, toActno);
                // 累计
                count += ps3.executeUpdate();

                if (count != 2) {
                    throw new AppException("App异常，请联系管理员");
                }

                // 手动提交事务
                conn.commit();
                // 转账成功
                out.print("转账成功！");
            }
        } catch (Exception e) {
            // 保险起见：回滚事务。
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            // 异常处理，发生异常之后，你准备怎么做
            //e.printStackTrace();
            out.print(e.getMessage());

        } finally {
            // 释放资源
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (ps2 != null) {
                try {
                    ps2.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (ps3 != null) {
                try {
                    ps3.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }
    
}




























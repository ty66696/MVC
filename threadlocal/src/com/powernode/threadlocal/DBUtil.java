package com.powernode.threadlocal;

public class DBUtil {

    // 静态变量特点：类加载时执行，并且只执行一次。
    // 全局的大Map集合
    private static MyThreadLocal<Connection> local = new MyThreadLocal<>();

    /**
     * 每一次都调用这个方法来获取Connection对象
     * @return
     */
    public static Connection getConnection(){
        Connection connection = local.get();
        if (connection == null) {
            // 第一次调用：getConnection()方法的时候，connection一定是空的。
            // 空的就new一次。
            connection = new Connection();
            // 将new的Connection对象绑定到大Map集合中。
            local.set(connection);
        }
        return connection;
    }

}

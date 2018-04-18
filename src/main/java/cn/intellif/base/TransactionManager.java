package cn.intellif.base;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.commons.dbutils.DbUtils;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;

public abstract class TransactionManager {
    private static DataSource dataSource = new ComboPooledDataSource("mysql");
    private static ThreadLocal<Boolean> isTran = new ThreadLocal<Boolean>(){
        @Override
        protected Boolean initialValue() {
            return false;
        }
    };

    private static ThreadLocal<Connection> proxyConns = new ThreadLocal<>();

    private static ThreadLocal<Connection> realConns = new ThreadLocal<>();

    //开始准备事务
    public static void preparedTran(){
        try{
            isTran.set(true);
            final Connection realConn = dataSource.getConnection();
            realConn.setAutoCommit(false);
            realConns.set(realConn);
            Connection proxyConn = (Connection) Proxy.newProxyInstance(TransactionManager.class.getClassLoader(), new Class[]{Connection.class}, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    String methodName = method.getName();
                    if(methodName.equals("close")) {//如果为close方法就不进行空执行
                        return null;
                    }else{
                       return method.invoke(realConn,args);
                    }
                }
            });
            proxyConns.set(proxyConn);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }


    public static  DataSource getDataSource(){
        if(isTran.get()){//如果需要开启事务
           return (DataSource) Proxy.newProxyInstance(TransactionManager.class.getClassLoader(), new Class[]{DataSource.class}, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    String methodName = method.getName();
                    if(methodName.equals("getConnection")){
                        return proxyConns.get();
                    }else{
                        return method.invoke(dataSource,args);
                    }

                }
            });
        }else{
            return dataSource;
        }
    }

    //回滚事务
    public static void rollBack(){
        Connection connection = realConns.get();
        DbUtils.rollbackAndCloseQuietly(connection);
    }

    //提交事务
    public static void commit(){
        Connection connection = realConns.get();
        DbUtils.commitAndCloseQuietly(connection);
    }

    //释放资源
    public static void releaseResource(){
        DbUtils.closeQuietly(realConns.get());
        DbUtils.closeQuietly(proxyConns.get());
        isTran.remove();
        proxyConns.remove();
        realConns.remove();
    }

}

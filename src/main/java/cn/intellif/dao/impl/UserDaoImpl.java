package cn.intellif.dao.impl;

import cn.intellif.base.TransactionManager;
import cn.intellif.dao.UserDao;
import cn.intellif.domain.UserEntity;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class UserDaoImpl implements UserDao {

    @Override
    public boolean save(UserEntity entity) {
        QueryRunner runner = new QueryRunner(TransactionManager.getDataSource());
        try {
            int rowCount = runner.update("insert into t_user(id,name,age)value(?,?,?)",entity.getId(),entity.getName(),entity.getAge());
            if(rowCount<=0)
                return false;
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        QueryRunner runner = new QueryRunner(TransactionManager.getDataSource());

        try {
            int rowCount = runner.update("delete from t_user where id =?",id);
            if(rowCount<=0)
                return false;
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<Map<String,Object>> listAll() {
        QueryRunner runner = new QueryRunner(TransactionManager.getDataSource());
        try {
          return  runner.query("select * from t_user", new ResultSetHandler<List<Map<String,Object>>>() {
                @Override
                public List<Map<String,Object>> handle(ResultSet resultSet) throws SQLException {
                    ResultSetMetaData rsmd = resultSet.getMetaData();
                    int count = rsmd.getColumnCount();
                    List<Map<String,Object>> list = new LinkedList<>();
                    while (resultSet.next()){
                        Map<String,Object> map = new HashMap<>();
                        for(int i=1;i<=count;i++){
                            String key = rsmd.getColumnName(i);
                            Object value = resultSet.getObject(i);
                            map.put(key,value);
                        }
                        list.add(map);
                    }
                    return list;
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

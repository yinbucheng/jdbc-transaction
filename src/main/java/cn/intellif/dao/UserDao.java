package cn.intellif.dao;

import cn.intellif.domain.UserEntity;

import java.util.List;
import java.util.Map;

public interface UserDao {
    boolean save(UserEntity entity);
    boolean deleteById(Long id);
    List<Map<String,Object>> listAll();
}

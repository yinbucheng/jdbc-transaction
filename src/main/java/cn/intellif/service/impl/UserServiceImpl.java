package cn.intellif.service.impl;

import cn.intellif.base.BasicFactory;
import cn.intellif.base.Tranactional;
import cn.intellif.dao.UserDao;
import cn.intellif.domain.UserEntity;
import cn.intellif.service.IUserService;

import java.beans.Transient;

public class UserServiceImpl implements IUserService {
    private UserDao userDao = BasicFactory.getInstance().getDao(UserDao.class);
    @Override
    public Object save(UserEntity userEntity) {
        return userDao.save(userEntity);
    }

    @Override
    public Object listAll() {
        return userDao.listAll();
    }

    @Override
    @Tranactional
    public Object test() {
        UserEntity entity = new UserEntity();
        entity.setName("yinchong2");
        entity.setAge(24);
        userDao.save(entity);
        int i =1/0;
        userDao.deleteById(1L);
        return null;
    }
}

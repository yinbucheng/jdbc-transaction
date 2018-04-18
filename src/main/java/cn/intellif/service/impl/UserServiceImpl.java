package cn.intellif.service.impl;

import cn.intellif.base.BasicFactory;
import cn.intellif.dao.UserDao;
import cn.intellif.domain.UserEntity;
import cn.intellif.service.IUserService;

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
}

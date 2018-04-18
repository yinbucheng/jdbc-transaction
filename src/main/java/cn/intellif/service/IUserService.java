package cn.intellif.service;

import cn.intellif.domain.UserEntity;

public interface IUserService {
    Object save(UserEntity userEntity);
    Object listAll();
    Object test();
}

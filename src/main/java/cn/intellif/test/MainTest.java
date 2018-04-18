package cn.intellif.test;

import cn.intellif.base.BasicFactory;
import cn.intellif.domain.UserEntity;
import cn.intellif.service.IUserService;

public class MainTest {
    public static void main(String[] args) {
       IUserService userService = BasicFactory.getInstance().getService(IUserService.class);
        UserEntity entity = new UserEntity();
        entity.setAge(12);
        entity.setName("nice");
        userService.save(entity);
    }
}

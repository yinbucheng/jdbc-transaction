package cn.intellif.test;

import cn.intellif.base.BasicFactory;
import cn.intellif.domain.UserEntity;
import cn.intellif.service.IUserService;

public class MainTest {
    public static void main(String[] args) {
        //saveTest();
        //listTest();
        IUserService userService = BasicFactory.getInstance().getService(IUserService.class);
        userService.test();
    }

    private static void listTest() {
        IUserService userService = BasicFactory.getInstance().getService(IUserService.class);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>:"+userService.listAll());
    }

    private static void saveTest() {
        IUserService userService = BasicFactory.getInstance().getService(IUserService.class);
        UserEntity entity = new UserEntity();
        entity.setAge(12);
        entity.setName("nice");
        userService.save(entity);
    }
}

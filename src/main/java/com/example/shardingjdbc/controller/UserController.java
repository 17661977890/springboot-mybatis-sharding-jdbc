package com.example.shardingjdbc.controller;

import com.example.shardingjdbc.entity.User;
import com.example.shardingjdbc.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * 用户web
 */
@Controller
public class UserController {

    @Autowired
	private UserMapper userMapper;

    /**
     * 测试单库分表 时候（也可以测试分库分表--水平拆分（将cityId 设置值改为i%2==...）---看效果会发现，一部分数据在1库user1表，一部分数据在2库user2表）
     * @return
     */
	@RequestMapping("/user/save")
	@ResponseBody
	public String save() {
        for (int i = 0; i <10 ; i++) {
            User user=new User();
            user.setName("test"+i);
            user.setCityId(1%2==0?1:2);
            user.setCreateTime(new Date());
            user.setSex(i%2==0?1:2);
            user.setPhone("11111111"+i);
            user.setEmail("xxxxx");
            user.setCreateTime(new Date());
            user.setPassword("eeeeeeeeeeee");
            userMapper.save(user);
        }

		return "success";
	}

    /**
     * 测试分库分表
     * @return
     */
    @RequestMapping("/user/save2")
    @ResponseBody
    public String save2() {
        for (int i = 0; i <10 ; i++) {
            for(int j =0; j<2; j++){
                User user=new User();
                user.setName("test"+i);
                user.setCityId(i%2==0?1:2);
                user.setCreateTime(new Date());
                user.setSex(j%2==0?1:2);
                user.setPhone("11111111"+i);
                user.setEmail("xxxxx");
                user.setCreateTime(new Date());
                user.setPassword("eeeeeeeeeeee");
                userMapper.save(user);
            }

        }

        return "success";
    }

    /**
     * 获取数据
     * @param id
     * @return
     */
	@RequestMapping("/user/get/{id}")
	@ResponseBody
	public User get(@PathVariable Long id) {
		User user =  userMapper.get(id);
		System.out.println(user.getId());
		return user;
	}

}
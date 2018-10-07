package com.dlri.chinacnr.bwts.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dlri.chinacnr.bwts.dao.UserDao;
import com.dlri.chinacnr.bwts.entity.User;
import com.dlri.chinacnr.bwts.service.UserService;

//加入注解service,让spring来管理bean.
@Service("userService")
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserDao userDao;//自动注入

	public User login(User user) {
		return userDao.login(user);
	}

	public User getUserInfo(long id) {
		return userDao.getUserInfo(id);
	}

}

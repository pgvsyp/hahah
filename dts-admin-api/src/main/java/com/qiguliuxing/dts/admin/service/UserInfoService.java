package com.qiguliuxing.dts.admin.service;

import com.qiguliuxing.dts.admin.dao.UserInfo;
import com.qiguliuxing.dts.db.domain.DtsUser;
import com.qiguliuxing.dts.db.service.DtsUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class UserInfoService {
	@Autowired
	private DtsUserService userService;

	public UserInfo getInfo(Integer userId) {
		DtsUser user = userService.findById(userId);
		Assert.state(user != null, "用户不存在");
		UserInfo userInfo = new UserInfo();
		userInfo.setNickName(user.getNickname());
		userInfo.setAvatarUrl(user.getAvatar());
		return userInfo;
	}
}

package com.qiguliuxing.dts.admin.web;

import com.alibaba.fastjson.JSONObject;
import com.qiguliuxing.dts.admin.dao.UserInfo;
import com.qiguliuxing.dts.admin.dao.UserToken;
import com.qiguliuxing.dts.admin.service.UserTokenManager;
import com.qiguliuxing.dts.admin.util.*;
import com.qiguliuxing.dts.core.captcha.CaptchaCodeManager;
import com.qiguliuxing.dts.core.consts.CommConsts;
import com.qiguliuxing.dts.core.type.UserTypeEnum;
import com.qiguliuxing.dts.core.util.Base64;
import com.qiguliuxing.dts.core.util.JacksonUtil;
import com.qiguliuxing.dts.core.util.ResponseUtil;
import com.qiguliuxing.dts.core.util.UUID;
import com.qiguliuxing.dts.core.util.bcrypt.BCryptPasswordEncoder;
import com.qiguliuxing.dts.db.domain.DtsAdmin;
import com.qiguliuxing.dts.db.domain.DtsUser;
import com.qiguliuxing.dts.db.service.DtsPermissionService;
import com.qiguliuxing.dts.db.service.DtsRoleService;
import com.qiguliuxing.dts.db.service.DtsUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.qiguliuxing.dts.admin.util.WxResponseCode.*;

@Api(tags = "用户操作")
@RestController
@RequestMapping("/admin/auth")
@Validated
public class AdminAuthController {
	private static final Logger logger = LoggerFactory.getLogger(AdminAuthController.class);

	@Autowired
	private DtsRoleService roleService;
	@Autowired
	private DtsPermissionService permissionService;

	@Autowired
	private DtsUserService userService;

	/*
	 * { username : value, password : value }
	 */
	@PostMapping("/login")
	public Object login(@RequestBody String body) {
		logger.info("【请求开始】系统管理->用户登录,请求参数:body:{}", body);

		String username = JacksonUtil.parseString(body, "username");
		String password = JacksonUtil.parseString(body, "password");
		String code = JacksonUtil.parseString(body, "code");
		String uuid = JacksonUtil.parseString(body, "uuid");

		if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password) || StringUtils.isEmpty(code) || StringUtils.isEmpty(uuid)) {
			return ResponseUtil.badArgument();
		}

		//验证码校验
		String cachedCaptcha = CaptchaCodeManager.getCachedCaptcha(uuid);
		if (cachedCaptcha == null) {
			logger.error("系统管理->用户登录  错误:{},", AdminResponseCode.AUTH_CAPTCHA_EXPIRED.desc());
			return AdminResponseUtil.fail(AdminResponseCode.AUTH_CAPTCHA_EXPIRED);
		}
		if (!code.equalsIgnoreCase(cachedCaptcha)) {
			logger.error("系统管理->用户登录  错误:{},输入验证码：{},后台验证码：{}", AdminResponseCode.AUTH_CAPTCHA_ERROR.desc(),code,cachedCaptcha);
			return AdminResponseUtil.fail(AdminResponseCode.AUTH_CAPTCHA_ERROR);
		}
		Subject currentUser = SecurityUtils.getSubject();
		try {
			currentUser.login(new UsernamePasswordToken(username, password));
		} catch (UnknownAccountException uae) {
			logger.error("系统管理->用户登录  错误:{}", AdminResponseCode.ADMIN_INVALID_ACCOUNT_OR_PASSWORD.desc());
			return AdminResponseUtil.fail(AdminResponseCode.ADMIN_INVALID_ACCOUNT_OR_PASSWORD);
		} catch (LockedAccountException lae) {
			logger.error("系统管理->用户登录 错误:{}", AdminResponseCode.ADMIN_LOCK_ACCOUNT.desc());
			return AdminResponseUtil.fail(AdminResponseCode.ADMIN_LOCK_ACCOUNT);

		} catch (AuthenticationException ae) {
			logger.error("系统管理->用户登录 错误:{}", AdminResponseCode.ADMIN_LOCK_ACCOUNT.desc());
			return AdminResponseUtil.fail(AdminResponseCode.ADMIN_INVALID_AUTH);
		}

		logger.info("【请求结束】系统管理->用户登录,响应结果:{}", JSONObject.toJSONString(currentUser.getSession().getId()));
		return ResponseUtil.ok(currentUser.getSession().getId());
	}

	@ApiOperation(value = "用户登录")
	@PostMapping("/userLogin")
	public Object userLogin(@RequestBody String body, HttpServletRequest request) {
		logger.info("【请求开始】账户登录,请求参数,body:{}", body);

		String username = JacksonUtil.parseString(body, "username");
		String password = JacksonUtil.parseString(body, "password");

		if (username == null || password == null) {
			return ResponseUtil.badArgument();
		}

		List<DtsUser> userList = userService.queryByMobile(username);
		DtsUser user = null;
		if (userList.size() > 1) {
			logger.error("账户登录 出现多个同名用户错误,用户名:{},用户数量:{}", username, userList.size());
			return ResponseUtil.serious();
		} else if (userList.size() == 0) {
			logger.error("账户登录 用户尚未存在,用户名:{}", username);
			return ResponseUtil.badArgumentValue();
		} else {
			user = userList.get(0);
		}

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		if (!encoder.matches(password, user.getPassword())) {
			logger.error("账户登录 ,错误密码：{},{}", password, AUTH_INVALID_ACCOUNT.desc());// 错误的密码打印到日志中作为提示也无妨
			return WxResponseUtil.fail(AUTH_INVALID_ACCOUNT);
		}

		// userInfo
		UserInfo userInfo = new UserInfo();
		userInfo.setNickName(user.getUsername());
		userInfo.setAvatarUrl(user.getAvatar());

		try {
			String pattern = "yyyy-MM-dd HH:mm:ss";
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
			String registerDate = formatter.format(user.getAddTime());
			String updateTime = formatter.format(user.getUpdateTime());
			userInfo.setUserId(user.getId());
			userInfo.setRegisterDate(registerDate);
			userInfo.setUpdateTime(updateTime);
			userInfo.setStatus(user.getStatus());
			userInfo.setUserLevel(user.getUserLevel());// 用户层级
			userInfo.setUserLevelDesc(UserTypeEnum.getInstance(user.getUserLevel()).getDesc());// 用户层级描述
		} catch (Exception e) {
			logger.error("账户登录：设置用户指定信息出错："+e.getMessage());
			e.printStackTrace();
		}

		userService.updateById(user);

		// token
		UserToken userToken = null;
		try {
			userToken = UserTokenManager.generateToken(user.getId());
		} catch (Exception e) {
			logger.error("账户登录失败,生成token失败：{}", user.getId());
			e.printStackTrace();
			return ResponseUtil.fail();
		}

		Map<Object, Object> result = new HashMap<Object, Object>();
		result.put("token", userToken.getToken());
		result.put("tokenExpire", userToken.getExpireTime().toString());
		result.put("userInfo", userInfo);

		logger.info("【请求结束】账户登录,响应结果:{}", JSONObject.toJSONString(result));
		Subject currentUser = SecurityUtils.getSubject();
		currentUser.login(new UsernamePasswordToken("luomu", "yujian520"));
		return ResponseUtil.ok(result);
	}

	@ApiOperation(value = "用户注册")
	@PostMapping("/userRegister")
	public Object userRegister(@RequestBody String body, HttpServletRequest request) {
		logger.info("【请求开始】账号注册,请求参数，body:{}", body);

		String username = JacksonUtil.parseString(body, "username");
		String password = JacksonUtil.parseString(body, "password");
		String mobile = JacksonUtil.parseString(body, "mobile");
//		String wxCode = JacksonUtil.parseString(body, "wxCode");

		if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password) || StringUtils.isEmpty(mobile)) {
			return ResponseUtil.badArgument();
		}

		List<DtsUser> userList = userService.queryByUsername(username);

		DtsUser user = null;
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String encodedPassword = encoder.encode(password);
		user = new DtsUser();
		user.setUsername(username);
		user.setPassword(encodedPassword);
		user.setMobile(mobile);
//		user.setWeixinOpenid(openId);
		user.setAvatar(CommConsts.DEFAULT_AVATAR);
		user.setNickname(username);
		user.setGender((byte) 0);
		user.setUserLevel((byte) 0);
		user.setStatus((byte) 0);
		user.setLastLoginTime(LocalDateTime.now());
		user.setLastLoginIp(IpUtil.client(request));
		userService.add(user);

		// userInfo
		UserInfo userInfo = new UserInfo();
		userInfo.setNickName(username);
		userInfo.setAvatarUrl(user.getAvatar());

		// token
		UserToken userToken = null;
		try {
			userToken = UserTokenManager.generateToken(user.getId());
		} catch (Exception e) {
			logger.error("账号注册失败,生成token失败：{}", user.getId());
			e.printStackTrace();
			return ResponseUtil.fail();
		}

		Map<Object, Object> result = new HashMap<Object, Object>();
		result.put("token", userToken.getToken());
		result.put("tokenExpire", userToken.getExpireTime().toString());
		result.put("userInfo", userInfo);

		logger.info("【请求结束】账号注册,响应结果:{}", JSONObject.toJSONString(result));
		return ResponseUtil.ok(result);

	}

	@ApiOperation(value = "发送邮箱验证码")
	@PostMapping("/sendEmail")
	public Map<String, String> sendEmail(@RequestBody Map jsonObject) throws JSONException {
		String eMail = (String) jsonObject.get("eMail");

		String randomCode = MailUtil.generateRandomCode(5);

		MailUtil.sendMail(eMail, randomCode);

		//返回邮箱验证码
		Map<String, String> resultMap = new HashMap<>();
		resultMap.put("eMailCode", randomCode);
		return resultMap;
	}


	/*
	 * 用户注销
	 */
	@RequiresAuthentication
	@PostMapping("/logout")
	public Object login() {
		Subject currentUser = SecurityUtils.getSubject();
		currentUser.logout();

		logger.info("【请求结束】系统管理->用户注销,响应结果:{}", JSONObject.toJSONString(currentUser.getSession().getId()));
		return ResponseUtil.ok();
	}

	@RequiresAuthentication
	@GetMapping("/info")
	public Object info() {
		Subject currentUser = SecurityUtils.getSubject();
		DtsAdmin admin = (DtsAdmin) currentUser.getPrincipal();

		Map<String, Object> data = new HashMap<>();
		data.put("name", admin.getUsername());
		data.put("avatar", admin.getAvatar());

		Integer[] roleIds = admin.getRoleIds();
		Set<String> roles = roleService.queryByIds(roleIds);
		Set<String> permissions = permissionService.queryByRoleIds(roleIds);
		data.put("roles", roles);
		// NOTE
		// 这里需要转换perms结构，因为对于前端而已API形式的权限更容易理解
		data.put("perms", toAPI(permissions));

		logger.info("【请求结束】系统管理->用户信息获取,响应结果:{}", JSONObject.toJSONString(data));
		return ResponseUtil.ok(data);
	}

	@Autowired
	private ApplicationContext context;
	private HashMap<String, String> systemPermissionsMap = null;

	private Collection<String> toAPI(Set<String> permissions) {
		if (systemPermissionsMap == null) {
			systemPermissionsMap = new HashMap<>();
			final String basicPackage = "com.qiguliuxing.dts.admin";
			List<Permission> systemPermissions = PermissionUtil.listPermission(context, basicPackage);
			for (Permission permission : systemPermissions) {
				String perm = permission.getRequiresPermissions().value()[0];
				String api = permission.getApi();
				systemPermissionsMap.put(perm, api);
			}
		}

		Collection<String> apis = new HashSet<>();
		for (String perm : permissions) {
			String api = systemPermissionsMap.get(perm);
			apis.add(api);

			if (perm.equals("*")) {
				apis.clear();
				apis.add("*");
				return apis;
				// return systemPermissionsMap.values();
			}
		}
		return apis;
	}
	
	/**
     * 生成验证码
     */
    @GetMapping("/captchaImage")
    public Object getCode(HttpServletResponse response) throws IOException {
        // 生成随机字串
        String verifyCode = VerifyCodeUtils.generateVerifyCode(4);
        // 唯一标识
        String uuid = UUID.randomUUID().toString(true);
        boolean successful = CaptchaCodeManager.addToCache(uuid, verifyCode,10);//存储内存
        if (!successful) {
			logger.error("请求验证码出错:{}", AdminResponseCode.AUTH_CAPTCHA_FREQUENCY.desc());
			return AdminResponseUtil.fail(AdminResponseCode.AUTH_CAPTCHA_FREQUENCY);
		}
        // 生成图片
        int w = 111, h = 36;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        VerifyCodeUtils.outputImage(w, h, stream, verifyCode);
        try {
        	Map<String, Object> data = new HashMap<>();
            data.put("uuid", uuid);
            data.put("img", Base64.encode(stream.toByteArray()));
            return ResponseUtil.ok(data);
        } catch (Exception e){
            e.printStackTrace();
            return ResponseUtil.serious();
        } finally {
            stream.close();
        }
    }

	@GetMapping("/401")
	public Object page401() {
		return ResponseUtil.unlogin();
	}

	@GetMapping("/index")
	public Object pageIndex() {
		return ResponseUtil.ok();
	}

	@GetMapping("/403")
	public Object page403() {
		return ResponseUtil.unauthz();
	}

	@ApiOperation("密码重置操作")
	@PostMapping("reset")
	public Object reset(@RequestBody @ApiParam("需要用户id,密码") DtsUser duser) {
		Integer uid = duser.getId();
		String password = duser.getPassword();
		logger.info("【请求开始】账号密码重置,请求参数，uid, password", uid, password);

		if (password == null) {
			return ResponseUtil.badArgument();
		}

		DtsUser user = userService.findById(uid);

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String encodedPassword = encoder.encode(password);
		user.setPassword(encodedPassword);

		if (userService.updateById(user) == 0) {
			logger.error("账号密码重置更新用户信息出错,id：{}", user.getId());
			return ResponseUtil.updatedDataFailed();
		}

		logger.info("【请求结束】账号密码重置成功!");
		return ResponseUtil.ok();
	}

	@ApiOperation("退出登录")
	@PostMapping("logoutUser")
	public Object logoutUser() {
		Subject currentUser = SecurityUtils.getSubject();
		currentUser.logout();
		logger.info("【请求结束】注销登录成功!");
		return ResponseUtil.ok();
	}

	@ApiOperation("注销用户")
	@PostMapping("deleteUser")
	public Object deleteUser(@RequestBody @ApiParam("用户id,密码") DtsUser duser) {
		DtsUser user = userService.findById(duser.getId());
		String password = duser.getPassword();
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		if (!encoder.matches(password, user.getPassword())) {
			logger.error("账户登录 ,错误密码：{},{}", password, AUTH_INVALID_ACCOUNT.desc());// 错误的密码打印到日志中作为提示也无妨
			return WxResponseUtil.fail(AUTH_INVALID_ACCOUNT);
		}
		userService.deleteById(duser.getId());

		logger.info("【请求结束】注销成功!");
		return ResponseUtil.ok();
	}

	@ApiOperation("头像重置操作")
	@PostMapping("resetAvatar")
	public Object resetAvatar(@RequestBody @ApiParam("需要用户id,头像") DtsUser duser) {
		Integer uid = duser.getId();
		String avatar = duser.getAvatar();
		logger.info("【请求开始】账号密码重置,请求参数，uid, password", uid, avatar);

		if (avatar == null) {
			return ResponseUtil.badArgument();
		}

		DtsUser user = userService.findById(uid);

		user.setAvatar(avatar);

		if (userService.updateById(user) == 0) {
			logger.error("头像重置更新用户信息出错,id：{}", user.getId());
			return ResponseUtil.updatedDataFailed();
		}

		logger.info("【请求结束】头像重置成功!");
		return ResponseUtil.ok();
	}
}

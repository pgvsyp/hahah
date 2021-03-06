package com.qiguliuxing.dts.db.service;

import com.github.pagehelper.PageHelper;
import com.qiguliuxing.dts.db.bean.DayStatis;
import com.qiguliuxing.dts.db.dao.DtsUserAccountMapper;
import com.qiguliuxing.dts.db.dao.DtsUserMapper;
import com.qiguliuxing.dts.db.dao.ex.StatMapper;
import com.qiguliuxing.dts.db.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DtsUserService {
	
	@Resource
	private DtsUserMapper userMapper;
	
	@Resource
	private DtsUserAccountMapper userAccountMapper;
	
	@Resource
	private StatMapper statMapper;

	public DtsUser findById(Integer userId) {
		return userMapper.selectByPrimaryKey(userId);
	}

	public UserVo findUserVoById(Integer userId) {
		DtsUser user = findById(userId);
		UserVo userVo = new UserVo();
		userVo.setNickname(user.getNickname());
		userVo.setAvatar(user.getAvatar());
		return userVo;
	}

	public DtsUser queryByOid(String openId) {
		DtsUserExample example = new DtsUserExample();
		example.or().andWeixinOpenidEqualTo(openId).andDeletedEqualTo(false);
		return userMapper.selectOneByExample(example);
	}

	public void add(DtsUser user) {
		user.setAddTime(LocalDateTime.now());
		user.setUpdateTime(LocalDateTime.now());
		userMapper.insertSelective(user);
	}

	public int updateById(DtsUser user) {
		user.setUpdateTime(LocalDateTime.now());
		return userMapper.updateByPrimaryKeySelective(user);
	}

	public List<DtsUser> querySelective(String username, String mobile, Integer page, Integer size, String sort,
			String order) {
		DtsUserExample example = new DtsUserExample();
		DtsUserExample.Criteria criteria = example.createCriteria();

		if (!StringUtils.isEmpty(username)) {
			criteria.andNicknameLike("%" + username + "%");
		}
		if (!StringUtils.isEmpty(mobile)) {
			criteria.andMobileEqualTo(mobile);
		}
		criteria.andDeletedEqualTo(false);

		if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
			example.setOrderByClause(sort + " " + order);
		}

		PageHelper.startPage(page, size);
		return userMapper.selectByExample(example);
	}

	public int count() {
		DtsUserExample example = new DtsUserExample();
		example.or().andDeletedEqualTo(false);

		return (int) userMapper.countByExample(example);
	}

	public List<DtsUser> queryByUsername(String username) {
		DtsUserExample example = new DtsUserExample();
		example.or().andUsernameEqualTo(username).andDeletedEqualTo(false);
		return userMapper.selectByExample(example);
	}

	public boolean checkByUsername(String username) {
		DtsUserExample example = new DtsUserExample();
		example.or().andUsernameEqualTo(username).andDeletedEqualTo(false);
		return userMapper.countByExample(example) != 0;
	}

	public List<DtsUser> queryByMobile(String mobile) {
		DtsUserExample example = new DtsUserExample();
		example.or().andMobileEqualTo(mobile).andDeletedEqualTo(false);
		return userMapper.selectByExample(example);
	}

	public List<DtsUser> queryByOpenid(String openid) {
		DtsUserExample example = new DtsUserExample();
		example.or().andWeixinOpenidEqualTo(openid).andDeletedEqualTo(false);
		return userMapper.selectByExample(example);
	}

	public void deleteById(Integer id) {
		userMapper.logicalDeleteByPrimaryKey(id);
	}

	/**
	 * ??????????????????
	 * @param
	 */
	public void approveAgency(Integer userId,Integer settlementRate,String shareUrl) {
		//??????????????????
		DtsUserAccountExample example = new DtsUserAccountExample();
		example.or().andUserIdEqualTo(userId);
		
		DtsUserAccount dbAccount = userAccountMapper.selectOneByExample(example);
		if (dbAccount == null) {
			throw new RuntimeException("?????????????????????");
		}
		dbAccount.setShareUrl(shareUrl);
		if (!StringUtils.isEmpty(settlementRate)) {
			dbAccount.setSettlementRate(settlementRate);
		}
		dbAccount.setModifyTime(LocalDateTime.now());
		userAccountMapper.updateByPrimaryKey(dbAccount);
		
		//???????????????????????????
		DtsUser user = findById(userId);
		user.setUserLevel((byte) 2);//??????????????????
		user.setStatus((byte) 0);//????????????
		updateById(user);
		
	}

	public DtsUserAccount detailApproveByUserId(Integer userId) {
		// ??????????????????
		DtsUserAccountExample example = new DtsUserAccountExample();
		example.or().andUserIdEqualTo(userId);

		DtsUserAccount dbAccount = userAccountMapper.selectOneByExample(example);
		return dbAccount;
	}
	
	public List<DtsUser> queryDtsUserListByNickname(String username,String mobile) {
		DtsUserExample example = new DtsUserExample();
		DtsUserExample.Criteria criteria = example.createCriteria();
		if (!StringUtils.isEmpty(username)) {
			criteria.andNicknameLike("%" + username + "%");
		}
		if (!StringUtils.isEmpty(mobile)) {
			criteria.andMobileEqualTo(mobile);
		}
		criteria.andDeletedEqualTo(false);
		return userMapper.selectByExample(example);
	}

	public List<DayStatis> recentCount(int statisDaysRang) {
		return statMapper.statisIncreaseUserCnt(statisDaysRang);
	}

	public DtsUser findUserId(Integer id) {
		return userMapper.findUserId(id);
	}
}

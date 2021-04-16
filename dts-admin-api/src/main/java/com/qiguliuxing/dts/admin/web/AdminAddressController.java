package com.qiguliuxing.dts.admin.web;

import com.alibaba.fastjson.JSONObject;
import com.qiguliuxing.dts.core.util.ResponseUtil;
import com.qiguliuxing.dts.db.domain.DtsAddress;
import com.qiguliuxing.dts.db.service.DtsAddressService;
import com.qiguliuxing.dts.db.service.DtsRegionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "地址操作")
@RestController
@RequestMapping("/admin/address")
@Validated
public class AdminAddressController {
	private static final Logger logger = LoggerFactory.getLogger(AdminAddressController.class);

	@Autowired
	private DtsAddressService addressService;

	@Autowired
	private DtsRegionService regionService;

	private Map<String, Object> toVo(DtsAddress address) {
		Map<String, Object> addressVo = new HashMap<>();
		addressVo.put("id", address.getId());
		addressVo.put("userId", address.getUserId());
		addressVo.put("name", address.getName());
		addressVo.put("mobile", address.getMobile());
		addressVo.put("isDefault", address.getIsDefault());
		addressVo.put("provinceId", address.getProvinceId());
		addressVo.put("cityId", address.getCityId());
		addressVo.put("areaId", address.getAreaId());
		addressVo.put("address", address.getAddress());
		String province = regionService.findById(address.getProvinceId()).getName();
		String city = regionService.findById(address.getCityId()).getName();
		String area = regionService.findById(address.getAreaId()).getName();
		addressVo.put("province", province);
		addressVo.put("city", city);
		addressVo.put("area", area);
		return addressVo;
	}


	@ApiOperation(value = "获取收货地址列表")
	@GetMapping("list")
	public Object list(@RequestParam @ApiParam("用户id")Integer userId) {
		logger.info("【请求开始】获取收货地址列表,请求参数,userId：{}", userId);
		if (userId == null) {
			return ResponseUtil.unlogin();
		}

		List<DtsAddress> addressList = addressService.queryByUid(userId);
//		List<Map<String, Object>> addressVoList = new ArrayList<>(addressList.size());
//		List<DtsRegion> regionList = getDtsRegions();
//		for (DtsAddress address : addressList) {
//			Map<String, Object> addressVo = new HashMap<>();
//			addressVo.put("id", address.getId());
//			addressVo.put("name", address.getName());
//			addressVo.put("mobile", address.getMobile());
//			addressVo.put("isDefault", address.getIsDefault());
//			Callable<String> provinceCallable = () -> regionList.stream()
//					.filter(region -> region.getId().equals(address.getProvinceId())).findAny().orElse(null).getName();
//			Callable<String> cityCallable = () -> regionList.stream()
//					.filter(region -> region.getId().equals(address.getCityId())).findAny().orElse(null).getName();
//			Callable<String> areaCallable = () -> regionList.stream()
//					.filter(region -> region.getId().equals(address.getAreaId())).findAny().orElse(null).getName();
//			FutureTask<String> provinceNameCallableTask = new FutureTask<>(provinceCallable);
//			FutureTask<String> cityNameCallableTask = new FutureTask<>(cityCallable);
//			FutureTask<String> areaNameCallableTask = new FutureTask<>(areaCallable);
//			executorService.submit(provinceNameCallableTask);
//			executorService.submit(cityNameCallableTask);
//			executorService.submit(areaNameCallableTask);
//			String detailedAddress = "";
//			try {
//				String province = provinceNameCallableTask.get();
//				String city = cityNameCallableTask.get();
//				String area = areaNameCallableTask.get();
//				String addr = address.getAddress();
//				detailedAddress = province + city + area + " " + addr;
//			} catch (Exception e) {
//				logger.error("【行政区域获取出错】获取收货地址列表错误！关键参数：{}", userId);
//				e.printStackTrace();
//			}
//			addressVo.put("detailedAddress", detailedAddress);
//
//			addressVoList.add(addressVo);
//		}
//		logger.info("【请求结束】获取收货地址列表,响应结果:{}", JSONObject.toJSONString(addressVoList));

		return ResponseUtil.ok(addressList);
	}

	@ApiOperation(value = "收货地址详情")
	@GetMapping("detail")
	public Object detail(@RequestParam @ApiParam("用户id") Integer userId, @RequestParam @ApiParam("收货地址id") Integer id) {
		logger.info("【请求开始】获取收货地址详情,请求参数,userId：{},id:{}", userId, id);
		if (userId == null) {
			return ResponseUtil.unlogin();
		}

		DtsAddress address = addressService.findById(id);
		if (address == null) {
			return ResponseUtil.badArgumentValue();
		}

		Map<Object, Object> data = new HashMap<Object, Object>();
		data.put("id", address.getId());
		data.put("name", address.getName());
		data.put("provinceId", address.getProvinceId());
		data.put("cityId", address.getCityId());
		data.put("areaId", address.getAreaId());
		data.put("mobile", address.getMobile());
		data.put("address", address.getAddress());
		data.put("isDefault", address.getIsDefault());
		String pname = regionService.findById(address.getProvinceId()).getName();
		data.put("provinceName", pname);
		String cname = regionService.findById(address.getCityId()).getName();
		data.put("cityName", cname);
		String dname = regionService.findById(address.getAreaId()).getName();
		data.put("areaName", dname);

		logger.info("【请求结束】获取收货地址详情,响应结果：{}", userId, id, JSONObject.toJSONString(data));
		return ResponseUtil.ok(data);

	}

	private Object validate(DtsAddress address) {
		String name = address.getName();
		if (StringUtils.isEmpty(name)) {
			return ResponseUtil.badArgument();
		}

		// 测试收货手机号码是否正确
		String mobile = address.getMobile();
		if (StringUtils.isEmpty(mobile)) {
			return ResponseUtil.badArgument();
		}

		Integer pid = address.getProvinceId();
		if (pid == null) {
			return ResponseUtil.badArgument();
		}
		if (regionService.findById(pid) == null) {
			return ResponseUtil.badArgumentValue();
		}

		Integer cid = address.getCityId();
		if (cid == null) {
			return ResponseUtil.badArgument();
		}
		if (regionService.findById(cid) == null) {
			return ResponseUtil.badArgumentValue();
		}

		Integer aid = address.getAreaId();
		if (aid == null) {
			return ResponseUtil.badArgument();
		}
		if (regionService.findById(aid) == null) {
			return ResponseUtil.badArgumentValue();
		}

		String detailedAddress = address.getAddress();
		if (StringUtils.isEmpty(detailedAddress)) {
			return ResponseUtil.badArgument();
		}

		Boolean isDefault = address.getIsDefault();
		if (isDefault == null) {
			return ResponseUtil.badArgument();
		}
		return null;
	}

	/**
	 * 添加或更新收货地址
	 *
	 * @param
	 *
	 * @param address
	 *            用户收货地址
	 * @return 添加或更新操作结果
	 */
	@ApiOperation("添加或更新收货地址")
	@PostMapping("save")
	public Object save(@RequestBody @ApiParam("(用户id,provinceId,cityId,areaId,address为必须,更新地址包括id,添加地址不用id") DtsAddress address) {
		Integer userId = address.getUserId();
		logger.info("【请求开始】添加或更新收货地址,请求参数,userId：{},address:{}", userId, JSONObject.toJSONString(address));
		if (userId == null) {
			return ResponseUtil.unlogin();
		}

		if (address.getIsDefault()) {// 如果设置本次地址为默认地址，则需要重置其他收货地址的默认选项
			addressService.resetDefault(userId);
		}

		if (address.getId() == null || address.getId().equals(0)) {
			address.setId(null);
			address.setUserId(userId);
			addressService.add(address);
		} else { // 更新地址
			address.setUserId(userId);
			if (addressService.update(address) == 0) {
				return ResponseUtil.updatedDataFailed();
			}
		}
		logger.info("【请求结束】添加或更新收货地址,响应结果：{}", address.getId());
		return ResponseUtil.ok(address.getId());
	}

	/**
	 * 删除收货地址
	 *
	 * @param
	 *
	 * @param address
	 *            用户收货地址，{ id: xxx }
	 * @return 删除操作结果
	 */
	@ApiOperation("删除地址")
	@PostMapping("delete")
	public Object delete(@RequestBody DtsAddress address) {
		Integer userId = address.getUserId();
		logger.info("【请求开始】删除收货地址,请求参数,userId：{},address:{}", userId, JSONObject.toJSONString(address));
		if (userId == null) {
			return ResponseUtil.unlogin();
		}
		Integer id = address.getId();
		if (id == null) {
			return ResponseUtil.badArgument();
		}
		addressService.delete(id);
		logger.info("【请求结束】删除收货地址,响应结果：成功");
		return ResponseUtil.ok();
	}
}



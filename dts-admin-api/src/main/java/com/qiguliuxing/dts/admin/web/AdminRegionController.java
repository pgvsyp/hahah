package com.qiguliuxing.dts.admin.web;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.qiguliuxing.dts.admin.util.AuthSupport;
import com.qiguliuxing.dts.core.util.ResponseUtil;
import com.qiguliuxing.dts.core.validator.Order;
import com.qiguliuxing.dts.core.validator.Sort;
import com.qiguliuxing.dts.db.domain.DtsRegion;
import com.qiguliuxing.dts.db.service.DtsRegionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "地域信息操作")
@RestController
@RequestMapping("/admin/region")
@Validated
public class AdminRegionController {
	private static final Logger logger = LoggerFactory.getLogger(AdminRegionController.class);

	@Autowired
	private DtsRegionService regionService;

	@GetMapping("/clist")
	public Object clist(@NotNull Integer id) {
		List<DtsRegion> regionList = regionService.queryByPid(id);
		return ResponseUtil.ok(regionList);
	}

	@GetMapping("/list")
	public Object list(String name, Integer code, @RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "10") Integer limit,
			@Sort(accepts = { "id" }) @RequestParam(defaultValue = "id") String sort,
			@Order @RequestParam(defaultValue = "desc") String order) {
		logger.info("【请求开始】操作人:[" + AuthSupport.userName()+ "] 行政区域管理->查询,请求参数,name:{},code:{},page:{}", name, code, page);

		List<DtsRegion> regionList = regionService.querySelective(name, code, page, limit, sort, order);
		long total = PageInfo.of(regionList).getTotal();
		Map<String, Object> data = new HashMap<>();
		data.put("total", total);
		data.put("items", regionList);

		logger.info("【请求结束】行政区域管理->查询,响应结果:{}", JSONObject.toJSONString(data));
		return ResponseUtil.ok(data);
	}

	@GetMapping("/getRegion")
	@ApiOperation(value = "获取地域信息")
	public Object getRegion() {
		return regionService.getAll();
	}

	@GetMapping("getRegionByPid")
	@ApiOperation(value = "根据pid获取地域信息", notes = "body需要一个pid就行")
	public Object getRegionByPid(@RequestParam Integer pid) {
		logger.info("【请求开始】根据pid获取子区域数据,请求参数,pid:{}", pid);
		List<DtsRegion> regionList = regionService.queryByPid(pid);

		logger.info("【请求结束】根据pid获取子区域数据成功!");
		return ResponseUtil.ok(regionList);
	}
}

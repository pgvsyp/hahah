package com.qiguliuxing.dts.admin.web;

import com.alibaba.fastjson.JSONObject;
import com.qiguliuxing.dts.admin.annotation.RequiresPermissionsDesc;
import com.qiguliuxing.dts.admin.util.AuthSupport;
import com.qiguliuxing.dts.core.qcode.QCodeService;
import com.qiguliuxing.dts.core.util.ResponseUtil;
import com.qiguliuxing.dts.db.domain.DtsTopic;
import com.qiguliuxing.dts.db.service.DtsTopicService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Api(tags = "快报操作接口")
@RestController
@RequestMapping("/admin/topic")
@Validated
public class AdminTopicController {
	private static final Logger logger = LoggerFactory.getLogger(AdminTopicController.class);

	@Autowired
	private DtsTopicService topicService;

	@Autowired
	private QCodeService qCodeService;

	@ApiOperation(value = "快报查询")
	@GetMapping("/list")
	public Object list() {
		List<DtsTopic> dtsTopics = topicService.queryList(1, 5);
		return dtsTopics;
	}




	@RequiresPermissions("admin:topic:read")
	@RequiresPermissionsDesc(menu = { "推广管理", "专题管理" }, button = "详情")
	@GetMapping("/read")
	public Object read(@NotNull Integer id) {
		logger.info("【请求开始】操作人:[" + AuthSupport.userName()+ "] 推广管理->专题管理->详情,请求参数,id:{}", id);

		DtsTopic topic = topicService.findById(id);

		logger.info("【请求结束】推广管理->专题管理->详情:响应结果:{}", JSONObject.toJSONString(topic));
		return ResponseUtil.ok(topic);
	}

	@RequiresPermissions("admin:topic:delete")
	@RequiresPermissionsDesc(menu = { "推广管理", "专题管理" }, button = "删除")
	@PostMapping("/delete")
	public Object delete(@RequestBody DtsTopic topic) {
		logger.info("【请求开始】操作人:[" + AuthSupport.userName()+ "] 推广管理->专题管理->删除,请求参数:{}", JSONObject.toJSONString(topic));

		topicService.deleteById(topic.getId());

		logger.info("【请求结束】推广管理->专题管理->删除,响应结果:{}", "成功!");
		return ResponseUtil.ok();
	}

}

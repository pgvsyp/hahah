package com.qiguliuxing.dts.admin.web;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.qiguliuxing.dts.admin.annotation.RequiresPermissionsDesc;
import com.qiguliuxing.dts.admin.dao.GoodsAllinone;
import com.qiguliuxing.dts.admin.service.AdminDataAuthService;
import com.qiguliuxing.dts.admin.service.AdminGoodsService;
import com.qiguliuxing.dts.admin.util.AuthSupport;
import com.qiguliuxing.dts.core.util.ResponseUtil;
import com.qiguliuxing.dts.core.validator.Order;
import com.qiguliuxing.dts.core.validator.Sort;
import com.qiguliuxing.dts.db.domain.DtsBrand;
import com.qiguliuxing.dts.db.domain.DtsComment;
import com.qiguliuxing.dts.db.domain.DtsGoods;
import com.qiguliuxing.dts.db.domain.DtsUser;
import com.qiguliuxing.dts.db.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Api(tags = "商品操作接口")
@RestController
@RequestMapping("/admin/goods")
@Validated
public class AdminGoodsController {
	private static final Logger logger = LoggerFactory.getLogger(AdminGoodsController.class);

	@Autowired
	private AdminGoodsService adminGoodsService;

	@Autowired
	private DtsGoodsService dtsGoodsService;
	
	@Autowired
	private AdminDataAuthService adminDataAuthService;


	@Autowired
	private DtsGoodsService goodsService;
	@Autowired
	private DtsIssueService goodsIssueService;

	@Autowired
	private DtsGoodsAttributeService goodsAttributeService;
	@Autowired
	private DtsGoodsProductService productService;
	@Autowired
	private DtsGoodsSpecificationService goodsSpecificationService;
	@Autowired
	private DtsBrandService brandService;
	@Autowired
	private DtsCommentService commentService;
	@Autowired
	private DtsUserService userService;
	@Autowired
	private DtsCollectService collectService;

	@Autowired
	private DtsFootprintService footprintService;

	@Autowired
	private DtsCategoryService categoryService;

	@Autowired
	private DtsSearchHistoryService searchHistoryService;
	@Autowired
	private DtsGrouponRulesService rulesService;
	private final static ArrayBlockingQueue<Runnable> WORK_QUEUE = new ArrayBlockingQueue<>(9);

	private final static RejectedExecutionHandler HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();

	private static ThreadPoolExecutor executorService = new ThreadPoolExecutor(16, 16, 1000, TimeUnit.MILLISECONDS,
			WORK_QUEUE, HANDLER);
	/**
	 * 查询商品
	 *
	 * @param goodsSn
	 * @param name
	 * @param page
	 * @param limit
	 * @param sort
	 * @param order
	 * @return
	 */
	@RequiresPermissions("admin:goods:list")
	@RequiresPermissionsDesc(menu = { "商品管理", "商品管理" }, button = "查询")
	@GetMapping("/list")
	public Object list(String goodsSn, String name, @RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "10") Integer limit,
			@Sort @RequestParam(defaultValue = "add_time") String sort,
			@Order @RequestParam(defaultValue = "desc") String order) {
		logger.info("【请求开始】操作人:[" + AuthSupport.userName()+ "] 商品管理->商品管理->查询,请求参数:goodsSn:{},name:{},page:{}", goodsSn, name, page);

	    //需要区分数据权限，如果属于品牌商管理员，则需要获取当前用户管理品牌店铺
		List<Integer> brandIds = null;
		if (adminDataAuthService.isBrandManager()) {
			brandIds = adminDataAuthService.getBrandIds();
			logger.info("运营商管理角色操作，需控制数据权限，brandIds:{}",JSONObject.toJSONString(brandIds));

			if (brandIds == null || brandIds.size() == 0) {
				Map<String, Object> data = new HashMap<>();
				data.put("total", 0L);
				data.put("items", null);

				logger.info("【请求结束】商品管理->商品管理->查询,响应结果:{}", JSONObject.toJSONString(data));
				return ResponseUtil.ok(data);
			}
		}
		
		return adminGoodsService.list(goodsSn, name, page, limit, sort, order, brandIds);
	}

	@ApiOperation(value = "轮播图商品查询")
	@GetMapping("/lbList")
	public Object lbList() {
		List<DtsGoods> list = new ArrayList<>();
		list.add(dtsGoodsService.findById(23851));
		list.add(dtsGoodsService.findById(23848));
		list.add(dtsGoodsService.findById(23835));
		list.add(dtsGoodsService.findById(23050));
		list.add(dtsGoodsService.findById(23044));
		list.add(dtsGoodsService.findById(23040));
		return list;
	}

	@ApiOperation(value = "秒杀商品查询")
	@GetMapping("/seckillList")
	public Object seckillList() {
		List<DtsGoods> dtsGoods = dtsGoodsService.queryBySeckill();
		return dtsGoods;
	}

	@ApiOperation(value = "随机商品查询")
	@GetMapping("/randomList")
	public Object randomList() {
		DtsGoods dtsGoods = dtsGoodsService.queryByRandom();
		return dtsGoods;
	}

	@ApiOperation(value = "特价商品查询")
	@GetMapping("/queryBySpecial")
	public Object queryBySpecial() {
		List<DtsGoods> dtsGoods = dtsGoodsService.queryBySpecial();
		return dtsGoods;
	}

	@ApiOperation(value = "品牌商品查询")
	@GetMapping("/queryByBrand")
	public Object queryByBrand() {
		Map<Object, Object> data = new HashMap<>();
		List<DtsGoods> dtsGoods = dtsGoodsService.queryByBrand();
		data.put("品牌", "小米童鞋");
		data.put("商品", dtsGoods);
		return data;
	}

	@ApiOperation(value = "频道1(童鞋)商品查询")
	@GetMapping("/queryByCategory1")
	public Object queryByCategory1() {
		Map<Object, Object> data = new HashMap<>();
		List<DtsGoods> dtsGoods = dtsGoodsService.queryByCategory1();
		data.put("频道", "童鞋");
		data.put("商品", dtsGoods);
		return data;
	}

	@ApiOperation(value = "频道2(女宝专区)商品查询")
	@GetMapping("/queryByCategory2")
	public Object queryByCategory2() {
		Map<Object, Object> data = new HashMap<>();
		List<DtsGoods> dtsGoods = dtsGoodsService.queryByCategory2();
		data.put("频道", "女宝专区");
		data.put("商品", dtsGoods);
		return data;
	}
	@ApiOperation(value = "频道3(当季热销)商品查询")
	@GetMapping("/queryByCategory3")
	public Object queryByCategory3() {
		Map<Object, Object> data = new HashMap<>();
		List<DtsGoods> dtsGoods = dtsGoodsService.queryByCategory3();
		data.put("频道", "当季热销");
		data.put("商品", dtsGoods);
		return data;
	}
	@ApiOperation(value = "频道4(居家日用)商品查询")
	@GetMapping("/queryByCategory4")
	public Object queryByCategory4() {
		Map<Object, Object> data = new HashMap<>();
		List<DtsGoods> dtsGoods = dtsGoodsService.queryByCategory4();
		data.put("频道", "居家日用");
		data.put("商品", dtsGoods);
		return data;
	}
	@ApiOperation(value = "频道5(家用电器)商品查询")
	@GetMapping("/queryByCategory5")
	public Object queryByCategory5() {
		Map<Object, Object> data = new HashMap<>();
		List<DtsGoods> dtsGoods = dtsGoodsService.queryByCategory5();
		data.put("频道", "家用电器");
		data.put("商品", dtsGoods);
		return data;
	}
	@ApiOperation(value = "频道6(学习用品)商品查询")
	@GetMapping("/queryByCategory6")
	public Object queryByCategory6() {
		Map<Object, Object> data = new HashMap<>();
		List<DtsGoods> dtsGoods = dtsGoodsService.queryByCategory6();
		data.put("频道", "学习用品");
		data.put("商品", dtsGoods);
		return data;
	}
	@ApiOperation(value = "频道7(水果)商品查询")
	@GetMapping("/queryByCategory7")
	public Object queryByCategory7() {
		Map<Object, Object> data = new HashMap<>();
		List<DtsGoods> dtsGoods = dtsGoodsService.queryByCategory7();
		data.put("频道", "水果");
		data.put("商品", dtsGoods);
		return data;
	}
	@ApiOperation(value = "频道8(美妆日化)商品查询")
	@GetMapping("/queryByCategory8")
	public Object queryByCategory8() {
		Map<Object, Object> data = new HashMap<>();
		List<DtsGoods> dtsGoods = dtsGoodsService.queryByCategory8();
		data.put("频道", "美妆日化");
		data.put("商品", dtsGoods);
		return data;
	}


	@GetMapping("/catAndBrand")
	public Object catAndBrand() {
		return adminGoodsService.catAndBrand();
	}

	/**
	 * 编辑商品
	 *
	 * @param goodsAllinone
	 * @return
	 */
	@RequiresPermissions("admin:goods:update")
	@RequiresPermissionsDesc(menu = { "商品管理", "商品管理" }, button = "编辑")
	@PostMapping("/update")
	public Object update(@RequestBody GoodsAllinone goodsAllinone) {
		logger.info("【请求开始】操作人:[" + AuthSupport.userName()+ "] 商品管理->商品管理->编辑,请求参数:{}", JSONObject.toJSONString(goodsAllinone));

		return adminGoodsService.update(goodsAllinone);
	}

	/**
	 * 删除商品
	 *
	 * @param goods
	 * @return
	 */
	@RequiresPermissions("admin:goods:delete")
	@RequiresPermissionsDesc(menu = { "商品管理", "商品管理" }, button = "删除")
	@PostMapping("/delete")
	public Object delete(@RequestBody DtsGoods goods) {
		logger.info("【请求开始】操作人:[" + AuthSupport.userName()+ "] 商品管理->商品管理->删除,请求参数:{}", JSONObject.toJSONString(goods));

		return adminGoodsService.delete(goods);
	}

	/**
	 * 添加商品
	 *
	 * @param goodsAllinone
	 * @return
	 */
	@RequiresPermissions("admin:goods:create")
	@RequiresPermissionsDesc(menu = { "商品管理", "商品管理" }, button = "上架")
	@PostMapping("/create")
	public Object create(@RequestBody GoodsAllinone goodsAllinone) {
		logger.info("【请求开始】操作人:[" + AuthSupport.userName()+ "] 商品管理->商品管理->上架,请求参数:{}", JSONObject.toJSONString(goodsAllinone));

		return adminGoodsService.create(goodsAllinone);
	}

//	/**
//	 * 商品详情
//	 *
//	 * @param id
//	 * @return
//	 */
//	@RequiresPermissions("admin:goods:read")
//	@RequiresPermissionsDesc(menu = { "商品管理", "商品管理" }, button = "详情")
//	@GetMapping("/detail")
//	public Object detail(@RequestParam Integer id) {
//		logger.info("【请求开始】操作人:[" + AuthSupport.userName()+ "] 商品管理->商品管理->详情,请求参数,id:{}", id);
//
//		return adminGoodsService.detail(id);
//	}
	@ApiOperation("商品详情查询")
	@GetMapping("detail")
	public Object detail(@RequestParam @ApiParam("商品id") Integer id) {
		logger.info("【请求开始】商品详情,请求参数,id:{}", id);

		// 商品信息
		DtsGoods info = goodsService.findById(id);

		// 商品属性
		Callable<List> goodsAttributeListCallable = () -> goodsAttributeService.queryByGid(id);

		// 商品规格 返回的是定制的GoodsSpecificationVo
		Callable<Object> objectCallable = () -> goodsSpecificationService.getSpecificationVoList(id);

		// 商品规格对应的数量和价格
		Callable<List> productListCallable = () -> productService.queryByGid(id);

		// 商品问题，这里是一些通用问题
		Callable<List> issueCallable = () -> goodsIssueService.query();

		// 商品品牌商
		Callable<DtsBrand> brandCallable = () -> {
			Integer brandId = info.getBrandId();
			DtsBrand brand;
			if (brandId == 0) {
				brand = new DtsBrand();
			} else {
				brand = brandService.findById(info.getBrandId());
			}
			return brand;
		};

		// 评论
		Callable<Map> commentsCallable = () -> {
			List<DtsComment> comments = commentService.queryGoodsByGid(id, 0, 2);
			List<Map<String, Object>> commentsVo = new ArrayList<>(comments.size());
			long commentCount = PageInfo.of(comments).getTotal();
			for (DtsComment comment : comments) {
				Map<String, Object> c = new HashMap<>();
				c.put("id", comment.getId());
				c.put("addTime", comment.getAddTime());
				c.put("content", comment.getContent());
				DtsUser user = userService.findById(comment.getUserId());
				c.put("nickname", user.getNickname());
				c.put("avatar", user.getAvatar());
				c.put("picList", comment.getPicUrls());
				commentsVo.add(c);
			}
			Map<String, Object> commentList = new HashMap<>();
			commentList.put("count", commentCount);
			commentList.put("data", commentsVo);
			return commentList;
		};



		FutureTask<List> goodsAttributeListTask = new FutureTask<>(goodsAttributeListCallable);
		FutureTask<Object> objectCallableTask = new FutureTask<>(objectCallable);
		FutureTask<List> productListCallableTask = new FutureTask<>(productListCallable);
		FutureTask<List> issueCallableTask = new FutureTask<>(issueCallable);
		FutureTask<Map> commentsCallableTsk = new FutureTask<>(commentsCallable);
		FutureTask<DtsBrand> brandCallableTask = new FutureTask<>(brandCallable);

		executorService.submit(goodsAttributeListTask);
		executorService.submit(objectCallableTask);
		executorService.submit(productListCallableTask);
		executorService.submit(issueCallableTask);
		executorService.submit(commentsCallableTsk);
		executorService.submit(brandCallableTask);

		Map<String, Object> data = new HashMap<>();

		try {
			data.put("info", info);
			data.put("issue", issueCallableTask.get());
			data.put("comment", commentsCallableTsk.get());
			data.put("specificationList", objectCallableTask.get());
			data.put("productList", productListCallableTask.get());
			data.put("attribute", goodsAttributeListTask.get());
			data.put("brand", brandCallableTask.get());
		} catch (Exception e) {
			logger.error("获取商品详情出错:{}", e.getMessage());
			e.printStackTrace();
		}

		// 商品分享图片地址
//		data.put("shareImage", info.getShareUrl());

		logger.info("【请求结束】获取商品详情成功!");// 这里不打印返回的信息，因为此接口查询量大，太耗日志空间
		return ResponseUtil.ok(data);
	}


}

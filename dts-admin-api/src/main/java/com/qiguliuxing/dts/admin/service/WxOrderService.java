package com.qiguliuxing.dts.admin.service;

import com.alibaba.fastjson.JSONObject;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.pagehelper.PageInfo;
import com.qiguliuxing.dts.admin.dao.BrandCartGoods;
import com.qiguliuxing.dts.admin.dao.BrandOrderGoods;
import com.qiguliuxing.dts.admin.util.IpUtil;
import com.qiguliuxing.dts.admin.util.WxResponseCode;
import com.qiguliuxing.dts.admin.util.WxResponseUtil;
import com.qiguliuxing.dts.core.consts.CommConsts;
import com.qiguliuxing.dts.core.notify.NotifyService;
import com.qiguliuxing.dts.core.system.SystemConfig;
import com.qiguliuxing.dts.core.util.JacksonUtil;
import com.qiguliuxing.dts.core.util.ResponseUtil;
import com.qiguliuxing.dts.db.domain.*;
import com.qiguliuxing.dts.db.service.*;
import com.qiguliuxing.dts.db.util.CouponUserConstant;
import com.qiguliuxing.dts.db.util.OrderHandleOption;
import com.qiguliuxing.dts.db.util.OrderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.qiguliuxing.dts.admin.util.WxResponseCode.*;


/**
 * 订单服务
 *
 * <p>
 * 订单状态： 101 订单生成，未支付；102，下单后未支付用户取消；103，下单后未支付超时系统自动取消 201
 * 支付完成，商家未发货；202，订单生产，已付款未发货，但是退款取消； 301 商家发货，用户未确认； 401 用户确认收货； 402
 * 用户没有确认收货超过一定时间，系统自动确认收货；
 *
 * <p>
 * 用户操作： 当101用户未付款时，此时用户可以进行的操作是取消订单，或者付款操作 当201支付完成而商家未发货时，此时用户可以取消订单并申请退款
 * 当301商家已发货时，此时用户可以有确认收货的操作 当401用户确认收货以后，此时用户可以进行的操作是删除订单，评价商品，或者再次购买
 * 当402系统自动确认收货以后，此时用户可以删除订单，评价商品，或者再次购买
 *
 * <p>
 * 注意：目前不支持订单退货和售后服务
 */
@Service
public class WxOrderService {
	private static final Logger logger = LoggerFactory.getLogger(WxOrderService.class);

	@Autowired
	private DtsUserService userService;
	@Autowired
	private DtsOrderService orderService;
	@Autowired
	private DtsOrderGoodsService orderGoodsService;
	@Autowired
	private DtsAddressService addressService;
	@Autowired
	private DtsCartService cartService;
	@Autowired
	private DtsRegionService regionService;
	@Autowired
	private DtsGoodsProductService productService;
	@Autowired
	private WxPayService wxPayService;
	@Autowired
	private NotifyService notifyService;
	@Autowired
	private DtsUserFormIdService formIdService;
	@Autowired
	private DtsGrouponRulesService grouponRulesService;
	@Autowired
	private DtsGrouponService grouponService;
	@Autowired
	private DtsCommentService commentService;
	@Autowired
	private DtsCouponUserService couponUserService;
	@Autowired
	private CouponVerifyService couponVerifyService;
	@Autowired
	private DtsAccountService accountService;

	private String detailedAddress(DtsAddress DtsAddress) {
		Integer provinceId = DtsAddress.getProvinceId();
		Integer cityId = DtsAddress.getCityId();
		Integer areaId = DtsAddress.getAreaId();
		String provinceName = regionService.findById(provinceId).getName();
		String cityName = regionService.findById(cityId).getName();
		String areaName = regionService.findById(areaId).getName();
		String fullRegion = provinceName + " " + cityName + " " + areaName;
		return fullRegion + " " + DtsAddress.getAddress();
	}

	/**
	 * 订单列表
	 *
	 * @param userId
	 *            用户ID
	 * @param showType
	 *            订单信息： 0，全部订单； 1，待付款； 2，待发货； 3，待收货； 4，待评价。
	 * @param page
	 *            分页页数
	 * @param size
	 *            分页大小
	 * @return 订单列表
	 */
	public Object list(Integer userId, Integer showType, Integer page, Integer size) {

		List<Short> orderStatus = OrderUtil.orderStatus(showType);
		List<DtsOrder> orderList = orderService.queryByOrderStatus(userId, orderStatus, page, size);
		long count = PageInfo.of(orderList).getTotal();
		int totalPages = (int) Math.ceil((double) count / size);

		List<Map<String, Object>> orderVoList = new ArrayList<>(orderList.size());
		for (DtsOrder order : orderList) {
			Map<String, Object> orderVo = new HashMap<>();
			orderVo.put("id", order.getId());
			orderVo.put("orderSn", order.getOrderSn());
			orderVo.put("addTime", order.getAddTime());
			orderVo.put("consignee", order.getConsignee());
			orderVo.put("mobile", order.getMobile());
			orderVo.put("address", order.getAddress());
			orderVo.put("goodsPrice", order.getGoodsPrice());
			orderVo.put("freightPrice", order.getFreightPrice());
			orderVo.put("actualPrice", order.getActualPrice());
			orderVo.put("orderStatusText", OrderUtil.orderStatusText(order));
			orderVo.put("handleOption", OrderUtil.build(order));
			orderVo.put("expCode", order.getShipChannel());
			orderVo.put("expNo", order.getShipSn());

			DtsGroupon groupon = grouponService.queryByOrderId(order.getId());
			if (groupon != null) {
				orderVo.put("isGroupin", true);
			} else {
				orderVo.put("isGroupin", false);
			}

			List<DtsOrderGoods> orderGoodsList = orderGoodsService.queryByOid(order.getId());
			/*
			 * List<Map<String, Object>> orderGoodsVoList = new
			 * ArrayList<>(orderGoodsList.size()); for (DtsOrderGoods orderGoods :
			 * orderGoodsList) { Map<String, Object> orderGoodsVo = new HashMap<>();
			 * orderGoodsVo.put("id", orderGoods.getId()); orderGoodsVo.put("goodsName",
			 * orderGoods.getGoodsName()); orderGoodsVo.put("number",
			 * orderGoods.getNumber()); orderGoodsVo.put("picUrl", orderGoods.getPicUrl());
			 * orderGoodsVo.put("price", orderGoods.getPrice()); orderGoodsVo.put("picUrl",
			 * orderGoods.getPicUrl()); orderGoodsVoList.add(orderGoodsVo); }
			 */
			orderVo.put("goodsList", orderGoodsList);

			orderVoList.add(orderVo);
		}

		Map<String, Object> result = new HashMap<>();
		result.put("count", count);
		result.put("data", orderVoList);
		result.put("totalPages", totalPages);

		logger.info("【请求结束】获取订单列表,响应结果:{}", JSONObject.toJSONString(result));
		return ResponseUtil.ok(result);
	}

	/**
	 * 订单详情
	 *
	 * @param userId
	 *            用户ID
	 * @param orderId
	 *            订单ID
	 * @return 订单详情
	 */
	public Object detail(Integer userId, Integer orderId) {
		if (userId == null) {
			logger.error("获取订单详情失败：用户未登录!");
			return ResponseUtil.unlogin();
		}

		// 订单信息
		DtsOrder order = orderService.findById(orderId);

		Map<String, Object> orderVo = new HashMap<String, Object>();
		orderVo.put("id", order.getId());
		orderVo.put("orderSn", order.getOrderSn());
		orderVo.put("addTime", order.getAddTime());
		orderVo.put("consignee", order.getConsignee());
		orderVo.put("mobile", order.getMobile());
		orderVo.put("address", order.getAddress());
		orderVo.put("goodsPrice", order.getGoodsPrice());
		orderVo.put("freightPrice", order.getFreightPrice());
		orderVo.put("discountPrice", order.getIntegralPrice().add(order.getGrouponPrice()).add(order.getCouponPrice()));
		orderVo.put("actualPrice", order.getActualPrice());
		orderVo.put("orderStatusText", OrderUtil.orderStatusText(order));
		orderVo.put("handleOption", OrderUtil.build(order));
		orderVo.put("expCode", order.getShipChannel());
		orderVo.put("expNo", order.getShipSn());

		List<DtsOrderGoods> orderGoodsList = orderGoodsService.queryByOid(order.getId());

		Map<String, Object> result = new HashMap<>();
		result.put("orderInfo", orderVo);
		result.put("orderGoods", orderGoodsList);

		// 订单状态为已发货且物流信息不为空
		// "YTO", "800669400640887922"
		/*
		 * if (order.getOrderStatus().equals(OrderUtil.STATUS_SHIP)) { ExpressInfo ei =
		 * expressService.getExpressInfo(order.getShipChannel(), order.getShipSn());
		 * result.put("expressInfo", ei); }
		 */

		logger.info("【请求结束】获取订单详情,响应结果:{}", JSONObject.toJSONString(result));
		return ResponseUtil.ok(result);

	}

	/**
	 * 提交订单
	 * <p>
	 * 1. 创建订单表项和订单商品表项; 2. 购物车清空; 3. 优惠券设置已用; 4. 商品货品库存减少; 5. 如果是团购商品，则创建团购活动表项。
	 *
	 * @param userId
	 *            用户ID
	 * @param body
	 *            订单信息，{ cartId：xxx, addressId: xxx, couponId: xxx, message: xxx,
	 *            grouponRulesId: xxx, grouponLinkId: xxx}
	 * @return 提交订单操作结果
	 */
	@Transactional
	public Object submit(Integer userId, String body) {
		if (userId == null) {
			logger.error("提交订单详情失败：用户未登录!");
			return ResponseUtil.unlogin();
		}
		if (body == null) {
			return ResponseUtil.badArgument();
		}
		Integer cartId = JacksonUtil.parseInteger(body, "cartId");
		Integer addressId = JacksonUtil.parseInteger(body, "addressId");
		Integer couponId = JacksonUtil.parseInteger(body, "couponId");
		String message = JacksonUtil.parseString(body, "message");
		Integer grouponRulesId = JacksonUtil.parseInteger(body, "grouponRulesId");
		Integer grouponLinkId = JacksonUtil.parseInteger(body, "grouponLinkId");

		// 如果是团购项目,验证活动是否有效
		if (grouponRulesId != null && grouponRulesId > 0) {
			DtsGrouponRules rules = grouponRulesService.queryById(grouponRulesId);
			// 找不到记录
			if (rules == null) {
				return ResponseUtil.badArgument();
			}
			// 团购活动已经过期
			if (grouponRulesService.isExpired(rules)) {
				logger.error("提交订单详情失败：{}", GROUPON_EXPIRED.desc());
				return WxResponseUtil.fail(GROUPON_EXPIRED);
			}
		}

		if (cartId == null || addressId == null || couponId == null) {
			return ResponseUtil.badArgument();
		}

		// 收货地址
		DtsAddress checkedAddress = addressService.findById(addressId);
		if (checkedAddress == null) {
			return ResponseUtil.badArgument();
		}

		// 团购优惠
		BigDecimal grouponPrice = new BigDecimal(0.00);
		DtsGrouponRules grouponRules = grouponRulesService.queryById(grouponRulesId);
		if (grouponRules != null) {
			grouponPrice = grouponRules.getDiscount();
		}

		// 货品价格
		List<DtsCart> checkedGoodsList = null;
		if (cartId.equals(0)) {
			checkedGoodsList = cartService.queryByUidAndChecked(userId);
		} else {
			DtsCart cart = cartService.findById(cartId);
			checkedGoodsList = new ArrayList<>(1);
			checkedGoodsList.add(cart);
		}
		if (checkedGoodsList.size() == 0) {
			return ResponseUtil.badArgumentValue();
		}

		BigDecimal goodsTotalPrice = new BigDecimal(0.00);// 商品总价 （包含团购减免，即减免团购后的商品总价，多店铺需将所有商品相加）
		BigDecimal totalFreightPrice = new BigDecimal(0.00);// 总配送费 （单店铺模式一个，多店铺模式多个配送费的总和）
		// 如果需要拆订单，则按店铺进行归类，在计算邮费
		if (SystemConfig.isMultiOrderModel()) {
			// a.按入驻店铺归类checkout商品
			List<BrandCartGoods> brandCartgoodsList = new ArrayList<BrandCartGoods>();
			for (DtsCart cart : checkedGoodsList) {
				Integer brandId = cart.getBrandId();
				boolean hasExsist = false;
				for (int i = 0; i < brandCartgoodsList.size(); i++) {
					if (brandCartgoodsList.get(i).getBrandId().intValue() == brandId.intValue()) {
						brandCartgoodsList.get(i).getCartList().add(cart);
						hasExsist = true;
						break;
					}
				}
				if (!hasExsist) {// 还尚未加入，则新增一类
					BrandCartGoods bandCartGoods = new BrandCartGoods();
					bandCartGoods.setBrandId(brandId);
					List<DtsCart> dtsCartList = new ArrayList<DtsCart>();
					dtsCartList.add(cart);
					bandCartGoods.setCartList(dtsCartList);
					brandCartgoodsList.add(bandCartGoods);
				}
			}
			// b.核算每个店铺的商品总价，用于计算邮费
			for (BrandCartGoods bcg : brandCartgoodsList) {
				List<DtsCart> bandCarts = bcg.getCartList();
				BigDecimal bandGoodsTotalPrice = new BigDecimal(0.00);
				BigDecimal bandFreightPrice = new BigDecimal(0.00);
				for (DtsCart cart : bandCarts) {// 循环店铺各自的购物商品
					// 只有当团购规格商品ID符合才进行团购优惠
					if (grouponRules != null && grouponRules.getGoodsId().equals(cart.getGoodsId())) {
						bandGoodsTotalPrice = bandGoodsTotalPrice
								.add(cart.getPrice().subtract(grouponPrice).multiply(new BigDecimal(cart.getNumber())));
					} else {
						bandGoodsTotalPrice = bandGoodsTotalPrice
								.add(cart.getPrice().multiply(new BigDecimal(cart.getNumber())));
					}
				}

				// 每个店铺都单独计算运费，满66则免运费，否则6元；
				if (bandGoodsTotalPrice.compareTo(SystemConfig.getFreightLimit()) < 0) {
					bandFreightPrice = SystemConfig.getFreight();
				}
				goodsTotalPrice = goodsTotalPrice.add(bandGoodsTotalPrice);
				totalFreightPrice = totalFreightPrice.add(bandFreightPrice);
			}
		} else {// 单个店铺模式
			for (DtsCart checkGoods : checkedGoodsList) {
				// 只有当团购规格商品ID符合才进行团购优惠
				if (grouponRules != null && grouponRules.getGoodsId().equals(checkGoods.getGoodsId())) {
					goodsTotalPrice = goodsTotalPrice.add(checkGoods.getPrice().subtract(grouponPrice)
							.multiply(new BigDecimal(checkGoods.getNumber())));
				} else {
					goodsTotalPrice = goodsTotalPrice
							.add(checkGoods.getPrice().multiply(new BigDecimal(checkGoods.getNumber())));
				}
			}
			// 根据订单商品总价计算运费，满足条件（例如66元）则免运费，否则需要支付运费（例如6元）；
			if (goodsTotalPrice.compareTo(SystemConfig.getFreightLimit()) < 0) {
				totalFreightPrice = SystemConfig.getFreight();
			}
		}

		// 获取可用的优惠券信息 使用优惠券减免的金额
		BigDecimal couponPrice = new BigDecimal(0.00);
		// 如果couponId=0则没有优惠券，couponId=-1则不使用优惠券
		if (couponId != 0 && couponId != -1) {
			DtsCoupon coupon = couponVerifyService.checkCoupon(userId, couponId, goodsTotalPrice);
			if (coupon == null) {
				return ResponseUtil.badArgumentValue();
			}
			couponPrice = coupon.getDiscount();
		}

		// 可以使用的其他钱，例如用户积分
		BigDecimal integralPrice = new BigDecimal(0.00);

		// 订单费用
		BigDecimal orderTotalPrice = goodsTotalPrice.add(totalFreightPrice).subtract(couponPrice);
		// 最终支付费用
		BigDecimal actualPrice = orderTotalPrice.subtract(integralPrice);

		Integer orderId = null;
		DtsOrder order = null;
		// 订单
		order = new DtsOrder();
		order.setUserId(userId);
		order.setOrderSn(orderService.generateOrderSn(userId));
		order.setOrderStatus(OrderUtil.STATUS_CREATE);
		order.setConsignee(checkedAddress.getName());
		order.setMobile(checkedAddress.getMobile());
		order.setMessage(message);
		String detailedAddress = detailedAddress(checkedAddress);
		order.setAddress(detailedAddress);
		order.setGoodsPrice(goodsTotalPrice);
		order.setFreightPrice(totalFreightPrice);
		order.setCouponPrice(couponPrice);
		order.setIntegralPrice(integralPrice);
		order.setOrderPrice(orderTotalPrice);
		order.setActualPrice(actualPrice);

		// 有团购活动
		if (grouponRules != null) {
			order.setGrouponPrice(grouponPrice); // 团购价格
		} else {
			order.setGrouponPrice(new BigDecimal(0.00)); // 团购价格
		}

		// 新增代理的结算金额计算
		DtsUser user = userService.findById(userId);
		Integer shareUserId = 1;//
		if (user != null && user.getShareUserId() != null) {
			shareUserId = user.getShareUserId();
		}
		Integer settlementRate = 5;// 默认百分之5
		DtsUserAccount userAccount = accountService.findShareUserAccountByUserId(shareUserId);
		if (userAccount != null && userAccount.getSettlementRate() > 0 && userAccount.getSettlementRate() < 15) {
			settlementRate = userAccount.getSettlementRate();
		}
		BigDecimal rate = new BigDecimal(settlementRate * 0.01);
		BigDecimal settlementMoney = (actualPrice.subtract(totalFreightPrice)).multiply(rate);
		order.setSettlementMoney(settlementMoney.setScale(2, BigDecimal.ROUND_DOWN));

		// 添加订单表项
		orderService.add(order);
		orderId = order.getId();

		// 添加订单商品表项
		for (DtsCart cartGoods : checkedGoodsList) {
			// 订单商品
			DtsOrderGoods orderGoods = new DtsOrderGoods();
			orderGoods.setOrderId(order.getId());
			orderGoods.setGoodsId(cartGoods.getGoodsId());
			orderGoods.setGoodsSn(cartGoods.getGoodsSn());
			orderGoods.setProductId(cartGoods.getProductId());
			orderGoods.setGoodsName(cartGoods.getGoodsName());
			orderGoods.setPicUrl(cartGoods.getPicUrl());
			orderGoods.setPrice(cartGoods.getPrice());
			orderGoods.setNumber(cartGoods.getNumber());
			orderGoods.setSpecifications(cartGoods.getSpecifications());
			orderGoods.setAddTime(LocalDateTime.now());

			orderGoods.setBrandId(cartGoods.getBrandId());// 订单商品需加上入驻店铺标志

			orderGoodsService.add(orderGoods);
		}

		// 删除购物车里面的商品信息
		cartService.clearGoods(userId);

		// 商品货品数量减少
		for (DtsCart checkGoods : checkedGoodsList) {
			Integer productId = checkGoods.getProductId();
			DtsGoodsProduct product = productService.findById(productId);

			Integer remainNumber = product.getNumber() - checkGoods.getNumber();
			if (remainNumber < 0) {
				throw new RuntimeException("下单的商品货品数量大于库存量");
			}
			if (productService.reduceStock(productId, checkGoods.getGoodsId(), checkGoods.getNumber()) == 0) {
				throw new RuntimeException("商品货品库存减少失败");
			}
		}

		// 如果使用了优惠券，设置优惠券使用状态
		if (couponId != 0 && couponId != -1) {
			DtsCouponUser couponUser = couponUserService.queryOne(userId, couponId);
			couponUser.setStatus(CouponUserConstant.STATUS_USED);
			couponUser.setUsedTime(LocalDateTime.now());
			couponUser.setOrderSn(order.getOrderSn());
			couponUserService.update(couponUser);
		}

		// 如果是团购项目，添加团购信息
		if (grouponRulesId != null && grouponRulesId > 0) {
			DtsGroupon groupon = new DtsGroupon();
			groupon.setOrderId(orderId);
			groupon.setPayed(false);
			groupon.setUserId(userId);
			groupon.setRulesId(grouponRulesId);

			// 参与者
			if (grouponLinkId != null && grouponLinkId > 0) {
				// 参与的团购记录
				DtsGroupon baseGroupon = grouponService.queryById(grouponLinkId);
				groupon.setCreatorUserId(baseGroupon.getCreatorUserId());
				groupon.setGrouponId(grouponLinkId);
				groupon.setShareUrl(baseGroupon.getShareUrl());
			} else {
				groupon.setCreatorUserId(userId);
				groupon.setGrouponId(0);
			}

			grouponService.createGroupon(groupon);
		}

		Map<String, Object> data = new HashMap<>();
		data.put("orderId", orderId);

		logger.info("【请求结束】提交订单,响应结果:{}", JSONObject.toJSONString(data));
		return ResponseUtil.ok(data);
	}

	/**
	 * 取消订单
	 * <p>
	 * 1. 检测当前订单是否能够取消； 2. 设置订单取消状态； 3. 商品货品库存恢复； 4. TODO 优惠券； 5. TODO 团购活动。
	 *
	 * @param userId
	 *            用户ID
	 * @param body
	 *            订单信息，{ orderId：xxx }
	 * @return 取消订单操作结果
	 */
	@Transactional
	public Object cancel(Integer userId, String body) {
		if (userId == null) {
			logger.error("取消订单失败：用户未登录!");
			return ResponseUtil.unlogin();
		}
		Integer orderId = JacksonUtil.parseInteger(body, "orderId");
		if (orderId == null) {
			return ResponseUtil.badArgument();
		}

		DtsOrder order = orderService.findById(orderId);
		if (order == null) {
			return ResponseUtil.badArgumentValue();
		}
		if (!order.getUserId().equals(userId)) {
			return ResponseUtil.badArgumentValue();
		}

		// 检测是否能够取消
		OrderHandleOption handleOption = OrderUtil.build(order);
		if (!handleOption.isCancel()) {
			logger.error("取消订单失败：{}", ORDER_INVALID_OPERATION.desc());
			return WxResponseUtil.fail(ORDER_INVALID_OPERATION);
		}

		// 设置订单已取消状态
		order.setOrderStatus(OrderUtil.STATUS_CANCEL);
		order.setEndTime(LocalDateTime.now());
		if (orderService.updateWithOptimisticLocker(order) == 0) {
			throw new RuntimeException("更新数据已失效");
		}

		// 商品货品数量增加
		List<DtsOrderGoods> orderGoodsList = orderGoodsService.queryByOid(orderId);
		for (DtsOrderGoods orderGoods : orderGoodsList) {
			Integer productId = orderGoods.getProductId();
			Short number = orderGoods.getNumber();
			if (productService.addStock(productId, number) == 0) {
				throw new RuntimeException("商品货品库存增加失败");
			}
		}

		logger.info("【请求结束】用户取消订单,响应结果:{}", "成功");
		return ResponseUtil.ok();
	}

	/**
	 * 付款订单的预支付会话标识
	 * <p>
	 * 1. 检测当前订单是否能够付款 2. 微信商户平台返回支付订单ID 3. 设置订单付款状态
	 *
	 * @param userId
	 *            用户ID
	 * @param body
	 *            订单信息，{ orderId：xxx }
	 * @return 支付订单ID
	 */
	@Transactional
	public Object prepay(Integer userId, String body, HttpServletRequest request) {
		if (userId == null) {
			logger.error("付款订单的预支付会话标识失败：用户未登录!");
			return ResponseUtil.unlogin();
		}
		Integer orderId = JacksonUtil.parseInteger(body, "orderId");
		if (orderId == null) {
			return ResponseUtil.badArgument();
		}

		DtsOrder order = orderService.findById(orderId);
		if (order == null) {
			return ResponseUtil.badArgumentValue();
		}
		if (!order.getUserId().equals(userId)) {
			return ResponseUtil.badArgumentValue();
		}

		// 检测是否能够取消
		OrderHandleOption handleOption = OrderUtil.build(order);
		if (!handleOption.isPay()) {
			logger.error("付款订单的预支付会话标识失败：{}", WxResponseCode.ORDER_REPAY_OPERATION.desc());
			return WxResponseUtil.fail(WxResponseCode.ORDER_REPAY_OPERATION);
		}

		DtsUser user = userService.findById(userId);
		String openid = user.getWeixinOpenid();
		if (openid == null) {
			logger.error("付款订单的预支付会话标识失败：{}", AUTH_OPENID_UNACCESS.desc());
			return WxResponseUtil.fail(AUTH_OPENID_UNACCESS);
		}
		WxPayMpOrderResult result = null;
		try {
			WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
			orderRequest.setOutTradeNo(order.getOrderSn());
			orderRequest.setOpenid(openid);
			orderRequest.setBody(CommConsts.DEFAULT_ORDER_FIX + order.getOrderSn());
			// 元转成分
			int fee = 0;
			BigDecimal actualPrice = order.getActualPrice();
			fee = actualPrice.multiply(new BigDecimal(100)).intValue();
			orderRequest.setTotalFee(fee);
			orderRequest.setSpbillCreateIp(IpUtil.getIpAddr(request));

			result = wxPayService.createOrder(orderRequest);

			// 缓存prepayID用于后续模版通知
			String prepayId = result.getPackageValue();
			prepayId = prepayId.replace("prepay_id=", "");
			DtsUserFormid userFormid = new DtsUserFormid();
			userFormid.setOpenid(user.getWeixinOpenid());
			userFormid.setFormid(prepayId);
			userFormid.setIsprepay(true);
			userFormid.setUseamount(3);
			userFormid.setExpireTime(LocalDateTime.now().plusDays(7));
			formIdService.addUserFormid(userFormid);

		} catch (Exception e) {
			logger.error("付款订单的预支付会话标识失败：{}", ORDER_PAY_FAIL.desc());
			e.printStackTrace();
			return WxResponseUtil.fail(ORDER_PAY_FAIL);
		}

		if (orderService.updateWithOptimisticLocker(order) == 0) {
			logger.error("付款订单的预支付会话标识失败：{}", "更新订单信息失败");
			return ResponseUtil.updatedDateExpired();
		}

		logger.info("【请求结束】购物车商品删除成功:清理的productIds:{}", JSONObject.toJSONString(result));
		return ResponseUtil.ok(result);
	}


	/**
	 * 将不变的订单属性复制到子订单
	 * 
	 * @param order
	 * @param childOrder
	 * @return
	 */
	private DtsOrder copyfixedOrderAttr(DtsOrder order, DtsOrder childOrder) {
		if (childOrder != null && order != null) {
			childOrder.setAddress(order.getAddress());
			childOrder.setAddTime(order.getAddTime());
			childOrder.setConsignee(order.getConsignee());
			childOrder.setMessage(order.getMessage());
			childOrder.setMobile(order.getMobile());
			childOrder.setUserId(order.getUserId());
		}
		return childOrder;
	}

	/**
	 * 将订单中的商品按入驻店铺分离归类
	 * 
	 * @param orderGoodsList
	 * @return
	 */
	private List<BrandOrderGoods> divideMultiBrandOrderGoods(List<DtsOrderGoods> orderGoodsList) {
		List<BrandOrderGoods> brandOrderGoodsList = new ArrayList<BrandOrderGoods>();
		for (int i = 0; i < orderGoodsList.size(); i++) {
			DtsOrderGoods dog = orderGoodsList.get(i);
			Integer brandId = dog.getBrandId();
			boolean hasExsist = false;
			for (int k = 0; k < brandOrderGoodsList.size(); k++) {
				if (brandOrderGoodsList.get(k).getBrandId().intValue() == dog.getBrandId().intValue()) {
					brandOrderGoodsList.get(k).getOrderGoodsList().add(dog);
					hasExsist = true;
					break;
				}
			}
			if (!hasExsist) { // 还尚未加入，则需要查询品牌入驻商铺
				BrandOrderGoods bog = new BrandOrderGoods();
				bog.setBrandId(brandId);
				List<DtsOrderGoods> childOrderGoodslist = new ArrayList<DtsOrderGoods>();
				childOrderGoodslist.add(dog);
				bog.setOrderGoodsList(childOrderGoodslist);
				brandOrderGoodsList.add(bog);
			}
		}

		return brandOrderGoodsList;
	}

	/**
	 * 订单申请退款
	 * <p>
	 * 1. 检测当前订单是否能够退款； 2. 设置订单申请退款状态。
	 *
	 * @param userId
	 *            用户ID
	 * @param body
	 *            订单信息，{ orderId：xxx }
	 * @return 订单退款操作结果
	 */
	public Object refund(Integer userId, String body) {
		if (userId == null) {
			logger.error("订单申请退款失败：用户未登录!");
			return ResponseUtil.unlogin();
		}
		Integer orderId = JacksonUtil.parseInteger(body, "orderId");
		if (orderId == null) {
			return ResponseUtil.badArgument();
		}

		DtsOrder order = orderService.findById(orderId);
		if (order == null) {
			return ResponseUtil.badArgument();
		}
		if (!order.getUserId().equals(userId)) {
			return ResponseUtil.badArgumentValue();
		}

		OrderHandleOption handleOption = OrderUtil.build(order);
		if (!handleOption.isRefund()) {
			logger.error("订单申请退款失败：{}", ORDER_INVALID_OPERATION.desc());
			return WxResponseUtil.fail(ORDER_INVALID_OPERATION);
		}

		// 设置订单申请退款状态
		order.setOrderStatus(OrderUtil.STATUS_REFUND);
		if (orderService.updateWithOptimisticLocker(order) == 0) {
			logger.error("订单申请退款失败：{}", "更新订单信息失败");
			return ResponseUtil.updatedDateExpired();
		}

		// TODO 发送邮件和短信通知，这里采用异步发送
		// 有用户申请退款，邮件通知运营人员
		// notifyService.notifyMail("退款申请", order.toString());
		notifyService.notifySslMail("退款申请", OrderUtil.orderHtmlText(order, order.getId().intValue() + "", null));

		// 请依据自己的模版消息配置更改参数
		/*
		 * String[] parms = new String[]{ order.getOrderSn(),
		 * order.getOrderPrice().toString(),
		 * DateTimeUtil.getDateTimeDisplayString(order.getAddTime()),
		 * order.getConsignee(), order.getMobile(), order.getAddress() };
		 * notifyService.notifyWxTemplate("oZQrt0N4e5Ps_R-NhtMzsei93-58",
		 * NotifyType.APPLYREFUND, parms, "pages/index/index?orderId=" + order.getId());
		 */
		logger.info("【请求结束】订单申请退款成功！");
		return ResponseUtil.ok();
	}

	/**
	 * 确认收货
	 * <p>
	 * 1. 检测当前订单是否能够确认收货； 2. 设置订单确认收货状态。
	 *
	 * @param userId
	 *            用户ID
	 * @param body
	 *            订单信息，{ orderId：xxx }
	 * @return 订单操作结果
	 */
	public Object confirm(Integer userId, String body) {
		if (userId == null) {
			logger.error("订单确认收货失败：用户未登录!");
			return ResponseUtil.unlogin();
		}
		Integer orderId = JacksonUtil.parseInteger(body, "orderId");
		if (orderId == null) {
			return ResponseUtil.badArgument();
		}

		DtsOrder order = orderService.findById(orderId);
		if (order == null) {
			return ResponseUtil.badArgument();
		}
		if (!order.getUserId().equals(userId)) {
			return ResponseUtil.badArgumentValue();
		}

		OrderHandleOption handleOption = OrderUtil.build(order);
		if (!handleOption.isConfirm()) {
			logger.error("订单确认收货失败：{}", ORDER_CONFIRM_OPERATION.desc());
			return WxResponseUtil.fail(ORDER_CONFIRM_OPERATION);
		}

		Short comments = orderGoodsService.getComments(orderId);
		order.setComments(comments);

		order.setOrderStatus(OrderUtil.STATUS_CONFIRM);
		order.setConfirmTime(LocalDateTime.now());
		if (orderService.updateWithOptimisticLocker(order) == 0) {
			logger.error("订单确认收货失败：{}", "更新订单信息失败");
			return ResponseUtil.updatedDateExpired();
		}

		logger.info("【请求结束】订单确认收货成功！");
		return ResponseUtil.ok();
	}

	/**
	 * 删除订单
	 * <p>
	 * 1. 检测当前订单是否可以删除； 2. 删除订单。
	 *
	 * @param userId
	 *            用户ID
	 * @param body
	 *            订单信息，{ orderId：xxx }
	 * @return 订单操作结果
	 */
	public Object delete(Integer userId, String body) {
		if (userId == null) {
			logger.error("删除订单失败：用户未登录!");
			return ResponseUtil.unlogin();
		}
		Integer orderId = JacksonUtil.parseInteger(body, "orderId");
		if (orderId == null) {
			return ResponseUtil.badArgument();
		}

		DtsOrder order = orderService.findById(orderId);
		if (order == null) {
			return ResponseUtil.badArgument();
		}
		if (!order.getUserId().equals(userId)) {
			return ResponseUtil.badArgumentValue();
		}

		OrderHandleOption handleOption = OrderUtil.build(order);
		if (!handleOption.isDelete()) {
			logger.error("删除订单失败：{}", ORDER_DEL_OPERATION.desc());
			return WxResponseUtil.fail(ORDER_DEL_OPERATION);
		}

		// 订单order_status没有字段用于标识删除
		// 而是存在专门的delete字段表示是否删除
		orderService.deleteById(orderId);

		logger.info("【请求结束】删除订单成功！");
		return ResponseUtil.ok();
	}

	/**
	 * 待评价订单商品信息
	 *
	 * @param userId
	 *            用户ID
	 * @param orderId
	 *            订单ID
	 * @param goodsId
	 *            商品ID
	 * @return 待评价订单商品信息
	 */
	public Object goods(Integer userId, Integer orderId, Integer goodsId) {
		if (userId == null) {
			logger.error("获取待评价订单商品订单失败：用户未登录!");
			return ResponseUtil.unlogin();
		}

		List<DtsOrderGoods> orderGoodsList = orderGoodsService.findByOidAndGid(orderId, goodsId);
		int size = orderGoodsList.size();

		Assert.state(size < 2, "存在多个符合条件的订单商品");

		if (size == 0) {
			return ResponseUtil.badArgumentValue();
		}

		DtsOrderGoods orderGoods = orderGoodsList.get(0);

		logger.info("【请求结束】获取待评价订单商品订单成功！");
		return ResponseUtil.ok(orderGoods);
	}

	/**
	 * 评价订单商品
	 * <p>
	 * 确认商品收货或者系统自动确认商品收货后7天内可以评价，过期不能评价。
	 *
	 * @param userId
	 *            用户ID
	 * @param body
	 *            订单信息，{ orderId：xxx }
	 * @return 订单操作结果
	 */
	public Object comment(Integer userId, String body) {
		if (userId == null) {
			logger.error("评价订单商品失败：用户未登录!");
			return ResponseUtil.unlogin();
		}

		Integer orderGoodsId = JacksonUtil.parseInteger(body, "orderGoodsId");
		if (orderGoodsId == null) {
			return ResponseUtil.badArgument();
		}
		DtsOrderGoods orderGoods = orderGoodsService.findById(orderGoodsId);
		if (orderGoods == null) {
			return ResponseUtil.badArgumentValue();
		}
		Integer orderId = orderGoods.getOrderId();
		DtsOrder order = orderService.findById(orderId);
		if (order == null) {
			return ResponseUtil.badArgumentValue();
		}

		if (!OrderUtil.isConfirmStatus(order) && !OrderUtil.isAutoConfirmStatus(order)) {
			logger.error("评价订单商品失败：{}", ORDER_NOT_COMMENT.desc());
			return WxResponseUtil.fail(ORDER_NOT_COMMENT);
		}
		if (!order.getUserId().equals(userId)) {
			logger.error("评价订单商品失败：{}", ORDER_INVALID.desc());
			return WxResponseUtil.fail(ORDER_INVALID);
		}
		Integer commentId = orderGoods.getComment();
		if (commentId == -1) {
			logger.error("评价订单商品失败：{}", ORDER_COMMENT_EXPIRED.desc());
			return WxResponseUtil.fail(ORDER_COMMENT_EXPIRED);
		}
		if (commentId != 0) {
			logger.error("评价订单商品失败：{}", ORDER_COMMENTED.desc());
			return WxResponseUtil.fail(ORDER_COMMENTED);
		}

		String content = JacksonUtil.parseString(body, "content");
		Integer star = JacksonUtil.parseInteger(body, "star");
		if (star == null || star < 0 || star > 5) {
			return ResponseUtil.badArgumentValue();
		}
		Boolean hasPicture = JacksonUtil.parseBoolean(body, "hasPicture");
		List<String> picUrls = JacksonUtil.parseStringList(body, "picUrls");
		if (hasPicture == null || !hasPicture) {
			picUrls = new ArrayList<>(0);
		}

		// 1. 创建评价
		DtsComment comment = new DtsComment();
		comment.setUserId(userId);
		comment.setType((byte) 0);
		comment.setValueId(orderGoods.getGoodsId());
		comment.setStar(star.shortValue());
		comment.setContent(content);
		comment.setHasPicture(hasPicture);
		comment.setPicUrls(picUrls.toArray(new String[] {}));
		commentService.save(comment);

		// 2. 更新订单商品的评价列表
		orderGoods.setComment(comment.getId());
		orderGoodsService.updateById(orderGoods);

		// 3. 更新订单中未评价的订单商品可评价数量
		Short commentCount = order.getComments();
		if (commentCount > 0) {
			commentCount--;
		}
		order.setComments(commentCount);
		orderService.updateWithOptimisticLocker(order);

		logger.info("【请求结束】评价订单商品成功！");
		return ResponseUtil.ok();
	}

	/**
	 * 推广订单列表
	 *
	 * @param userId
	 *            用户代理用户ID
	 * @param showType
	 *            订单信息： 0，全部订单； 1，有效订单； 2，失效订单； 3，结算订单； 4，待结算订单。
	 * @param page
	 *            分页页数
	 * @param size
	 *            分页大小
	 * @return 推广订单列表
	 */
	public Object settleOrderList(Integer userId, Integer showType, Integer page, Integer size) {
		if (userId == null) {
			logger.error("获取推广订单列表失败：用户未登录!");
			return ResponseUtil.unlogin();
		}
		List<Short> orderStatus = OrderUtil.settleOrderStatus(showType);
		List<Short> settlementStatus = OrderUtil.settlementStatus(showType);

		List<DtsOrder> orderList = accountService.querySettlementOrder(userId, orderStatus, settlementStatus, page,
				size);
		long count = PageInfo.of(orderList).getTotal();
		int totalPages = (int) Math.ceil((double) count / size);

		List<Map<String, Object>> orderVoList = new ArrayList<>(orderList.size());
		for (DtsOrder order : orderList) {
			Map<String, Object> orderVo = new HashMap<>();
			orderVo.put("id", order.getId());
			orderVo.put("orderSn", order.getOrderSn());
			orderVo.put("actualPrice", order.getActualPrice());
			orderVo.put("orderStatusText", OrderUtil.orderStatusText(order));
			orderVo.put("handleOption", OrderUtil.build(order));

			List<DtsOrderGoods> orderGoodsList = orderGoodsService.queryByOid(order.getId());
			List<Map<String, Object>> orderGoodsVoList = new ArrayList<>(orderGoodsList.size());
			for (DtsOrderGoods orderGoods : orderGoodsList) {
				Map<String, Object> orderGoodsVo = new HashMap<>();
				orderGoodsVo.put("id", orderGoods.getId());
				orderGoodsVo.put("goodsName", orderGoods.getGoodsName());
				orderGoodsVo.put("number", orderGoods.getNumber());
				orderGoodsVo.put("picUrl", orderGoods.getPicUrl());
				orderGoodsVoList.add(orderGoodsVo);
			}
			orderVo.put("goodsList", orderGoodsVoList);

			orderVoList.add(orderVo);
		}

		Map<String, Object> result = new HashMap<>();
		result.put("count", count);
		result.put("data", orderVoList);
		result.put("totalPages", totalPages);

		logger.info("【请求结束】获取推广订单列表成功！,推广订单数：{}", orderVoList.size());
		return ResponseUtil.ok(result);
	}

	/**
	 * 分页查询取款结算记录
	 * 
	 * @param userId
	 * @param page
	 * @param size
	 * @return
	 */
	public Object extractList(Integer userId, Integer page, Integer size) {
		if (userId == null) {
			logger.error("分页查询取款结算记录失败：用户未登录!");
			return ResponseUtil.unlogin();
		}

		List<DtsAccountTrace> accountTraceList = accountService.queryAccountTraceList(userId, page, size);
		long count = PageInfo.of(accountTraceList).getTotal();
		int totalPages = (int) Math.ceil((double) count / size);

		Map<String, Object> result = new HashMap<>();
		result.put("count", count);
		result.put("accountTraceList", accountTraceList);
		result.put("totalPages", totalPages);

		logger.info("【请求结束】获取佣金提现列表成功！,提现总数：{}", count);
		return ResponseUtil.ok(result);
	}

//	/**
//	 * 订单物流跟踪
//	 *
//	 * @param userId
//	 * @param orderId
//	 * @return
//	 */
//	public Object expressTrace(Integer userId, @NotNull Integer orderId) {
//		if (userId == null) {
//			logger.error("订单物流跟踪失败：用户未登录!");
//			return ResponseUtil.unlogin();
//		}
//
//		// 订单信息
//		DtsOrder order = orderService.findById(orderId);
//		if (null == order) {
//			logger.error("订单物流跟踪失败：{}", ORDER_UNKNOWN.desc());
//			return WxResponseUtil.fail(ORDER_UNKNOWN);
//		}
//		if (!order.getUserId().equals(userId)) {
//			logger.error("订单物流跟踪失败：{}", ORDER_INVALID.desc());
//			return WxResponseUtil.fail(ORDER_INVALID);
//		}
//
//		Map<String, Object> result = new HashMap<>();
//		DateTimeFormatter dateSdf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
//		DateTimeFormatter timeSdf = DateTimeFormatter.ofPattern("HH:mm");
//
//		result.put("shipDate", dateSdf.format(order.getShipTime()));
//		result.put("shipTime", timeSdf.format(order.getShipTime()));
//		result.put("shipperCode", order.getShipSn());
//		result.put("address", order.getAddress());
//
//		// "YTO", "800669400640887922"
//		if (order.getOrderStatus().equals(OrderUtil.STATUS_SHIP)) {
//			ExpressInfo ei = expressService.getExpressInfo(order.getShipChannel(), order.getShipSn());
//			if (ei != null) {
//				result.put("state", ei.getState());
//				result.put("shipperName", ei.getShipperName());
//				List<Traces> eiTrace = ei.getTraces();
//				List<Map<String, Object>> traces = new ArrayList<Map<String, Object>>();
//				for (Traces trace : eiTrace) {
//					Map<String, Object> traceMap = new HashMap<String, Object>();
//					traceMap.put("date", trace.getAcceptTime().substring(0, 10));
//					traceMap.put("time", trace.getAcceptTime().substring(11, 16));
//					traceMap.put("acceptTime", trace.getAcceptTime());
//					traceMap.put("acceptStation", trace.getAcceptStation());
//					traces.add(traceMap);
//				}
//				result.put("traces", traces);
//			}
//		}
//
//		logger.info("【请求结束】订单物流跟踪成功！");
//		return ResponseUtil.ok(result);
//
//	}
}
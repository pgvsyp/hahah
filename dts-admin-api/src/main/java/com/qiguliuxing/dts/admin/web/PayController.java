package com.qiguliuxing.dts.admin.web;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.qiguliuxing.dts.admin.config.AlipayConfig;
import com.qiguliuxing.dts.admin.util.MailUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin/pay")
@Validated
public class PayController{
    /**
     * 方法实现说明 支付宝支付
     * @author      金刚
     * @date        2020/10/14 17:12
     */
    @ApiOperation(value = "支付宝扫码支付",tags = "支付入口")
    @GetMapping(value = "aliPay")
    public String goAlipay(HttpServletResponse httpResponse) throws Exception {
        //订单保存
        //省略业务代码
        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = MailUtil.getOrderIdByTime();
        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.app_id, AlipayConfig.merchant_private_key, "json", AlipayConfig.charset, AlipayConfig.alipay_public_key, AlipayConfig.sign_type);
        //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        //这里设置支付后跳转的地址
        alipayRequest.setReturnUrl(AlipayConfig.return_url);
        alipayRequest.setNotifyUrl(AlipayConfig.notify_url);
        alipayRequest.setBizContent(JSON.toJSONString(alipayClient));
        //付款金额，必填
        String total_amount = String.valueOf("0.01");
        //订单名称，必填
        String subject ="充值";
        //商品描述，可空
        String body = "金币充值";
        // 该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。 该参数数值不接受小数点， 如 1.5h，可转换为 90m。
        String timeout_express = "5m";
        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"timeout_express\":\""+ timeout_express +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        //请求
        //  String result = alipayClient.pageExecute(alipayRequest).getBody();
        AlipayTradePagePayResponse response = alipayClient.pageExecute(alipayRequest);
        String body1 = response.getBody();  // 注意 这个是调起支付扫码的，但是不是获取二维码的，他这个返回来的是个form标签
        //  我们只需要将返回的这个标签进行跳转就行了，二维码是跳转之后就出来的，不用考虑获取二维码
        httpResponse.setContentType("text/html;charset="+ AlipayConfig.charset);
        httpResponse.getWriter().write(body1);
        httpResponse.getWriter().flush();
        httpResponse.getWriter().close();
        Map<String, Object> data = new HashMap<>();
        if (response.isSuccess()){
            System.out.println("调起成功");
            data.put("msg", "调起成功");
            return null;
        }else {
            System.out.println("调起失败");
            data.put("msg", "调起失败");
            return null;
        }
    }

}

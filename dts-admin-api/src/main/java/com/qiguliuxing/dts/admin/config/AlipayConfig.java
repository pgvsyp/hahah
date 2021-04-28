package com.qiguliuxing.dts.admin.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class AlipayConfig {
    //↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public static String app_id = "2021002140609221";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDZZEltO2CKwQl0hZngOlG8J4omGctHDm3A54QTyCxvKspW6zoUX1HvZ+5zzx/7tiODf+XPr/8KUGT4zS3n/j/m0imCWC+2jnCSSVgMh/xfD5+qvIPQkx8mP9QbrN5BiPZrSw3hNFzgRiz5GLfEmVtXu3FzXYnGXZX35flDLNoP9yIDCHH42JdQcaX4MjY7SwxYLHRpN84KhPUfSEMyjtx8Bwng7IJC66UHkz2eXm4daDYmaLcPA2opK333XC+4G+WuWkktv8gd4thUHtu0rHFy+FzwnDgfcfUpoJv3E0n0QFU2GpSx7nqbQWdRDuV780vIsdUfw043JpU8ZUDIEsm3AgMBAAECggEAe3jJd6Ac8lI3s5XaUw/ssohmn8NQwLgCQGl+i9nK2w0bxYhvAXkuSWLnfaFr0gSeYSb4eSDGI66L2wB2jWjMFKbTarP5zZ9TctLzjpKCFeAC2O+fkoytSYVBTMK/rctD4qbLaFjcU4moK4cNgT11MvxEsLi08xTeg+F71NqZtCymGF5S5DRUhNdY77gqMcUCWjWnJccJaCibVnpshFwFy19yDs9Pq1cD87d+OJsCSOO5PpTPnvbd2PVNXnFx/wlaOSMhmEcD4OgTd/I6zBWp+mBunXflq2W/0Nwm4Vp8GmtWGGwOt0/0xdkTWulFLSLDFcstHkOwhLbCa69az/d2cQKBgQD57mc0xlTW+fvU1Iraukgb4PCxlDsdy/QYIzyIsj/IuKVrHSAXimMdFL+TQlLd5XLW++VY8r5AMCj7LaGCLOBbckk7DoiehibhnJ0gWt2NiCISCoO/hgukEDENGjcF3h/yXScu82G0iTszWtKkE/D9negtLq41Emjj4SsssJvRbwKBgQDeq51p1BXiuxQYtm6Vo7+ITFForBsrCk3MMG7higlr0A4KzuAlIVBQB5nAMB4AqnfgrjIWm2BNweLIatCGTDgEVXZx1CMH8txqemxUt1hurwjgXFKXdNYPeGnw4kG4H60hRcBTDIBaOCPkQIkFbNLbLc4c/IAV6ktanVrzCU1YOQKBgGQSii5+bz/4rlYEmGiGCBNpmNZxtM30eRMlILrXLGTxUgK2kb/7QD5HrYGIH+gLVFUyXR2lbHoFDpOsLKwTze2vsBS+eAOJUY4qiFuJJobiCEpS/Xe1c3nIpetKINfvnbkMhYDuIKQKArNIXWknNupFzM4yj7xeV7NO2Fu3h1xvAoGBAIn0Mxts3X5SP1osNIkbIT/1YH6EGnTo9lgqLZgoKDwWc4y4rN/0KsjaXKRYa04oTwrxgXDGJL0oATQAEV8S+6WbuT0+m9GB4pNJc7JswSoZ4Vr3d+gD59c/2Ar4cpT7cwzpJQWO+jprC7mPThUBXgWS9F67BTQOq07hrIZkXfnpAoGAdRaWoY167dCI5eAjjXpgjmb/RrQHVGsLo9k2FvEKyrI60YBfZYBn/R8mhm2Pf/kKSnZ36o/RSKpf1J/jsGG7I7q3brHHVnwuPSopLNBPoMaPNyQR5uAFH9knrGPA1KVn7sTrJfNQni+NJJGbGkW4HCLOQ5I3zP1KfVZQ1WXVrJY=";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmQWf984EDOmp/w4HUC9kKctSLhJhxs7O/0cltAuxk0iWFDOQEvf5hQwdt+FXwmazRvv7l9d5R+ySD7cDkAQ06Ty/wbVB7XP0VJBFZpIg+/fayv5RoKYvP+3Y7W7RyQILsE6Hf534i9NNAPormOLelW3LfzMVsgEmoYBbSQNU7WrWxsGvTYUZtFZzqVw8CtRexbKzAxx0FtddLvhkiCBu15cH+k453EHU6/3IPpBiHeR+LU/4jGhJSDzc7FZnUVwvg0y/0qG7Cp/og2kyP5exd8Wst7vf4L1wgFXowPXdTtFLhundw/2fpSrT+9CwMhHCCcciej/7C6Fa/N+4YkKxUwIDAQAB";
    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //这里地址为，支付成功之后回调的地址，异步地址
    public static String notify_url = "http://localhost:8082/";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //这里地址为，支付成功之后跳转的地址，同步
    public static String return_url = "http://localhost:8082/buy";

    // 签名方式
    public static String sign_type = "RSA2";

    // 字符编码格式
    public static String charset = "utf-8";

    // 支付宝网关
    public static String gatewayUrl = "https://openapi.alipay.com/gateway.do";
}

package com.qiguliuxing.dts.admin.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class AlipayConfig {
    //↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public static String app_id = "2021001110632048";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCXOr5a8HSN8alubQNxcp423PQeF6ES4Y3qRAv1hAjJaHPRalRa5o73kB0qUlgGbfqYr5hUl2ovLicCcA/IFOpRHjQXNC28qAhWn2wNQGJJu9qr6tI6KBvE6vKQQf10LrnTtuLpoCNgIqiyO38S9RTyCSFfM8GrCFmhcVHboVB28vTgFqkpm/kt5eFmRNxjjM2jDBanbmaqnluDWsC1x+DNvQpk7mtQLrdwnOmGX9nqGh5DcgYeSJ8g3uw0Ih966o2JciXixj58qdDigikZoR7IBEbUhsZ26GkhHOcgMLPyppTtGulfGo8EaWGdjBwVwrUAalS4WO/7mdqu7U7YMPQPAgMBAAECggEACPxOjyy2YDC5tnv3tmfvCqd6Qcd5pJuOSRsd8sqTkxQHvaups3T7ja4cbYVTAZ7LQwM789rDTmZZnwV1ipfjjEpVIgPRq+H4DN2QGAF5VZQ52/kp4Ja9f6fOHdAt9RdJp1C4jtUbBP1KNLzgQgoZ4n+0Yem7WiQ23ybSjFA7bbXORYXUlWHyQYhRLHL1LoP7ShJjIfoEncYPEpaPzQ7T+Dm0yE5R2zYw/UxeJaFuExINGiO2BmQ3w/a4SeCRF2WhBMqJmlAD5m68FHgA/x7Z9YjwkfSfape3gvWaPOniNKaCvCEctttxVwe4+s308PKy0kUaSafKXCvgcKeB6lKqMQKBgQDij/LD7Yh5jpOEHcNDph/CogXsoZahCJHVfjPHm2tcIWyGJ1g8MeTZZZ507gfIjtED35hF6D0j+COjf6AUNVPdz4kOkjUll+QS9WZ49X07zasDHIMrxKicuYVYNgG2TJA8+YJ0Og86hF6euQgEcv5hRk+ydsW/pwEX1E66tud9IwKBgQCq4Qd840oWmcMAAq3uXvGXjqdqjsYdNxuWrRTvIpqC5QFtn91OcUCEzHMeV2snBMK2+bB0NNh0cQQcF7jw2IpuWdMbd2E5E+7+iZGqJoRTPXpkRJ+3rKcBviq988YfzLJTC2d9biOkR7JnNqHTEq8KsIK7lTGgW4zBgRUCMY2KJQKBgQCP6Xn+ZHqlJCCl8jXfWb2g50Q2HCpPHd9sql5/s0SN4BlMWd+dVOAWC4uMoMpG5hj0ienyteNoXq1cpKvtSjZ8leBsZgjmWUYhmMm/mcgz3Z98OXdErQWhj+KYtq19u8J/SMpJw7t6oyxrTANAuD7HUV88cF+KLq3uEgj0Z0xB2wKBgChpXYb4jc34gThgadfk1kfccdVufIXuQXUJ69h+G4J3PDY7x2/Esth1LYhwbiPlKVNyi3t//+zRyYIHTjKPU3WzgoyNWA3SnAPQbwWJZGP3Nxm9bPRYPYrY+WzA2QsuMsIXAVuVj5JLbnKgZfbwBTuG8PQB3aGp5QjiSSCirXGNAoGAXqtKKNw81QAvtxLtlQR8rEmyPzDmQhkz3EQQuOKzryyeJssDjeAwsZHiyIdGRDyBq8QGLYAHos2mcYKvmMtLP16jR8T2P0l/TZqbzHpmxesYoxsMp8DcuTPibJtjRrxd/LbDQd6ehCs6hM5sxGxv7RKAYEI2Bv6qZkJOS3W1RX8=";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlzq+WvB0jfGpbm0DcXKeNtz0HhehEuGN6kQL9YQIyWhz0WpUWuaO95AdKlJYBm36mK+YVJdqLy4nAnAPyBTqUR40FzQtvKgIVp9sDUBiSbvaq+rSOigbxOrykEH9dC6507bi6aAjYCKosjt/EvUU8gkhXzPBqwhZoXFR26FQdvL04BapKZv5LeXhZkTcY4zNowwWp25mqp5bg1rAtcfgzb0KZO5rUC63cJzphl/Z6hoeQ3IGHkifIN7sNCIfeuqNiXIl4sY+fKnQ4oIpGaEeyARG1IbGduhpIRznIDCz8qaU7RrpXxqPBGlhnYwcFcK1AGpUuFjv+5naru1O2DD0DwIDAQAB";

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

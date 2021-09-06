package com.fuint.application.service.weixin;

import com.fuint.application.BaseService;
import com.fuint.application.dao.entities.MtOrder;
import com.fuint.application.dao.entities.MtUser;
import com.fuint.application.dto.OrderDto;
import com.fuint.application.dto.UserOrderDto;
import com.fuint.application.enums.OrderStatusEnum;
import com.fuint.application.enums.OrderTypeEnum;
import  com.fuint.application.http.HttpRESTDataClient;
import com.fuint.application.service.order.OrderService;
import com.fuint.application.util.TimeUtils;
import com.fuint.exception.BusinessCheckException;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import com.fuint.application.config.WXPayConfigImpl;
import com.fuint.application.ResponseObject;
import org.apache.commons.lang.StringUtils;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import weixin.popular.util.JsonUtil;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.AlgorithmParameters;
import java.security.Security;
import com.fuint.application.service.usercoupon.UserCouponService;

@Service
public class WeixinServiceimpl extends BaseService implements WeixinService {
    private static final Logger logger = LoggerFactory.getLogger(WeixinServiceimpl.class);

    @Resource
    private WXPayConfigImpl wxPayConfigImpl;

    @Autowired
    private UserCouponService userCouponService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private Environment env;

    @Override
    public ResponseObject createPrepayOrder(MtUser userInfo, MtOrder orderInfo, Integer payAmount, Integer giveAmount, String ip) throws BusinessCheckException {
        logger.debug("WeixinService createPrepayOrder inParams userInfo={} payAmount={} giveAmount={} goodsInfo={}", userInfo, payAmount, giveAmount, orderInfo);

        String goodsInfo = orderInfo.getOrderSn();
        if (orderInfo.getType().equals(OrderTypeEnum.PRESTORE.getKey())) {
            goodsInfo = OrderTypeEnum.PRESTORE.getValue();
        }

        // 1. 调用微信接口生成预支付订单
        Map<String, String> reqData = new HashMap<>();
        reqData.put("body", goodsInfo);
        reqData.put("out_trade_no", orderInfo.getOrderSn());
        reqData.put("device_info", "");
        reqData.put("fee_type", "CNY");
        reqData.put("total_fee", payAmount.toString());
        reqData.put("spbill_create_ip", ip);
        reqData.put("notify_url", wxPayConfigImpl.getCallbackUrl());
        reqData.put("trade_type", "JSAPI");
        reqData.put("openid", userInfo.getOpenId());

        Map<String, String> respData = this.unifiedOrder(reqData);
        if (respData == null) {
            logger.error("微信支付接口调用异常......");
            return getFailureResult(3000, "微信支付接口调用异常");
        }

        // 2.记录支付接口请求/响应参数
        Map<String, String> outParmas = new HashMap<>();

        //3.更新预支付订单号
        if (respData.get("return_code").equals("SUCCESS")) {
            String prepayId = respData.get("prepay_id");

            //组织返回参数
            String appId = respData.get("appid");
            String nonceStr = respData.get("nonce_str");

            outParmas.put("appId", appId);
            outParmas.put("timeStamp", String.valueOf(TimeUtils.timeStamp()));
            outParmas.put("nonceStr", nonceStr);
            outParmas.put("package", "prepay_id=" + prepayId);
            outParmas.put("signType", "MD5");
            try {
                String sign = WXPayUtil.generateSignature(outParmas, wxPayConfigImpl.getKey());
                outParmas.put("paySign", sign);
            } catch (Exception e) {
                //签名失败
                logger.error(e.getMessage(), e);
                return getFailureResult(3000, "微信支付签名失败");
            }
        } else {
            logger.error("微信支付接口返回状态失败......");
            return getFailureResult(3000, "微信支付接口返回状态失败");
        }

        // 更新支付金额
        BigDecimal payAmount1 = new BigDecimal(payAmount).divide(new BigDecimal("100"));
        OrderDto reqDto = new OrderDto();
        reqDto.setId(orderInfo.getId());
        reqDto.setPayAmount(payAmount1);
        orderService.updateOrder(reqDto);

        ResponseObject responseObject = getSuccessResult(outParmas);
        logger.debug("WXService createPrepayOrder outParams {} payAmount{}", responseObject.toString(), payAmount1);

        return responseObject;
    }

    @Override
    public boolean paymentCallback(UserOrderDto orderInfo) throws BusinessCheckException {
        // 预存卡订单
        if (orderInfo.getType().equals(OrderTypeEnum.PRESTORE.getKey())) {
            Map<String, Object> param = new HashMap<>();
            param.put("couponId", orderInfo.getCouponId());
            param.put("userId", orderInfo.getUserId());
            param.put("param", orderInfo.getParam());
            param.put("orderId", orderInfo.getId());
            userCouponService.preStore(param);
        }

        // 更新订单状态为已支付
        OrderDto reqDto = new OrderDto();
        reqDto.setId(orderInfo.getId());
        reqDto.setStatus(OrderStatusEnum.PAID.getKey());
        orderService.updateOrder(reqDto);

        return true;
    }

    public Map<String, String> processResXml(HttpServletRequest request) {
        InputStream inStream = null;
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        try {
            inStream = request.getInputStream();
            byte[] buffer = new byte[4096];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }

            String result = new String(outSteam.toByteArray(), "utf-8");
            logger.info("微信支付回调入参报文{}", result);

            Map<String, String> resultMap = WXPayUtil.xmlToMap(result);
            String returnCode = resultMap.get("return_code");
            if (StringUtils.isNotEmpty(returnCode) && returnCode.equals("SUCCESS")) {
                boolean flag = WXPayUtil.isSignatureValid(resultMap, wxPayConfigImpl.getKey());
                if (!flag) {
                    logger.error("微信支付回调接口验签失败");
                    return null;
                }
                return resultMap;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            if (outSteam != null) {
                try {
                    outSteam.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return null;
    }

    public void processRespXml(HttpServletResponse response, boolean flag){
        Map<String,String> respData = new HashMap<String,String>();
        if (flag) {
            respData.put("return_code", "SUCCESS");
            respData.put("return_msg", "OK");
        }else{
            respData.put("return_code", "FAIL");
            respData.put("return_msg", "FAIL");
        }
        OutputStream outputStream = null;
        try {
            String respXml = WXPayUtil.mapToXml(respData);
            outputStream = response.getOutputStream();
            outputStream.write(respXml.getBytes("UTF-8"));
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(outputStream!=null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                    outputStream = null;
                }
            }
        }
    }

    @Override
    public JSONObject wxLogin(String code) {
        String wxAppId = env.getProperty("weixin.pay.appId");
        String wxAppSecret = env.getProperty("weixin.pay.appSecret");
        String wxAccessUrl = env.getProperty("weixin.access.url");

        String url = String.format(wxAccessUrl, wxAppId, wxAppSecret, code);
        try {
            String response = HttpRESTDataClient.requestGet(url);
            JSONObject json = (JSONObject) JSONObject.parse(response);
            if (!json.containsKey("errcode")) {
                return json;
            } else {
                logger.error("获取union id 出错：" + json.get("errmsg"));
            }
        } catch (Exception e) {
            logger.error("获取微信union id 异常：" + e.getMessage());
        }

        return null;
    }

    @Override
    public String getPhoneNumber(String encryptedData, String sessionKey, String iv) {
        // 被加密的数据
        byte[] dataByte = Base64.decode(encryptedData);
        // 加密秘钥
        byte[] keyByte = Base64.decode(sessionKey);
        // 偏移量
        byte[] ivByte = Base64.decode(iv);
        try {
            // 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
            int base = 16;
            if (keyByte.length % base != 0) {
                int groups = keyByte.length / base + (keyByte.length % base != 0 ? 1 : 0);
                byte[] temp = new byte[groups * base];
                Arrays.fill(temp, (byte) 0);
                System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
                keyByte = temp;
            }
            // 初始化
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
            AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
            parameters.init(new IvParameterSpec(ivByte));
            cipher.init(Cipher.DECRYPT_MODE, spec, parameters);// 初始化
            byte[] resultByte = cipher.doFinal(dataByte);
            if (null != resultByte && resultByte.length > 0) {
                String result = new String(resultByte, "UTF-8");
                JSONObject object = JSONObject.parseObject(result);
                return object.getString("phoneNumber");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private Map<String, String> unifiedOrder(Map<String, String> reqData) {
        try {
            logger.info("调用微信支付下单接口入参{}", JsonUtil.toJSONString(reqData));

            WXPay wxPay = new WXPay(wxPayConfigImpl);
            Map<String, String> respMap = wxPay.unifiedOrder(reqData);

            logger.info("调用微信支付下单接口返回{}", JsonUtil.toJSONString(respMap));
            return respMap;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }
}

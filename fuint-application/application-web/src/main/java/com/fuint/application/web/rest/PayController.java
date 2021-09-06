package com.fuint.application.web.rest;

import com.fuint.application.dto.UserOrderDto;
import com.fuint.application.enums.OrderStatusEnum;
import com.fuint.application.service.order.OrderService;
import com.fuint.application.service.weixin.WeixinService;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.ResponseObject;
import com.fuint.application.BaseController;
import com.fuint.application.dao.entities.MtUser;
import com.fuint.application.service.token.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 支付类controller
 * Created by FSQ
 * Contact wx fsq_better
 */
@RestController
@RequestMapping(value = "/rest/pay")
public class PayController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(SignController.class);

    @Autowired
    private TokenService tokenService;

    /**
     * 微信服务接口
     * */
    @Autowired
    private WeixinService weixinService;

    /**
     * 订单服务接口
     * */
    @Autowired
    private OrderService orderService;

    /**
     * 提交支付
     */
    @RequestMapping(value = "/doPay", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject doPay(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException{
        String userToken = request.getHeader("Access-Token");
        MtUser userInfo = tokenService.getUserInfoByToken(userToken);

        if (userInfo == null) {
            return getFailureResult(1001, "用户未登录");
        }

        ResponseObject responseObject = null;

        return getSuccessResult(responseObject.getData());
    }

    /**
     * 支付回调
     */
    @RequestMapping(value = "/weixinCallback", method = RequestMethod.POST)
    @CrossOrigin
    public void weixinCallback(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException{
        logger.info("微信支付结果回调....");

        Map<String, String> inParams = weixinService.processResXml(request);
        logger.info("微信返回Map:" + inParams);
        if (!CollectionUtils.isEmpty(inParams)) {
            String orderSn = inParams.get("out_trade_no");//商户订单号
            String orderId = inParams.get("transaction_id");//微信订单号
            String tranAmt = inParams.get("total_fee");//交易金额
            BigDecimal tranAmount = new BigDecimal(tranAmt).divide(new BigDecimal("100"));

            // 参数校验
            if (StringUtils.isNotEmpty(orderSn) && StringUtils.isNotEmpty(tranAmt) && StringUtils.isNotEmpty(orderId)) {
                UserOrderDto orderInfo = orderService.getOrderByOrderSn(orderSn);
                if (orderInfo != null) {
                    // 订单金额
                    BigDecimal payAmount = orderInfo.getPayAmount();
                    int compareFlag = tranAmount.compareTo(payAmount);
                    if (compareFlag == 0) {
                        if (orderInfo.getStatus().equals(OrderStatusEnum.CREATED.getKey())) {
                            boolean flag = weixinService.paymentCallback(orderInfo);
                            logger.info("回调结果："+flag);
                            if (flag) {
                                weixinService.processRespXml(response, true);
                            } else {
                                weixinService.processRespXml(response, false);
                            }
                        } else {
                            logger.error("订单{}已经支付，orderInfo.getStatus() = {}, CREATED.getKey() = {}", orderSn, orderInfo.getStatus(), OrderStatusEnum.CREATED.getKey());
                        }
                    } else {
                        logger.error("回调金额与支付金额不匹配 tranAmount = {}, payAmount = {}, compareFlag = {}", tranAmount, orderInfo.getPayAmount(), compareFlag);
                    }
                } else {
                    logger.error("支付订单{}对应的信息不存在", orderSn);
                }
            }
        }
    }
}

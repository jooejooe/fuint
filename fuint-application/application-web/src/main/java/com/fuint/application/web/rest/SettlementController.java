package com.fuint.application.web.rest;

import com.fuint.application.dao.entities.MtCoupon;
import com.fuint.application.dao.entities.MtOrder;
import com.fuint.application.dao.entities.MtUser;
import com.fuint.application.dao.entities.MtUserGrade;
import com.fuint.application.dto.OrderDto;
import com.fuint.application.enums.OrderTypeEnum;
import com.fuint.application.service.coupon.CouponService;
import com.fuint.application.service.order.OrderService;
import com.fuint.application.service.token.TokenService;
import com.fuint.application.service.weixin.WeixinService;
import com.fuint.application.service.usergrade.UserGradeService;
import com.fuint.application.util.CommonUtil;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.ResponseObject;
import com.fuint.application.BaseController;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 结算中心接口
 * Created by FSQ
 * Contact wx fsq_better
 */
@RestController
@RequestMapping(value = "/rest/settlement")
public class SettlementController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(SettlementController.class);

    /**
     * Token服务接口
     */
    @Autowired
    private TokenService tokenService;

    /**
     * 订单服务接口
     * */
    @Autowired
    private OrderService orderService;

    /**
     * 卡券服务接口
     */
    @Autowired
    private CouponService couponService;

    /**
     * 微信服务接口
     * */
    @Autowired
    private WeixinService weixinService;

    /**
     * 会员等级接口
     * */
    @Autowired UserGradeService userGradeService;

    /**
     * 结算提交
     */
    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject submit(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        MtUser userInfo = tokenService.getUserInfoByToken(token);
        if (null == userInfo) {
            return getFailureResult(1001);
        }
        param.put("userId", userInfo.getId());

        Integer targetId = param.get("targetId") == null ? 0 : Integer.parseInt(param.get("targetId").toString()); // 预存卡、升级等级必填
        String selectNum = param.get("selectNum") == null ? "" : param.get("selectNum").toString(); // 预存卡必填
        String remark = param.get("remark") == null ? "" : param.get("remark").toString();
        String type = param.get("type") == null ? "" : param.get("type").toString();
        String payAmount = param.get("payAmount") == null ? "" : param.get("payAmount").toString();
        Integer usePoint = param.get("usePoint") == null ? 0 : Integer.parseInt(param.get("usePoint").toString());

        // 生成订单数据
        OrderDto orderDto = new OrderDto();
        orderDto.setRemark(remark);
        orderDto.setUserId(userInfo.getId());
        orderDto.setType(type);
        orderDto.setUsePoint(usePoint);

        // 预存卡的订单
        if (orderDto.getType().equals(OrderTypeEnum.PRESTORE.getKey())) {
            orderDto.setCouponId(targetId);
            String orderParam = "";
            BigDecimal totalAmount = new BigDecimal(0);

            MtCoupon couponInfo = couponService.queryCouponById(targetId);
            String inRule = couponInfo.getInRule();
            String[] selectNumArr = selectNum.split(",");
            String[] ruleArr = inRule.split(",");
            for (int i = 0; i < ruleArr.length; i++) {
                String item = ruleArr[i] + "_" + (StringUtils.isNotEmpty(selectNumArr[i]) ? selectNumArr[i] : 0);
                String[] itemArr = item.split("_");
                // 预存金额
                BigDecimal price = new BigDecimal(itemArr[0]);
                // 预存数量
                BigDecimal num = new BigDecimal(selectNumArr[i]);
                BigDecimal amount = price.multiply(num);
                totalAmount = totalAmount.add(amount);
                orderParam = StringUtils.isEmpty(orderParam) ?  item : orderParam + ","+item;
            }

            orderDto.setParam(orderParam);
            orderDto.setAmount(totalAmount);

            payAmount = totalAmount.toString();
        }

        // 付款订单
        if (orderDto.getType().equals(OrderTypeEnum.PAYMENT.getKey())) {
            MtUserGrade userGrade = userGradeService.queryUserGradeById(Integer.parseInt(userInfo.getGradeId()));
            // 是否有会员折扣
            if (userGrade.getDiscount() > 0) {
                BigDecimal percent = new BigDecimal(userGrade.getDiscount()).divide(new BigDecimal("10"));
                BigDecimal payAmountDiscount = new BigDecimal(payAmount).multiply(percent);
                orderDto.setAmount(payAmountDiscount);
                orderDto.setDiscount(new BigDecimal(payAmount).subtract(payAmountDiscount));
            } else {
                orderDto.setAmount(new BigDecimal(payAmount));
                orderDto.setDiscount(new BigDecimal("0"));
            }
            orderDto.setPointAmount(new BigDecimal(usePoint));
        }

        // 升级订单
        if (orderDto.getType().equals(OrderTypeEnum.MEMBER.getKey())) {
            orderDto.setParam(targetId+"");
            MtUserGrade userGrade = userGradeService.queryUserGradeById(targetId);
            orderDto.setRemark("付费升级" + userGrade.getName());
            orderDto.setAmount(new BigDecimal(userGrade.getCatchValue().toString()));
            payAmount = userGrade.getCatchValue().toString();
        }

        // 生成订单
        MtOrder orderInfo = orderService.createOrder(orderDto);
        param.put("orderId", orderInfo.getId());

        // 生成支付订单
        String ip = CommonUtil.getIPFromHttpRequest(request);
        BigDecimal realPayAmount = new BigDecimal(payAmount);
        BigDecimal pay = realPayAmount.multiply(new BigDecimal("100"));
        ResponseObject paymentInfo = weixinService.createPrepayOrder(userInfo, orderInfo, (pay.intValue()), 0, ip);

        Map<String, Object> outParams = new HashMap();

        outParams.put("isCreated", true);
        outParams.put("payType", "wechat");
        outParams.put("orderInfo", orderInfo);
        outParams.put("payment", paymentInfo.getData());

        ResponseObject responseObject = getSuccessResult(outParams);

        return getSuccessResult(responseObject.getData());
    }
}

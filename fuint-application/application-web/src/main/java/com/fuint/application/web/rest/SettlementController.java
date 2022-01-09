package com.fuint.application.web.rest;

import com.fuint.application.dao.entities.*;
import com.fuint.application.dto.OrderDto;
import com.fuint.application.enums.OrderTypeEnum;
import com.fuint.application.enums.SettingTypeEnum;
import com.fuint.application.service.coupon.CouponService;
import com.fuint.application.service.order.OrderService;
import com.fuint.application.service.member.MemberService;
import com.fuint.application.service.setting.SettingService;
import com.fuint.application.service.token.TokenService;
import com.fuint.application.service.weixin.WeixinService;
import com.fuint.application.service.usergrade.UserGradeService;
import com.fuint.application.util.CommonUtil;
import com.fuint.base.shiro.util.ShiroUserHelper;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.ResponseObject;
import com.fuint.application.BaseController;
import com.fuint.base.shiro.ShiroUser;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
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
     * 会员服务接口
     * */
    @Autowired
    private MemberService memberService;

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
     * 配置服务接口
     * */
    @Autowired
    private SettingService settingService;

    /**
     * 结算提交
     */
    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject submit(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        MtUser userInfo = tokenService.getUserInfoByToken(token);

        String operator = null;
        ShiroUser ShiroUser = ShiroUserHelper.getCurrentShiroUser();
        if (ShiroUser != null) {
            operator = ShiroUserHelper.getCurrentShiroUser().getAcctName();
        }

        if ((null == userInfo || StringUtils.isEmpty(token))) {
            String mobile = param.get("mobile") == null ? "" : param.get("mobile").toString();
            if (StringUtils.isNotEmpty(operator) && StringUtils.isNotEmpty(mobile)) {
                userInfo = memberService.queryMemberByMobile(mobile);
                // 自动注册会员
                if (null == userInfo) {
                    userInfo = memberService.addMemberByMobile(mobile);
                }
                if (null == userInfo) {
                    return getFailureResult(2001);
                }
            } else {
                return getFailureResult(1001);
            }
        }

        param.put("userId", userInfo.getId());

        Integer targetId = param.get("targetId") == null ? 0 : Integer.parseInt(param.get("targetId").toString()); // 预存卡、升级等级必填
        String selectNum = param.get("selectNum") == null ? "" : param.get("selectNum").toString(); // 预存卡必填
        String remark = param.get("remark") == null ? "" : param.get("remark").toString();
        String type = param.get("type") == null ? "" : param.get("type").toString();
        String payAmount = param.get("payAmount") == null ? "" : param.get("payAmount").toString();
        Integer usePoint = param.get("usePoint") == null ? 0 : Integer.parseInt(param.get("usePoint").toString());
        String payType = param.get("payType") == null ? "JSAPI" : param.get("payType").toString();
        String authCode = param.get("authCode") == null ? "" : param.get("authCode").toString();
        Integer storeId = param.get("storeId") == null ? 0 : Integer.parseInt(param.get("storeId").toString());

        // 立即购买商品
        Integer goodsId = param.get("goodsId") == null ? 0 : Integer.parseInt(param.get("goodsId").toString());
        Integer skuId = param.get("skuId") == null ? 0 : Integer.parseInt(param.get("skuId").toString());
        Integer buyNum = param.get("buyNum") == null ? 1 : Integer.parseInt(param.get("buyNum").toString());

        // 订单模式(配送or自取)
        String orderMode = param.get("orderMode") == null ? "" : param.get("orderMode").toString();

        // 生成订单数据
        OrderDto orderDto = new OrderDto();
        orderDto.setRemark(remark);
        orderDto.setUserId(userInfo.getId());
        orderDto.setStoreId(storeId);
        orderDto.setType(type);
        orderDto.setGoodsId(goodsId);
        orderDto.setSkuId(skuId);
        orderDto.setBuyNum(buyNum);
        orderDto.setOrderMode(orderMode);
        orderDto.setOperator(operator);
        orderDto.setPayType(payType);

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
                orderDto.setAmount(new BigDecimal(payAmount));
                orderDto.setDiscount(new BigDecimal(payAmount).subtract(payAmountDiscount));
            } else {
                orderDto.setAmount(new BigDecimal(payAmount));
                orderDto.setDiscount(new BigDecimal("0"));
            }
        }

        // 使用积分抵扣
        if (usePoint > 0) {
            List<MtSetting> settingList = settingService.getSettingList(SettingTypeEnum.POINT.getKey());
            String canUsedAsMoney = "false";
            String exchangeNeedPoint = "0";
            for (MtSetting setting : settingList) {
                if (setting.getName().equals("canUsedAsMoney")) {
                    canUsedAsMoney = setting.getValue();
                } else if (setting.getName().equals("exchangeNeedPoint")) {
                    exchangeNeedPoint = setting.getValue();
                }
            }
            // 是否可以使用积分，并且积分数量足够
            if (canUsedAsMoney.equals("true") && Float.parseFloat(exchangeNeedPoint) > 0 && (userInfo.getPoint() >= usePoint)) {
                orderDto.setUsePoint(usePoint);
                orderDto.setPointAmount(new BigDecimal(usePoint).divide(new BigDecimal(exchangeNeedPoint)));
                orderDto.setAmount(new BigDecimal(payAmount).add(orderDto.getPointAmount()));
            }
        }

        // 升级订单
        if (orderDto.getType().equals(OrderTypeEnum.MEMBER.getKey())) {
            orderDto.setParam(targetId+"");
            MtUserGrade userGrade = userGradeService.queryUserGradeById(targetId);
            orderDto.setRemark("付费升级" + userGrade.getName());
            orderDto.setAmount(new BigDecimal(userGrade.getCatchValue().toString()));
        }

        // 生成订单
        MtOrder orderInfo = orderService.createOrder(orderDto);
        param.put("orderId", orderInfo.getId());

        // 生成支付订单
        String ip = CommonUtil.getIPFromHttpRequest(request);
        BigDecimal realPayAmount = orderInfo.getAmount().subtract(new BigDecimal(orderInfo.getDiscount().toString())).subtract(new BigDecimal(orderInfo.getPointAmount().toString()));
        BigDecimal wxPayAmount = realPayAmount.multiply(new BigDecimal("100"));
        ResponseObject paymentInfo = weixinService.createPrepayOrder(userInfo, orderInfo, (wxPayAmount.intValue()), authCode, 0, ip);
        if (paymentInfo.getData() == null) {
            return getFailureResult(3000);
        }

        Map<String, Object> outParams = new HashMap();
        outParams.put("isCreated", true);
        outParams.put("payType", "wechat");
        outParams.put("orderInfo", orderInfo);
        outParams.put("payment", paymentInfo.getData());

        ResponseObject responseObject = getSuccessResult(outParams);

        return getSuccessResult(responseObject.getData());
    }
}

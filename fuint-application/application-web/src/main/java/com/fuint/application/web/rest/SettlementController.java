package com.fuint.application.web.rest;

import com.fuint.application.dao.entities.MtUser;
import com.fuint.application.service.coupon.CouponService;
import com.fuint.application.service.token.TokenService;
import com.fuint.application.service.usercoupon.UserCouponService;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.ResponseObject;
import com.fuint.application.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 结算接口controller
 * Created by zach on 2021/5/2.
 */
@RestController
@RequestMapping(value = "/rest/settlement")
public class SettlementController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(PageController.class);

    /**
     * 会员卡券服务接口
     * */
    @Autowired
    private UserCouponService userCouponService;

    /**
     * Token服务接口
     */
    @Autowired
    private TokenService tokenService;

    /**
     * 结算提交
     */
    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject submit(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        MtUser mtUser = tokenService.getUserInfoByToken(token);
        if (null == mtUser) {
            return getFailureResult(1001);
        }
        param.put("userId", mtUser.getId());

        // @todo 支付成功，生成order数据
        param.put("orderId", 1001);
        userCouponService.preStore(param);

        Map<String, Object> outParams = new HashMap();

        outParams.put("isCreated", true);
        outParams.put("payType", "WECHAT");

        ResponseObject responseObject = getSuccessResult(outParams);

        return getSuccessResult(responseObject.getData());
    }
}

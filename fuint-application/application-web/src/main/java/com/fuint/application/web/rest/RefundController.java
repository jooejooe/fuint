package com.fuint.application.web.rest;

import com.fuint.application.dao.entities.MtRefund;
import com.fuint.application.dto.RefundDto;
import com.fuint.application.service.refund.RefundService;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.ResponseObject;
import com.fuint.application.BaseController;
import com.fuint.application.dao.entities.MtUser;
import com.fuint.application.service.token.TokenService;
import jodd.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 售后类controller
 * Created by FSQ
 * Contact wx fsq_better
 */
@RestController
@RequestMapping(value = "/rest/refund")
public class RefundController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(RefundController.class);

    @Autowired
    private TokenService tokenService;

    /**
     * 售后服务接口
     * */
    @Autowired
    private RefundService refundService;

    /**
     * 获取订单列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request, @RequestParam Map<String, Object> param) throws BusinessCheckException{
        String userToken = request.getHeader("Access-Token");
        MtUser userInfo = tokenService.getUserInfoByToken(userToken);

        if (userInfo == null) {
            return getFailureResult(1001, "用户未登录");
        }

        param.put("userId", userInfo.getId());
        ResponseObject orderData = refundService.getUserRefundList(param);
        return getSuccessResult(orderData.getData());
    }

    /**
     * 售后订单提交
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

        String orderSn = param.get("orderSn") == null ? "" : param.get("orderSn").toString();
        String remark = param.get("remark") == null ? "" : param.get("remark").toString();

        RefundDto refundDto = new RefundDto();
        refundDto.setUserId(mtUser.getId());
        refundDto.setOrderSn(orderSn);
        refundDto.setRemark(remark);
        MtRefund refundInfo = refundService.createRefund(refundDto);

        Map<String, Object> outParams = new HashMap();
        outParams.put("refundInfo", refundInfo);

        ResponseObject responseObject = getSuccessResult(outParams);

        return getSuccessResult(responseObject.getData());
    }

    /**
     * 获取订单详情
     */
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject detail(HttpServletRequest request) throws BusinessCheckException{
        String userToken = request.getHeader("Access-Token");
        MtUser mtUser = tokenService.getUserInfoByToken(userToken);

        if (mtUser == null) {
            return getFailureResult(1001, "用户未登录");
        }

        String refundId = request.getParameter("refundId");
        if (StringUtil.isEmpty(refundId)) {
            return getFailureResult(2000, "售后订单ID不能为空");
        }

        RefundDto refundInfo = refundService.getRefundById(Integer.parseInt(refundId));

        return getSuccessResult(refundInfo);
    }
}

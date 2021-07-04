package com.fuint.application.web.rest;

import com.fuint.application.dao.entities.*;
import com.fuint.application.dto.AssetDto;
import com.fuint.application.enums.CouponTypeEnum;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.enums.UserCouponStatusEnum;
import com.fuint.application.service.confirmer.ConfirmerService;
import com.fuint.application.service.member.MemberService;
import com.fuint.application.service.usercoupon.UserCouponService;
import com.fuint.application.service.coupon.CouponService;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.base.util.RequestHandler;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.ResponseObject;
import com.fuint.application.BaseController;
import com.fuint.application.service.token.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会员类controller
 * Created by zach on 2021/04/27.
 */
@RestController
@RequestMapping(value = "/rest/user")
public class UserController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(SignController.class);

    @Autowired
    private TokenService tokenService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private UserCouponService userCouponService;

    @Autowired
    private ConfirmerService confirmerService;

    @Autowired
    private CouponService couponService;

    /**
     * 获取会员信息
     */
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject info(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException{
        String userToken = request.getHeader("Access-Token");
        MtUser userInfo = tokenService.getUserInfoByToken(userToken);

        if (null == userInfo) {
            return getFailureResult(1001, "用户未登录");
        }

        userInfo = memberService.queryMemberById(userInfo.getId());

        if (null == userInfo.getGradeId()) {
            userInfo.setGradeId("0");
        }

        MtUserGrade gradeInfo = memberService.queryMemberGradeByGradeId(Integer.parseInt(userInfo.getGradeId()));

        Map<String, Object> outParams = new HashMap<>();
        outParams.put("userInfo", userInfo);
        outParams.put("gradeInfo", gradeInfo);

        // 是否商户核销员
        boolean isMerchant = false;
        MtConfirmer confirmInfo = confirmerService.queryConfirmerByUserId(userInfo.getId());
        if (null != confirmInfo) {
            if (confirmInfo.getAuditedStatus().equals(StatusEnum.ENABLED.getKey())) {
                isMerchant = true;
            }
        }
        outParams.put("isMerchant", isMerchant);

        return getSuccessResult(outParams);
    }

    /**
     * 获取会员数据信息
     */
    @RequestMapping(value = "/asset", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject asset(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException{
        String userToken = request.getHeader("Access-Token");
        MtUser mtUser = tokenService.getUserInfoByToken(userToken);

        if (mtUser == null) {
            return getFailureResult(1001, "用户未登录");
        }

        Integer couponNum = 0;
        Integer preStoreNum = 0;
        Integer timerNum = 0;

        List<String> statusList = Arrays.asList(UserCouponStatusEnum.UNUSED.getKey());
        List<MtUserCoupon> dataList = userCouponService.getUserCouponList(mtUser.getId(), statusList);

        PaginationRequest requestName = RequestHandler.buildPaginationRequest(request, model);
        requestName.getSearchParams().put("EQ_status", StatusEnum.ENABLED.getKey());
        requestName.setCurrentPage(1);
        requestName.setPageSize(10000);
        PaginationResponse<MtCoupon> couponData = couponService.queryCouponListByPagination(requestName);
        List<MtCoupon> couponList = couponData.getContent();

        for (int i = 0; i < dataList.size(); i++) {
            MtCoupon couponInfo = new MtCoupon();
            for (int j = 0; j < couponList.size(); j++) {
                if (dataList.get(i).getCouponId() == couponList.get(j).getId()) {
                    couponInfo = couponList.get(j);
                    break;
                }
            }

            boolean isEffective = couponService.isCouponEffective(couponInfo);
            if (!isEffective) {
               continue;
            }
            
            if (dataList.get(i).getType().equals(CouponTypeEnum.COUPON.getKey())) {
                couponNum++;
            }
            if (dataList.get(i).getType().equals(CouponTypeEnum.PRESTORE.getKey())) {
                preStoreNum++;
            }
            if (dataList.get(i).getType().equals(CouponTypeEnum.TIMER.getKey())) {
                timerNum++;
            }
        }

        AssetDto asset = new AssetDto();
        asset.setCoupon(couponNum);
        asset.setPrestore(preStoreNum);
        asset.setTimer(timerNum);

        Map<String, Object> outParams = new HashMap<>();
        outParams.put("asset", asset);

        return getSuccessResult(outParams);
    }
}

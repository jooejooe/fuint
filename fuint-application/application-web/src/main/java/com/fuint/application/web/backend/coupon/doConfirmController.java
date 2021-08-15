package com.fuint.application.web.backend.coupon;

import com.fuint.application.dao.entities.MtCoupon;
import com.fuint.application.dao.entities.MtUserCoupon;
import com.fuint.application.dao.repositories.MtUserCouponRepository;
import com.fuint.application.service.coupon.CouponService;
import com.fuint.exception.BusinessCheckException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import com.fuint.application.web.backend.base.BaseController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 卡券核销类controller
 * Created by FSQ
 * Contact wx fsq_better
 */
@Controller
@RequestMapping(value = "/backend/doConfirm")
public class doConfirmController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(couponController.class);

    @Autowired
    private MtUserCouponRepository userCouponRepository;

    /**
     * 卡券服务接口
     */
    @Autowired
    private CouponService couponService;

    /**
     * 卡券列表查询
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     * @return 卡券列表展现页面
     */
    @RequiresPermissions("backend/coupon/confirm")
    @RequestMapping(value = "/index")
    public String index(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        Integer userCouponId = request.getParameter("id") == null ? 0 : Integer.parseInt(request.getParameter("id"));

        MtUserCoupon userCoupon = userCouponRepository.findOne(userCouponId);
        MtCoupon couponInfo = couponService.queryCouponById(userCoupon.getCouponId().longValue());

        model.addAttribute("couponInfo", couponInfo);
        model.addAttribute("userCoupon", userCoupon);

        return "coupon/confirm";
    }
}

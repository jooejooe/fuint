package com.fuint.application.web.backend.home;

import com.fuint.application.FrameworkConstants;
import com.fuint.application.ResponseObject;
import com.fuint.application.service.confirmlog.ConfirmLogService;
import com.fuint.application.service.member.MemberService;
import com.fuint.application.service.order.OrderService;
import com.fuint.exception.BusinessCheckException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 首页控制器
 * Created by FSQ
 * Contact wx fsq_better
 */
@Controller
@RequestMapping(value = "/backend/home")
public class homeController {

    private static final Logger logger = LoggerFactory.getLogger(homeController.class);

    @Autowired
    private MemberService memberService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ConfirmLogService confirmLogService;

    /**
     * 首页
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequestMapping(value = "/index")
    public String index(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {

        // 会员数
        Long userCount = memberService.getUserCount();

        // 订单数
        Long orderCount = orderService.getOrderCount();

        // 收款额
        Long totalPay = 0L;

        // 核销券数
        Long confirmCount = confirmLogService.getConfirmCount();

        // 售后订单
        Long refundCount = 0L;

        model.addAttribute("totalPay", totalPay);
        model.addAttribute("totalOrder", orderCount);
        model.addAttribute("totalMember", userCount);
        model.addAttribute("confirmCount", confirmCount);
        model.addAttribute("refundCount", refundCount);

        return "home/index";
    }

    /**
     * 首页图表统计数据
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequestMapping(value = "/statistic")
    @ResponseBody
    public ResponseObject statistic(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        String tag = request.getParameter("tag");

        Map<String, Object> resultMap = new HashMap<>();
        if (tag.equals("new_user,user_active")) {
            int data[][] = {{100, 290, 300, 401, 680, 790, 902}, {10, 300, 420, 710, 880, 700, 500}};
            resultMap.put("data", data);
        } else {
            int data[][] = {{52, 40, 30, 40, 68, 79, 72}, {10, 30, 40, 10, 88, 70, 80}};
            resultMap.put("data", data);
        }

        String label[] = {"4月6日", "4月7日", "4月8日", "4月9日", "4月10日", "昨天", "今天"};
        resultMap.put("labels", label);

        return new ResponseObject(FrameworkConstants.HTTP_RESPONSE_CODE_SUCCESS, "请求成功", resultMap);
    }

    /**
     * 核销卡券
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequestMapping(value = "/confirmCoupon")
    public String confirmCoupon(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        return "home/confirmCoupon";
    }
}

package com.fuint.application.web.rest;

import com.fuint.application.dao.entities.MtOrder;
import com.fuint.application.dto.OrderDto;
import com.fuint.application.dto.UserOrderDto;
import com.fuint.application.enums.OrderStatusEnum;
import com.fuint.application.service.order.OrderService;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.ResponseObject;
import com.fuint.application.BaseController;
import com.fuint.application.dao.entities.MtUser;
import com.fuint.application.service.token.TokenService;
import jodd.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单类controller
 * Created by FSQ
 * Contact wx fsq_better
 */
@RestController
@RequestMapping(value = "/rest/order")
public class OrderController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private TokenService tokenService;

    /**
     * 订单服务接口
     * */
    @Autowired
    private OrderService orderService;

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
        ResponseObject orderData = orderService.getUserOrderList(param);
        return getSuccessResult(orderData.getData());
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

        String orderId = request.getParameter("orderId");
        if (StringUtil.isEmpty(orderId)) {
            return getFailureResult(2000, "订单不能为空");
        }

        UserOrderDto orderInfo = orderService.getOrderById(Integer.parseInt(orderId));

        return getSuccessResult(orderInfo);
    }

    /**
     * 取消订单
     */
    @RequestMapping(value = "/cancel", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject cancel(HttpServletRequest request) throws BusinessCheckException{
        String userToken = request.getHeader("Access-Token");
        MtUser mtUser = tokenService.getUserInfoByToken(userToken);

        if (mtUser == null) {
            return getFailureResult(1001, "用户未登录");
        }

        String orderId = request.getParameter("orderId");
        if (StringUtil.isEmpty(orderId)) {
            return getFailureResult(2000, "订单不能为空");
        }

        UserOrderDto order = orderService.getOrderById(Integer.parseInt(orderId));
        if (!order.getUserId().equals(mtUser.getId())) {
            return getFailureResult(2000, "订单信息有误");
        }

        OrderDto reqDto = new OrderDto();
        reqDto.setId(Integer.parseInt(orderId));
        reqDto.setStatus(OrderStatusEnum.CANCEL.getKey());
        MtOrder orderInfo = orderService.updateOrder(reqDto);

        return getSuccessResult(orderInfo);
    }

    /**
     * 获取待办订单
     */
    @RequestMapping(value = "/todoCounts", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject todoCounts(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException{
        String userToken = request.getHeader("Access-Token");
        MtUser mtUser = tokenService.getUserInfoByToken(userToken);

        if (mtUser == null) {
            return getFailureResult(1001, "用户未登录");
        }

        Map<String, Object> param = new HashMap<>();
        param.put("EQ_status", OrderStatusEnum.CREATED.getKey());
        param.put("EQ_userId", mtUser.getId()+"");
        List<MtOrder> data = orderService.getOrderListByParams(param);

        Map<String, Object> result = new HashMap<>();
        result.put("payment", data.size());

        return getSuccessResult(result);
    }
}

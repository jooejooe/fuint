package com.fuint.application.web.backend.orderManager;

import com.fuint.application.ResponseObject;
import com.fuint.application.dao.entities.MtOrder;
import com.fuint.application.dto.OrderDto;
import com.fuint.application.dto.UserOrderDto;
import com.fuint.application.enums.OrderStatusEnum;
import com.fuint.application.enums.OrderTypeEnum;
import com.fuint.application.enums.PayStatusEnum;
import com.fuint.application.service.order.OrderService;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.util.RequestHandler;
import com.fuint.exception.BusinessCheckException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 订单管理controller
 * Created by FSQ
 * Contact wx fsq_better
 */
@Controller
@RequestMapping(value = "/backend/order")
public class OrderManagerController {

    private static final Logger logger = LoggerFactory.getLogger(OrderManagerController.class);

    /**
     * 订单服务接口
     * */
    @Autowired
    private OrderService orderService;

    /**
     * 订单列表查询
     *
     * @param request  HttpServletRequest对象
     * @param model    SpringFramework Model对象
     * @return
     */
    @RequiresPermissions("backend/order/list")
    @RequestMapping(value = "/list")
    public String list(HttpServletRequest request, Model model) throws BusinessCheckException {
        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);
        Map<String, Object> params = paginationRequest.getSearchParams();

        Map<String, Object> param = new HashMap<>();
        param.put("type", params.get("EQ_type"));
        param.put("orderSn", params.get("EQ_orderSn"));
        param.put("status", params.get("EQ_status"));
        param.put("payStatus", params.get("EQ_payStatus"));
        param.put("pageNumber", paginationRequest.getCurrentPage());
        param.put("pageSize", paginationRequest.getPageSize());
        param.put("userId", params.get("EQ_userId"));
        param.put("mobile", params.get("EQ_mobile"));

        ResponseObject response = orderService.getUserOrderList(param);
        OrderTypeEnum[] typeList = OrderTypeEnum.values();
        OrderStatusEnum[] statusList = OrderStatusEnum.values();
        PayStatusEnum[] payStatusList = PayStatusEnum.values();

        model.addAttribute("paginationResponse", response.getData());
        model.addAttribute("typeList", typeList);
        model.addAttribute("statusList", statusList);
        model.addAttribute("payStatusList", payStatusList);
        model.addAttribute("params", params);

        return "order/list";
    }

    /**
     * 订单详情
     * @param request  HttpServletRequest对象
     * @param model    SpringFramework Model对象
     * @return
     * */
    @RequiresPermissions("backend/order/detail/{orderId}")
    @RequestMapping(value = "/detail/{orderId}")
    public String detail(HttpServletRequest request, Model model, @PathVariable("orderId") Integer orderId) throws BusinessCheckException {
        UserOrderDto orderInfo = orderService.getOrderById(orderId);

        model.addAttribute("orderInfo", orderInfo);
        return "order/detail";
    }
    /**
     * 确认发货
     * @param request  HttpServletRequest对象
     * @param model    SpringFramework Model对象
     * @return
     * */
    @RequiresPermissions("backend/order/delivered/{orderId}")
    @RequestMapping(value = "/delivered/{orderId}")
    public String delivered(HttpServletRequest request, Model model, @PathVariable("orderId") Integer orderId) throws BusinessCheckException {
        OrderDto dto = new OrderDto();
        dto.setId(orderId);
        dto.setStatus(OrderStatusEnum.DELIVERED.getKey());

        orderService.updateOrder(dto);

        return "redirect:/backend/order/list";
    }
}

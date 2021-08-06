package com.fuint.application.web.backend.refundManager;

import com.fuint.application.dao.entities.MtOrder;
import com.fuint.application.dto.UserOrderDto;
import com.fuint.application.enums.OrderTypeEnum;
import com.fuint.application.service.order.OrderService;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
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
import java.util.Map;

/**
 * 售后管理controller
 * Created by FSQ
 * Contact wx fsq_better
 */
@Controller
@RequestMapping(value = "/backend/refund")
public class RefundManagerController {

    private static final Logger logger = LoggerFactory.getLogger(RefundManagerController.class);

    /**
     * 订单服务接口
     * */
    @Autowired
    private OrderService orderService;

    /**
     * 退款列表查询
     *
     * @param request  HttpServletRequest对象
     * @param model    SpringFramework Model对象
     * @return
     */
    @RequiresPermissions("backend/refund/index")
    @RequestMapping(value = "/index")
    public String list(HttpServletRequest request, Model model) throws BusinessCheckException {
        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);
        Map<String, Object> params = paginationRequest.getSearchParams();

        PaginationResponse<MtOrder> paginationResponse = orderService.getOrderListByPagination(paginationRequest);
        OrderTypeEnum[] typeList = OrderTypeEnum.values();

        // 取订单类型名称
        for (MtOrder order :paginationResponse.getContent()) {
          for (OrderTypeEnum type: typeList) {
              if (type.getKey().equals(order.getType())) {
                 order.setTypeName(type.getValue());
              }
          }
        }

        model.addAttribute("paginationResponse", paginationResponse);
        model.addAttribute("typeList", typeList);
        model.addAttribute("params", params);

        return "refund/list";
    }

    /**
     * 退款详情
     * @param request  HttpServletRequest对象
     * @param model    SpringFramework Model对象
     * @return
     * */
    @RequiresPermissions("backend/refund/detail/{refundId}")
    @RequestMapping(value = "/detail/{refundId}")
    public String detail(HttpServletRequest request, Model model, @PathVariable("refundId") Integer refundId) throws BusinessCheckException {
        UserOrderDto orderInfo = orderService.getOrderById(refundId);

        model.addAttribute("orderInfo", orderInfo);
        return "refund/detail";
    }
}

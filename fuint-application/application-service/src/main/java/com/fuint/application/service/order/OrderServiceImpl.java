package com.fuint.application.service.order;

import com.fuint.application.BaseService;
import com.fuint.application.ResponseObject;
import com.fuint.application.config.Constants;
import com.fuint.application.dao.entities.MtCoupon;
import com.fuint.application.dao.entities.MtOrder;
import com.fuint.application.dao.entities.MtUser;
import com.fuint.application.dao.repositories.MtOrderRepository;
import com.fuint.application.dto.*;
import com.fuint.application.enums.OrderTypeEnum;
import com.fuint.application.enums.PayStatusEnum;
import com.fuint.application.service.coupon.CouponService;
import com.fuint.application.service.member.MemberService;
import com.fuint.application.util.CommonUtil;
import com.fuint.application.util.DateUtil;
import com.fuint.base.annoation.OperationServiceLog;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import com.fuint.exception.BusinessRuntimeException;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.enums.OrderStatusEnum;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 订单接口实现类
 * Created by FSQ
 * Contact wx fsq_better
 */
@Service
public class OrderServiceImpl extends BaseService implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private MtOrderRepository orderRepository;

    @Autowired
    private CouponService couponService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private Environment env;

    /**
     * 获取用户订单列表
     * @param paramMap
     * @throws BusinessCheckException
     * */
    @Override
    @Transactional
    public ResponseObject getUserOrderList(Map<String, Object> paramMap) throws BusinessCheckException {
        Integer pageNumber = paramMap.get("pageNumber") == null ? Constants.PAGE_NUMBER : Integer.parseInt(paramMap.get("pageNumber").toString());
        Integer pageSize = paramMap.get("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(paramMap.get("pageSize").toString());
        String userId = paramMap.get("userId") == null ? "" : paramMap.get("userId").toString();
        String status =  paramMap.get("status") == null ? "": paramMap.get("status").toString();
        String payStatus =  paramMap.get("payStatus") == null ? "": paramMap.get("payStatus").toString();
        String dataType =  paramMap.get("dataType") == null ? "": paramMap.get("dataType").toString();
        String type =  paramMap.get("type") == null ? "": paramMap.get("type").toString();
        String orderSn =  paramMap.get("orderSn") == null ? "": paramMap.get("orderSn").toString();
        String mobile =  paramMap.get("mobile") == null ? "": paramMap.get("mobile").toString();

        if (dataType.equals("pay")) {
            status = OrderStatusEnum.CREATED.getKey();// 待支付
        } else if(dataType.equals("paid")) {
            status = OrderStatusEnum.PAID.getKey(); // 已支付
        } else if(dataType.equals("cancel")) {
            status = OrderStatusEnum.CANCEL.getKey(); // 已取消
        }

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(pageNumber);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> searchParams = new HashedMap();

        if (StringUtils.isNotEmpty(orderSn)) {
            searchParams.put("EQ_orderSn", orderSn);
        }
        if (StringUtils.isNotEmpty(status)) {
            searchParams.put("EQ_status", status);
        }
        if (StringUtils.isNotEmpty(payStatus)) {
            searchParams.put("EQ_payStatus", payStatus);
        }
        if (StringUtils.isNotEmpty(mobile)) {
            MtUser userInfo = memberService.queryMemberByMobile(mobile);
            userId = userInfo.getId()+"";
        }
        if (StringUtils.isNotEmpty(userId)) {
            searchParams.put("EQ_userId", userId);
        }
        if (StringUtils.isNotEmpty(type)) {
            searchParams.put("EQ_type", type);
        }

        paginationRequest.setSearchParams(searchParams);
        paginationRequest.setSortColumn(new String[]{"createTime desc", "status asc"});
        PaginationResponse<MtOrder> paginationResponse = orderRepository.findResultsByPagination(paginationRequest);

        List<UserOrderDto> dataList = new ArrayList<>();
        if (paginationResponse.getContent().size() > 0) {
            for (MtOrder order : paginationResponse.getContent()) {
                UserOrderDto dto = this._dealOrderDetail(order);
                dataList.add(dto);
            }
        }

        Long total = paginationResponse.getTotalElements();
        PageRequest pageRequest = new PageRequest(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        Page page = new PageImpl(dataList, pageRequest, total.longValue());
        PaginationResponse<UserOrderDto> pageResponse = new PaginationResponse(page, UserOrderDto.class);
        pageResponse.setContent(page.getContent());
        pageResponse.setCurrentPage(pageResponse.getCurrentPage() + 1);

        return getSuccessResult(pageResponse);
    }

    /**
     * 创建订单
     *
     * @param orderDto
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "创建订单")
    public MtOrder createOrder(OrderDto orderDto) throws BusinessCheckException {
        MtOrder MtOrder = new MtOrder();
        if (null != orderDto.getId()) {
            MtOrder.setId(MtOrder.getId());
        }

        String orderSn = CommonUtil.createOrderSN(orderDto.getUserId()+"");
        MtOrder.setOrderSn(orderSn);
        MtOrder.setUserId(orderDto.getUserId());
        MtOrder.setCouponId(orderDto.getCouponId());
        MtOrder.setParam(orderDto.getParam());
        MtOrder.setRemark(orderDto.getRemark());
        MtOrder.setStatus(OrderStatusEnum.CREATED.getKey());
        MtOrder.setType(orderDto.getType());
        MtOrder.setAmount(orderDto.getAmount());
        MtOrder.setPayStatus(PayStatusEnum.WAIT.getKey());

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dt = format.format(new Date());
            Date addTime = format.parse(dt);
            MtOrder.setUpdateTime(addTime);
            MtOrder.setCreateTime(addTime);
        } catch (ParseException e) {
            throw new BusinessRuntimeException("日期转换异常 " + e.getMessage());
        }

        return orderRepository.save(MtOrder);
    }

    /**
     * 根据ID获取订单详情
     *
     * @param id 订单ID
     * @throws BusinessCheckException
     */
    @Override
    public UserOrderDto getOrderById(Integer id) throws BusinessCheckException {
        MtOrder orderInfo = orderRepository.findOne(id);
        return this. _dealOrderDetail(orderInfo);
    }

    /**
     * 根据订单号获取订单详情
     *
     * @param orderSn 订单号
     * @throws BusinessCheckException
     */
    @Override
    public UserOrderDto getOrderByOrderSn(String orderSn) throws BusinessCheckException {
        MtOrder orderInfo = orderRepository.findByOrderSn(orderSn);
        return this. _dealOrderDetail(orderInfo);
    }

    /**
     * 根据ID删除数据
     *
     * @param id       订单ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "删除订单")
    public void deleteOrder(Integer id, String operator) throws BusinessCheckException {
        MtOrder MtOrder = orderRepository.findOne(id);
        if (null == MtOrder) {
            return;
        }

        MtOrder.setStatus(StatusEnum.DISABLE.getKey());
        MtOrder.setUpdateTime(new Date());

        orderRepository.save(MtOrder);
    }

    /**
     * 更新订单
     *
     * @param orderDto
     * @throws BusinessCheckException
     */
    @Override
    @Transactional
    @OperationServiceLog(description = "更新订单")
    public MtOrder updateOrder(OrderDto orderDto) throws BusinessCheckException {
        MtOrder MtOrder = orderRepository.findOne(orderDto.getId());
        if (null == MtOrder || StatusEnum.DISABLE.getKey().equals(MtOrder.getStatus())) {
            log.error("该订单状态异常");
            throw new BusinessCheckException("该订单状态异常");
        }

        MtOrder.setId(orderDto.getId());
        MtOrder.setUpdateTime(new Date());

        if (null != orderDto.getOperator()) {
            MtOrder.setOperator(orderDto.getOperator());
        }

        if (null != orderDto.getStatus()) {
            MtOrder.setStatus(orderDto.getStatus());
        }

        if (null != orderDto.getPayAmount()) {
            MtOrder.setPayAmount(orderDto.getPayAmount());
        }

        if (null != orderDto.getPayTime()) {
            MtOrder.setPayTime(orderDto.getPayTime());
        }

        if (null != orderDto.getPayStatus()) {
            MtOrder.setPayStatus(orderDto.getPayStatus());
        }

        return orderRepository.save(MtOrder);
    }

    @Override
    public List<MtOrder> getOrderListByParams(Map<String, Object> params) throws BusinessCheckException {
        Specification<MtOrder> specification = orderRepository.buildSpecification(params);
        Sort sort = new Sort(Sort.Direction.ASC, "createTime");
        List<MtOrder> result = orderRepository.findAll(specification, sort);
        return result;
    }

    /**
     * 处理订单详情
     * @param orderInfo
     * @return UserOrderDto
     * */
    private UserOrderDto _dealOrderDetail(MtOrder orderInfo) throws BusinessCheckException {
        UserOrderDto dto = new UserOrderDto();

        dto.setId(orderInfo.getId());
        dto.setUserId(orderInfo.getUserId());
        dto.setCouponId(orderInfo.getCouponId());
        dto.setOrderSn(orderInfo.getOrderSn());
        dto.setRemark(orderInfo.getRemark());
        dto.setType(orderInfo.getType());
        dto.setCreateTime(DateUtil.formatDate(orderInfo.getCreateTime(), "yyyy.MM.dd HH:mm"));
        dto.setUpdateTime(DateUtil.formatDate(orderInfo.getUpdateTime(), "yyyy.MM.dd HH:mm"));
        dto.setAmount(orderInfo.getAmount());
        dto.setPayAmount(orderInfo.getPayAmount());
        dto.setDiscount(orderInfo.getDiscount());
        dto.setStatus(orderInfo.getStatus());
        dto.setParam(orderInfo.getParam());
        dto.setPayStatus(orderInfo.getPayStatus());
        if (orderInfo.getPayTime() != null) {
            dto.setPayTime(DateUtil.formatDate(orderInfo.getPayTime(), "yyyy.MM.dd HH:mm"));
        }

        if (dto.getType().equals(OrderTypeEnum.PRESTORE.getKey())) {
            dto.setTypeName(OrderTypeEnum.PRESTORE.getValue());
        } else if(dto.getType().equals(OrderTypeEnum.PAYMENT.getKey())) {
            dto.setTypeName(OrderTypeEnum.PAYMENT.getValue());
        } else if(dto.getType().equals(OrderTypeEnum.GOOGS.getKey())) {
            dto.setTypeName(OrderTypeEnum.GOOGS.getValue());
        } else if(dto.getType().equals(OrderTypeEnum.MEMBER.getKey())) {
            dto.setTypeName(OrderTypeEnum.MEMBER.getValue());
        } else if(dto.getType().equals(OrderTypeEnum.RECHARGE.getKey())) {
            dto.setTypeName(OrderTypeEnum.RECHARGE.getValue());
        }

        if (dto.getStatus().equals(OrderStatusEnum.CREATED.getKey())) {
            dto.setStatusText(OrderStatusEnum.CREATED.getValue());
        } else if(dto.getStatus().equals(OrderStatusEnum.CANCEL.getKey())) {
            dto.setStatusText(OrderStatusEnum.CANCEL.getValue());
        } else if(dto.getStatus().equals(OrderStatusEnum.PAID.getKey())) {
            dto.setStatusText(OrderStatusEnum.PAID.getValue());
        }

        // 下单用户信息暂时直接取会员个人信息
        OrderUserDto userInfo = new OrderUserDto();
        MtUser user = memberService.queryMemberById(orderInfo.getUserId());
        if (user != null) {
            userInfo.setName(user.getName());
            userInfo.setMobile(user.getMobile());
            userInfo.setCardNo(user.getCarNo());
            userInfo.setAddress(user.getAddress());
            dto.setUserInfo(userInfo);
        }

        List<OrderGoodsDto> goodsList = new ArrayList<>();

        String baseImage = env.getProperty("images.website");

        // 预存卡的订单
        if (orderInfo.getType().equals(OrderTypeEnum.PRESTORE.getKey())) {
            MtCoupon coupon = couponService.queryCouponById(orderInfo.getCouponId().longValue());
            String[] paramArr = orderInfo.getParam().split(",");
            for(int i = 0; i < paramArr.length; i++) {
                String[] item = paramArr[i].split("_");
                OrderGoodsDto o = new OrderGoodsDto();
                o.setId(coupon.getId());
                o.setType(OrderTypeEnum.PRESTORE.getKey());
                o.setName("预存￥"+item[0]+"升至￥"+item[1]);
                o.setNum(Integer.parseInt(item[2]));
                o.setPrice(item[0]);
                o.setImage(baseImage + coupon.getImage());
                goodsList.add(o);
            }
        }

        dto.setGoods(goodsList);

        return dto;
    }

    /**
     * 获取订单数量
     * */
    @Override
    public Long getOrderCount() throws BusinessCheckException {
        return orderRepository.getOrderCount();
    }
}

package com.fuint.application.service.order;

import com.fuint.application.BaseService;
import com.fuint.application.ResponseObject;
import com.fuint.application.config.Constants;
import com.fuint.application.dao.entities.*;
import com.fuint.application.dao.repositories.*;
import com.fuint.application.dto.*;
import com.fuint.application.enums.*;
import com.fuint.application.service.address.AddressService;
import com.fuint.application.service.cart.CartService;
import com.fuint.application.service.coupon.CouponService;
import com.fuint.application.service.goods.GoodsService;
import com.fuint.application.service.member.MemberService;
import com.fuint.application.service.point.PointService;
import com.fuint.application.util.CommonUtil;
import com.fuint.application.util.DateUtil;
import com.fuint.base.annoation.OperationServiceLog;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
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
    private MtGoodsRepository goodsRepository;

    @Autowired
    private MtOrderGoodsRepository orderGoodsRepository;

    @Autowired
    private MtCartRepository cartRepository;

    @Autowired
    private MtOrderAddressRepository orderAddressRepository;

    @Autowired
    private MtGoodsSkuRepository goodsSkuRepository;

    @Autowired
    private MtRegionRepository regionRepository;

    @Autowired
    private CouponService couponService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private PointService pointService;

    @Autowired
    private CartService cartService;

    @Autowired
    private GoodsService goodsService;

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
                UserOrderDto dto = this._dealOrderDetail(order, false);
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
    @Transactional
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
        MtOrder.setDiscount(orderDto.getDiscount());
        MtOrder.setPayStatus(PayStatusEnum.WAIT.getKey());
        MtOrder.setPointAmount(orderDto.getPointAmount());
        MtOrder.setUsePoint(orderDto.getUsePoint());
        MtOrder.setOrderMode(orderDto.getOrderMode());

        // 扣减积分
        if (orderDto.getUsePoint() > 0) {
            MtPoint reqPointDto = new MtPoint();
            reqPointDto.setUserId(orderDto.getUserId());
            reqPointDto.setAmount(-orderDto.getUsePoint());
            reqPointDto.setOrderSn(orderSn);
            reqPointDto.setDescription("支付扣除" + orderDto.getUsePoint() + "积分");
            pointService.addPoint(reqPointDto);
        }

        MtOrder.setUpdateTime(new Date());
        MtOrder.setCreateTime(new Date());

        // 计算订单总金额
        List<MtCart> cartList = new ArrayList<>();
        List<MtGoods> goodsList = new ArrayList<>();
        Map<String, Object> cartData = new HashMap<>();
        if (orderDto.getType().equals(OrderTypeEnum.GOOGS.getKey())) {
            Map<String, Object> param = new HashMap<>();
            param.put("EQ_status", StatusEnum.ENABLED.getKey());
            if (orderDto.getGoodsId() < 1) {
                cartList = cartService.queryCartListByParams(param);
            } else {
                // 直接购买
                MtCart mtCart = new MtCart();
                mtCart.setGoodsId(orderDto.getGoodsId());
                mtCart.setSkuId(orderDto.getSkuId());
                mtCart.setNum(orderDto.getBuyNum());
                mtCart.setId(0);
                mtCart.setUserId(orderDto.getUserId());
                mtCart.setStatus(StatusEnum.ENABLED.getKey());
                cartList.add(mtCart);
            }

            cartData = this.calculateCartGoods(cartList);
            MtOrder.setAmount(new BigDecimal(cartData.get("totalPrice").toString()));
        }

        MtOrder orderInfo = orderRepository.save(MtOrder);
        if (orderInfo == null) {
            throw new BusinessCheckException("生成订单失败，请稍后重试");
        }

        // 如果是商品订单，生成订单商品
        if (orderDto.getType().equals(OrderTypeEnum.GOOGS.getKey()) && cartList.size() > 0) {
            Object listObject = cartData.get("list");
            List<ResCartDto> lists =(ArrayList<ResCartDto>)listObject;
            for (ResCartDto cart : lists) {
                 MtOrderGoods orderGoods = new MtOrderGoods();
                 orderGoods.setOrderId(orderInfo.getId());
                 orderGoods.setGoodsId(cart.getGoodsId());
                 orderGoods.setSkuId(cart.getSkuId());
                 orderGoods.setNum(cart.getNum());
                 orderGoods.setPrice(cart.getGoodsInfo().getPrice());
                 orderGoods.setDiscount(new BigDecimal("0"));
                 orderGoods.setStatus(StatusEnum.ENABLED.getKey());
                 orderGoods.setCreateTime(new Date());
                 orderGoods.setUpdateTime(new Date());
                 orderGoodsRepository.save(orderGoods);
                 // 扣减库存
                 MtGoods goodsInfo = goodsRepository.findOne(cart.getGoodsId());
                 if (goodsInfo.getIsSingleSpec().equals("Y")) {
                     // 单规格
                     goodsInfo.setStock(goodsInfo.getStock() - cart.getNum());
                     goodsRepository.save(goodsInfo);
                 } else {
                     // 多规格
                     MtGoodsSku mtGoodsSku = goodsSkuRepository.findOne(cart.getSkuId());
                     goodsInfo.setStock(mtGoodsSku.getStock() - cart.getNum());
                     goodsSkuRepository.save(mtGoodsSku);
                 }
                 if (cart.getId() > 0) {
                    cartRepository.delete(cart.getId());
                 }
            }

            // 需要配送的订单，生成配送地址
            if (orderDto.getOrderMode().equals(OrderModeEnum.EXPRESS.getKey())) {
                Map<String, Object> params = new HashMap<>();
                params.put("userId", orderDto.getUserId().toString());
                params.put("isDefault", "Y");
                List<MtAddress> addressList = addressService.queryListByParams(params);
                MtAddress mtAddress;
                if (addressList.size() > 0) {
                    mtAddress = addressList.get(0);
                } else {
                    throw new BusinessCheckException("配送地址出错了，请重新选择配送地址");
                }
                MtOrderAddress orderAddress = new MtOrderAddress();
                orderAddress.setOrderId(orderInfo.getId());
                orderAddress.setUserId(orderDto.getUserId());
                orderAddress.setName(mtAddress.getName());
                orderAddress.setMobile(mtAddress.getMobile());
                orderAddress.setCityId(mtAddress.getCityId());
                orderAddress.setProvinceId(mtAddress.getProvinceId());
                orderAddress.setRegionId(mtAddress.getRegionId());
                orderAddress.setDetail(mtAddress.getDetail());
                orderAddress.setCreateTime(new Date());
                orderAddressRepository.save(orderAddress);
            }
        }

        return orderInfo;
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
        return this._dealOrderDetail(orderInfo, true);
    }

    /**
     * 根据订单号获取订单详情
     *
     * @param  orderSn 订单号
     * @throws BusinessCheckException
     */
    @Override
    public UserOrderDto getOrderByOrderSn(String orderSn) throws BusinessCheckException {
        MtOrder orderInfo = orderRepository.findByOrderSn(orderSn);
        return this._dealOrderDetail(orderInfo, true);
    }

    /**
     * 根据ID删除订单
     *
     * @param  id       订单ID
     * @param  operator 操作人
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
    private UserOrderDto _dealOrderDetail(MtOrder orderInfo, boolean needAddress) throws BusinessCheckException {
        UserOrderDto dto = new UserOrderDto();

        dto.setId(orderInfo.getId());
        dto.setUserId(orderInfo.getUserId());
        dto.setCouponId(orderInfo.getCouponId());
        dto.setOrderSn(orderInfo.getOrderSn());
        dto.setRemark(orderInfo.getRemark());
        dto.setType(orderInfo.getType());
        dto.setOrderMode(orderInfo.getOrderMode());
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
        } else if(dto.getStatus().equals(OrderStatusEnum.DELIVERY.getKey())) {
            dto.setStatusText(OrderStatusEnum.DELIVERY.getValue());
        } else if(dto.getStatus().equals(OrderStatusEnum.DELIVERED.getKey())) {
            dto.setStatusText(OrderStatusEnum.DELIVERED.getValue());
        } else if(dto.getStatus().equals(OrderStatusEnum.RECEIVED.getKey())) {
            dto.setStatusText(OrderStatusEnum.RECEIVED.getValue());
        } else if(dto.getStatus().equals(OrderStatusEnum.DELETED.getKey())) {
            dto.setStatusText(OrderStatusEnum.DELETED.getValue());
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

        String baseImage = env.getProperty("images.upload.url");

        // 预存卡的订单
        if (orderInfo.getType().equals(OrderTypeEnum.PRESTORE.getKey())) {
            MtCoupon coupon = couponService.queryCouponById(orderInfo.getCouponId());
            String[] paramArr = orderInfo.getParam().split(",");
            for(int i = 0; i < paramArr.length; i++) {
                String[] item = paramArr[i].split("_");
                OrderGoodsDto o = new OrderGoodsDto();
                o.setId(coupon.getId());
                o.setType(OrderTypeEnum.PRESTORE.getKey());
                o.setName("预存￥"+item[0]+"升至￥"+item[1]);
                o.setNum(Integer.parseInt(item[2]));
                o.setPrice(item[0]);
                o.setDiscount("0");
                if (coupon.getImage().indexOf(baseImage) == -1) {
                    o.setImage(baseImage + coupon.getImage());
                }
                goodsList.add(o);
            }
        }

        // 商品订单
        if (orderInfo.getType().equals(OrderTypeEnum.GOOGS.getKey())) {
            Map<String, Object> params = new HashMap<>();
            params.put("EQ_orderId", orderInfo.getId().toString());
            Specification<MtOrderGoods> specification = orderGoodsRepository.buildSpecification(params);
            Sort sort = new Sort(Sort.Direction.ASC, "createTime");
            List<MtOrderGoods> orderGoodsList = orderGoodsRepository.findAll(specification, sort);
            for (MtOrderGoods orderGoods : orderGoodsList) {
                MtGoods goodsInfo = goodsRepository.findOne(orderGoods.getGoodsId());
                OrderGoodsDto o = new OrderGoodsDto();
                o.setId(orderGoods.getId());
                o.setName(goodsInfo.getName());
                if (goodsInfo.getLogo().indexOf(baseImage) == -1) {
                    o.setImage(baseImage + goodsInfo.getLogo());
                }
                o.setType(OrderTypeEnum.GOOGS.getKey());
                o.setNum(orderGoods.getNum());
                o.setSkuId(orderGoods.getSkuId());
                o.setPrice(orderGoods.getPrice().toString());
                o.setDiscount("0");
                if (orderGoods.getSkuId() > 0) {
                    List<GoodsSpecValueDto> specList = goodsService.getSpecListBySkuId(orderGoods.getSkuId());
                    o.setSpecList(specList);
                }
                goodsList.add(o);
            }
        }

        // 配送地址
        if (orderInfo.getOrderMode().equals(OrderModeEnum.EXPRESS.getKey()) && needAddress) {
            MtOrderAddress orderAddress = orderAddressRepository.getOrderAddress(orderInfo.getId());
            if (orderAddress != null) {
                AddressDto address = new AddressDto();
                address.setId(orderAddress.getId());
                address.setName(orderAddress.getName());
                address.setMobile(orderAddress.getMobile());
                address.setDetail(orderAddress.getDetail());
                address.setProvinceId(orderAddress.getProvinceId());
                address.setCityId(orderAddress.getCityId());
                address.setRegionId(orderAddress.getRegionId());

                if (orderAddress.getProvinceId() > 0) {
                    MtRegion mtProvince = regionRepository.findOne(orderAddress.getProvinceId());
                    if (mtProvince != null) {
                        address.setProvinceName(mtProvince.getName());
                    }
                }
                if (orderAddress.getCityId() > 0) {
                    MtRegion mtCity = regionRepository.findOne(orderAddress.getCityId());
                    if (mtCity != null) {
                        address.setCityName(mtCity.getName());
                    }
                }
                if (orderAddress.getRegionId() > 0) {
                    MtRegion mtRegion = regionRepository.findOne(orderAddress.getRegionId());
                    if (mtRegion != null) {
                        address.setRegionName(mtRegion.getName());
                    }
                }

                dto.setAddress(address);
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

    /**
     * 获取支付总金额
     * */
    @Override
    public BigDecimal getPayMoney() {
        return orderRepository.getPayMoney();
    }

    /**
     * 计算商品总价
     * @param cartList
     * @return
     * */
    @Override
    public Map<String, Object> calculateCartGoods(List<MtCart> cartList) throws BusinessCheckException {
        List<ResCartDto> cartDtoList = new ArrayList<>();
        String basePath = env.getProperty("images.upload.url");
        Integer totalNum = 0;
        BigDecimal totalPrice = new BigDecimal("0");

        if (cartList.size() > 0) {
            for (MtCart cart : cartList) {
                totalNum = totalNum + cart.getNum();
                ResCartDto cartDto = new ResCartDto();
                cartDto.setId(cart.getId());
                cartDto.setGoodsId(cart.getGoodsId());
                cartDto.setNum(cart.getNum());
                cartDto.setSkuId(cart.getSkuId());
                cartDto.setUserId(cart.getUserId());

                if (cart.getSkuId() > 0) {
                    List<GoodsSpecValueDto> specList = goodsService.getSpecListBySkuId(cart.getSkuId());
                    cartDto.setSpecList(specList);
                }

                // 购物车商品信息
                MtGoods mtGoodsInfo = goodsService.queryGoodsById(cart.getGoodsId());
                if (StringUtils.isNotEmpty(mtGoodsInfo.getLogo()) && (mtGoodsInfo.getLogo().indexOf(basePath) == -1)) {
                    mtGoodsInfo.setLogo(basePath + mtGoodsInfo.getLogo());
                }

                // 读取sku的数据
                if (cart.getSkuId() > 0) {
                    MtGoods mtGoods = new MtGoods();
                    BeanUtils.copyProperties(mtGoodsInfo, mtGoods);
                    MtGoodsSku mtGoodsSku = goodsSkuRepository.findOne(cart.getSkuId());
                    if (mtGoodsSku != null) {
                        if (StringUtils.isNotEmpty(mtGoodsSku.getLogo()) && (mtGoodsSku.getLogo().indexOf(basePath) == -1)) {
                            mtGoods.setLogo(basePath + mtGoodsSku.getLogo());
                        }
                        if (mtGoodsSku.getWeight().compareTo(new BigDecimal("0")) > 0) {
                            mtGoods.setWeight(mtGoodsSku.getWeight());
                        }
                        mtGoods.setPrice(mtGoodsSku.getPrice());
                        mtGoods.setLinePrice(mtGoodsSku.getLinePrice());
                        mtGoods.setStock(mtGoodsSku.getStock());
                    }
                    cartDto.setGoodsInfo(mtGoods);
                } else {
                    cartDto.setGoodsInfo(mtGoodsInfo);
                }

                // 计算总价
                totalPrice = totalPrice.add(cartDto.getGoodsInfo().getPrice().multiply(new BigDecimal(cart.getNum())));

                cartDtoList.add(cartDto);
            }
        }

        Map<String, Object> result = new HashMap<>();

        result.put("list", cartDtoList);
        result.put("totalNum", totalNum);
        result.put("totalPrice", totalPrice);

        return result;
    }
}

package com.fuint.application.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * UserOrderDto 实体类
 * Created by FSQ
 * Contact wx fsq_better
 */
public class UserOrderDto implements Serializable {
	/**
	 * 自增ID
	 */
	private Integer id;

	/**
	 * 订单号
	 */
	private String orderSn;

	/**
	 * 类型
	 * */
	private String type;

	/**
	 * 类型名称
	 * */
	private String typeName;

	/**
	 * 订单模式
	 * */
	private String orderMode;

	/**
	 * 卡券ID
	 */
	private Integer couponId;

	/**
	 * 用户ID
	 */
	private Integer userId;

	/**
	 * 总金额
	 * */
	private BigDecimal amount;

	/**
	 * 支付金额
	 * */
	private BigDecimal payAmount;

	/**
	 * 优惠金额
	 * */
	private BigDecimal discount;

	/**
	 * 积分金额
	 * */
	private BigDecimal pointAmount;

	/**
	 * 订单参数
	 */
	private String param;

	/**
	 * 用户备注
	 */
	private String remark;

	/**
	 * 创建时间
	 */
	private String createTime;

	/**
	 * 更新时间
	 */
	private String updateTime;

	/**
	 * 支付时间
	 */
	private String payTime;

	/**
	 * 状态
	 */
	private String status;

	/**
	 * 支付状态
	 */
	private String payStatus;

	/**
	 * 状态
	 */
	private String statusText;

	/**
	 * 最后操作人
	 */
	private String operator;

	/**
	 * 订单商品列表
	 * */
	private List<OrderGoodsDto> goods;

	/**
	 * 下单用户信息
	 * */
	private OrderUserDto userInfo;

	/**
	 * 配送地址
	 * */
	private AddressDto address;

	public Integer getId(){
		return id;
	}
	public void setId(Integer id){
		this.id=id;
	}
	public String getOrderSn(){
		return orderSn;
	}
	public void setOrderSn(String orderSn){
		this.orderSn=orderSn;
	}
	public String getType(){
		return type;
	}
	public void setType(String type){
		this.type=type;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName=typeName;
	}
	public String getOrderMode() {
		return orderMode;
	}
	public void setOrderMode(String orderMode) {
		this.orderMode=orderMode;
	}
	public Integer getCouponId(){
		return couponId;
	}
	public void setCouponId(Integer couponId){
		this.couponId=couponId;
	}
	public Integer getUserId(){
		return userId;
	}
	public void setUserId(Integer userId){
		this.userId=userId;
	}
	public BigDecimal getAmount(){
		return amount;
	}
	public void setAmount(BigDecimal amount){
		this.amount=amount;
	}
	public BigDecimal getPayAmount(){
		return payAmount;
	}
	public void setPayAmount(BigDecimal payAmount){
		this.payAmount=payAmount;
	}
	public BigDecimal getDiscount(){
		return discount;
	}
	public void setDiscount(BigDecimal discount){
		this.discount=discount;
	}
	public BigDecimal getPointAmount(){
		return pointAmount;
	}
	public void setPointAmount(BigDecimal pointAmount){
		this.pointAmount=pointAmount;
	}
	public String getParam(){
		return param;
	}
	public void setParam(String param){
		this.param=param;
	}
	public String getRemark(){
		return remark;
	}
	public void setRemark(String remark){
		this.remark=remark;
	}
	public String getCreateTime(){
		return createTime;
	}
	public void setCreateTime(String createTime){
		this.createTime=createTime;
	}
	public String getUpdateTime(){
		return updateTime;
	}
	public void setUpdateTime(String updateTime){
		this.updateTime=updateTime;
	}
	public String getStatus(){
		return status;
	}
	public void setStatus(String status){
		this.status=status;
	}
	public String getStatusText(){
		return statusText;
	}
	public void setStatusText(String statusText){
		this.statusText=statusText;
	}
	public String getPayStatus(){
		return payStatus;
	}
	public void setPayStatus(String payStatus){
		this.payStatus=payStatus;
	}
	public String getPayTime(){
		return payTime;
	}
	public void setPayTime(String payTime){
		this.payTime=payTime;
	}
	public String getOperator(){
		return operator;
	}
	public void setOperator(String operator){
		this.operator=operator;
	}
	public List<OrderGoodsDto> getGoods(){
		return goods;
	}
	public void setGoods(List<OrderGoodsDto> goods){
		this.goods=goods;
	}
	public OrderUserDto getUserInfo(){
		return userInfo;
	}
	public void setUserInfo(OrderUserDto userInfo){
		this.userInfo=userInfo;
	}
	public AddressDto getAddress(){
		return address;
	}
	public void setAddress(AddressDto address){
		this.address=address;
	}
}


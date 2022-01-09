package com.fuint.application.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class OrderDto implements Serializable {
	/**
	 * 自增ID
	 */
	private Integer id;

	/**
	 * 订单类型
	 * */
	private String type;

	/**
	 * 支付类型
	 * */
	private String payType;

	/**
	 * 订单类型名称
	 * */
	private String orderMode;

	/**
	 * 订单号
	 */
	private String orderSn;

	/**
	 * 卡券ID
	 */
	private Integer couponId;

	/**
	 * 商品ID
	 * */
	private Integer goodsId;

	/**
	 * skuID
	 * */
	private Integer skuId;

	/**
	 * 购买数量
	 * */
	private Integer buyNum;

	/**
	 * 用户ID
	 */
	private Integer userId;

	/**
	 * 店铺ID
	 * */
	private Integer storeId;

	/**
	 * 订单金额
	 */
	private BigDecimal amount;

	/**
	 * 支付金额
	 */
	private BigDecimal payAmount;

	/**
	 * 使用积分数量
	 */
	private Integer usePoint;

	/**
	 * 积分金额
	 */
	private BigDecimal pointAmount;

	/**
	 * 折扣金额
	 */
	private BigDecimal discount;

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
	private Date createTime;

	/**
	 * 更新时间
	 */
	private Date updateTime;

	/**
	 * 支付时间
	 */
	private Date payTime;

	/**
	 * 订单状态
	 */
	private String status;

	/**
	 * 支付状态
	 */
	private String payStatus;

	/**
	 * 最后操作人
	 */
	private String operator;

	public Integer getId(){
		return id;
	}
	public void setId(Integer id){
		this.id=id;
	}
	public String getType(){
		return type;
	}
	public void setType(String type){
		this.type=type;
	}
	public String getPayType(){
		return payType;
	}
	public void setPayType(String payType){
		this.payType=payType;
	}
	public String getOrderMode() {
		return orderMode;
	}
	public void setOrderMode(String orderMode) {
		this.orderMode=orderMode;
	}
	public String getOrderSn(){
		return orderSn;
	}
	public void setOrderSn(String orderSn){
		this.orderSn=orderSn;
	}
	public Integer getCouponId(){
		return couponId;
	}
	public void setCouponId(Integer couponId){
		this.couponId=couponId;
	}
	public Integer getGoodsId(){
		return goodsId;
	}
	public void setGoodsId(Integer goodsId){
		this.goodsId=goodsId;
	}
	public Integer getSkuId(){
		return skuId;
	}
	public void setSkuId(Integer skuId){
		this.skuId=skuId;
	}
	public Integer getBuyNum(){
		return buyNum;
	}
	public void setBuyNum(Integer buyNum){
		this.buyNum=buyNum;
	}
	public Integer getUserId(){
		return userId;
	}
	public void setUserId(Integer userId){
		this.userId=userId;
	}
	public Integer getStoreId(){
		return storeId;
	}
	public void setStoreId(Integer storeId){
		this.storeId=storeId;
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
	public BigDecimal getPointAmount(){
		return pointAmount;
	}
	public void setPointAmount(BigDecimal pointAmount){
		this.pointAmount=pointAmount;
	}
	public Integer getUsePoint(){
		return usePoint;
	}
	public void setUsePoint(Integer usePoint){
		this.usePoint=usePoint;
	}
	public BigDecimal getDiscount(){
		return discount;
	}
	public void setDiscount(BigDecimal discount){
		this.discount=discount;
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
	public Date getCreateTime(){
		return createTime;
	}
	public void setCreateTime(Date createTime){
		this.createTime=createTime;
	}
	public Date getUpdateTime(){
		return updateTime;
	}
	public void setUpdateTime(Date updateTime){
		this.updateTime=updateTime;
	}
	public Date getPayTime(){
		return payTime;
	}
	public void setPayTime(Date payTime){
		this.payTime=payTime;
	}
	public String getStatus(){
		return status;
	}
	public void setStatus(String status){
		this.status=status;
	}
	public String getPayStatus(){
		return payStatus;
	}
	public void setPayStatus(String payStatus){
		this.payStatus=payStatus;
	}
	public String getOperator(){
		return operator;
	}
	public void setOperator(String operator){
		this.operator=operator;
	}
}


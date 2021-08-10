package com.fuint.application.dto;

import java.io.Serializable;

/**
 * RefundDto 实体类
 * Created by FSQ
 * Contact wx fsq_better
 */
public class RefundDto implements Serializable {
	/**
	 * 自增ID
	 */
	private Integer id;

	/**
	 * 订单号
	 */
	private String orderSn;

	/**
	 * 用户ID
	 */
	private Integer userId;

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
	 * 状态
	 */
	private String status;

	/**
	 * 状态
	 */
	private String statusText;

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
	public String getOrderSn(){
		return orderSn;
	}
	public void setOrderSn(String orderSn){
		this.orderSn=orderSn;
	}
	public Integer getUserId(){
		return userId;
	}
	public void setUserId(Integer userId){
		this.userId=userId;
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
	public String getOperator(){
		return operator;
	}
	public void setOperator(String operator){
		this.operator=operator;
	}
}


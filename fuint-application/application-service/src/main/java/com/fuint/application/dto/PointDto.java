package com.fuint.application.dto;

import com.fuint.application.dao.entities.MtUser;
import java.io.Serializable;
import java.util.Date;

/**
 * PointDto 实体类
 * Created by FSQ
 * Contact wx fsq_better
 */
public class PointDto implements Serializable {
   /**
    * 自增ID 
    */
	private Integer id;

   /**
    * 用户ID 
    */
	private Integer userId;

	/**
	 * 会员信息
	 * */
	private MtUser userInfo;

	/**
	 * 订单号
	 */
	private String orderSn;

   /**
    * 积分变化数量 
    */
	private Integer amount;

   /**
    * 创建时间 
    */
	private Date createTime;

   /**
    * 更新时间 
    */
	private Date updateTime;

   /**
    * 备注说明 
    */
	private String description;

   /**
    * 状态，A正常；D作废 
    */
	private String status;

	public Integer getId(){
		return id;
	}
	public void setId(Integer id){
	this.id=id;
	}
	public Integer getUserId(){
		return userId;
	}
	public void setUserId(Integer userId){
	this.userId=userId;
	}
	public MtUser getUserInfo(){
		return userInfo;
	}
	public void setUserInfo(MtUser userInfo){
		this.userInfo=userInfo;
	}
	public String getOrderSn(){
		return orderSn;
	}
	public void setOrderSn(String orderSn){
		this.orderSn=orderSn;
	}
	public Integer getAmount(){
		return amount;
	}
	public void setAmount(Integer amount){
	this.amount=amount;
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
	public String getDescription(){
		return description;
	}
	public void setDescription(String description){
	this.description=description;
	}
	public String getStatus(){
		return status;
	}
	public void setStatus(String status){
	this.status=status;
	}
}


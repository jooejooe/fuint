package com.fuint.application.dao.entities;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.io.Serializable;
import java.util.Date;

/**
 * mt_refund 实体类
 * Created by FSQ
 * Contact wx fsq_better
 */ 
@Entity 
@Table(name = "mt_refund")
public class MtRefund implements Serializable{
   /**
    * 自增ID 
    */ 
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false, length = 11)
	private Integer id;

   /**
    * 订单ID 
    */ 
	@Column(name = "ORDER_ID", nullable = false, length = 11)
	private Integer orderId;

   /**
    * 会员ID 
    */ 
	@Column(name = "USER_ID", nullable = false, length = 11)
	private Integer userId;

   /**
    * 退款备注 
    */ 
	@Column(name = "REMARK", length = 500)
	private String remark;

   /**
    * 创建时间 
    */ 
	@Column(name = "CREATE_TIME")
	private Date createTime;

   /**
    * 更新时间 
    */ 
	@Column(name = "UPDATE_TIME")
	private Date updateTime;

   /**
    * 状态 
    */ 
	@Column(name = "STATUS", length = 1)
	private String status;

   /**
    * 最后操作人 
    */ 
	@Column(name = "OPERATOR", length = 30)
	private String operator;

	public Integer getId(){
		return id;
	}
	public void setId(Integer id){
	this.id=id;
	}
	public Integer getOrderId(){
		return orderId;
	}
	public void setOrderId(Integer orderId){
	this.orderId=orderId;
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
	public String getStatus(){
		return status;
	}
	public void setStatus(String status){
	this.status=status;
	}
	public String getOperator(){
		return operator;
	}
	public void setOperator(String operator){
	this.operator=operator;
	}
}


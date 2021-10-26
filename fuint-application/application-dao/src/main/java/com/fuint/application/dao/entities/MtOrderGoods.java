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
 * mt_order_goods 实体类
 * Created by FSQ
 * Contact wx fsq_better
 */ 
@Entity 
@Table(name = "mt_order_goods")
public class MtOrderGoods implements Serializable{
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
	@Column(name = "ORDER_ID", nullable = false, length = 10)
	private Integer orderId;

   /**
    * 商品ID 
    */ 
	@Column(name = "GOODS_ID", nullable = false, length = 10)
	private Integer goodsId;

   /**
    * 商品数量 
    */ 
	@Column(name = "NUM", nullable = false, length = 10)
	private Integer num;

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
    * A：正常；D：删除 
    */ 
	@Column(name = "STATUS", length = 1)
	private String status;

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
	public Integer getGoodsId(){
		return goodsId;
	}
	public void setGoodsId(Integer goodsId){
	this.goodsId=goodsId;
	}
	public Integer getNum(){
		return num;
	}
	public void setNum(Integer num){
	this.num=num;
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
}


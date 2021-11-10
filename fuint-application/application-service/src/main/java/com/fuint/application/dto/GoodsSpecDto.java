package com.fuint.application.dto;

import java.io.Serializable;

public class GoodsSpecDto implements Serializable {

   /**
	* 自增ID
	* */
	private Integer id;

   /**
    * 商品ID 
    */
	private Integer goodsId;

   /**
    * 规格名称 
    */
	private String name;

   /**
    * 规格值 
    */
	private String value;

	public Integer getId(){
		return id;
	}
	public void setId(Integer id){
	this.id=id;
	}
	public Integer getGoodsId(){
		return goodsId;
	}
	public void setGoodsId(Integer goodsId){
	this.goodsId=goodsId;
	}
	public String getName(){
		return name;
	}
	public void setName(String name){
	this.name=name;
	}
	public String getValue(){
		return value;
	}
	public void setValue(String value){
	this.value=value;
	}
}


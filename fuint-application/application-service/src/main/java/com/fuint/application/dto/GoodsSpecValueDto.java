package com.fuint.application.dto;

import java.io.Serializable;

public class GoodsSpecValueDto implements Serializable {

   /**
	* 自增ID
	* */
	private Integer specValueId;

   /**
    * 规格名称 
    */
	private String specValue;

	public Integer getSpecValueId(){
		return specValueId;
	}
	public void setSpecValueId(Integer specValueId){
	this.specValueId=specValueId;
	}
	public String getSpecValue(){
		return specValue;
	}
	public void setSpecValue(String specValue){
	this.specValue=specValue;
	}
}


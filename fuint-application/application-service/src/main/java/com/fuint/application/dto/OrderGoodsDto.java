package com.fuint.application.dto;

import javax.persistence.Column;
import java.io.Serializable;
import java.math.BigDecimal;

public class OrderGoodsDto implements Serializable {
	private Integer id;

	private String type;

	private String name;

	private String price;

	private String discount;

	private Integer num;

	private String image;

	public Integer getId(){
		return id;
	}
	public void setId(Integer id){
		this.id=id;
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}

	public String getDiscount(){
		return discount;
	}
	public void setDiscount(String discount){
		this.discount=discount;
	}

	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}

	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
}


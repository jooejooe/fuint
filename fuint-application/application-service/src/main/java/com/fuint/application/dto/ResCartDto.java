package com.fuint.application.dto;

import com.fuint.application.dao.entities.MtGoods;
import java.io.Serializable;

/**
 * 购物车返回DTO
 * Created by FSQ
 * Contact wx fsq_better
 */
public class ResCartDto implements Serializable {

    /**
     * ID
     * */
    private Integer id;

    /**
     * 分类名称
     */
    private Integer userId;

    /**
     * 商品ID
     */
    private Integer goodsId;

    /**
     * 数量
     */
    private Integer num;

    /**
     * 商品数据
     * */
    private MtGoods goodsInfo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer name) {
        this.userId = name;
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public MtGoods getGoodsInfo() {
        return goodsInfo;
    }

    public void setGoodsInfo(MtGoods goodsInfo) {
        this.goodsInfo = goodsInfo;
    }
}

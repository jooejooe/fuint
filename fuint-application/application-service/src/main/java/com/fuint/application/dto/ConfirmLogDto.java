package com.fuint.application.dto;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 核销卡券流水表
 * Created by FSQ
 * Contact wx fsq_better
 */
public class ConfirmLogDto {

    /**
     * 自增ID
     */
     private Integer id;

    /**
     * 核销编码
     */
    private String code;

    /**
     * 核销状态 A正常核销；D：撤销使用
     */
    private String confirmStatus;

    /**
     * 用户卡券ID
     */
    private Integer userCouponId;

    /**
     * 核销确认时间
     */
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date confirmTime;

    /**
     * 消费撤销时间
     */
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date cancelTime;

    /**
     * 可撤销消费截止时间
     */
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date endCancelTime;

    /**
     * 券ID
     */
    private Integer couponId;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 状态：A：未使用；B：已使用；C：已过期; D：已删除
     */
    private String couponInfoStatus;

    /**
     *状态说明：A：未使用；B：已使用；C：已过期; D：已删除
     */
    private String couponInfoStatusDesc;

    /**
     * 使用店铺ID
     */
    private Integer storeId;

    /**
     * 使用时间
     */
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date usedTime;

    /**
     * 用户卡券创建时间
     */
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 用户卡券更新时间
     */
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 手机号码
     */
    private String mobile;

    /**
     * 适用店铺id列表
     */
    private String suitStoreIds;

    /**
     * 真实姓名
     */
    private String name;

    /**
     * 券名称
     */
    private String couponName;

    /**
     * 面额
     */
    private BigDecimal money;

    /**
     * 分组ID
     */
    private Integer groupId;

    /**
     * A：正常；D：删除
     */
    private String couponStatus;

    /**
     * 开始有效期
     */
    private Date beginTime;

    /**
     * 结束有效期
     */
    private Date endTime;

    /**
     * 分组名称
     */
    private String couponGroupName;

    /**
     * 店铺名称
     */
    private String storeName;

    /**
     * 导入UUID
     */
    @Column(name = "UUID", nullable = false, length = 50)
    private String uuid;

    public Integer getId(){
    return id;
    }
    public void setId(Integer id){
    this.id=id;
    }
    public String getCode(){
    return code;
    }
    public void setCode(String code){
    this.code=code;
    }
    public String getConfirmStatus(){
    return confirmStatus;
    }
    public void setConfirmStatus(String confirmStatus){
    this.confirmStatus=confirmStatus;
    }
    public Integer getUserCouponId(){
    return userCouponId;
    }
    public void setUserCouponId(Integer userCouponId){
    this.userCouponId=userCouponId;
    }
    public Date getConfirmTime(){
    return confirmTime;
    }
    public void setConfirmTime(Date confirmTime){
    this.confirmTime=confirmTime;
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
    public String getCouponInfoStatus(){
     return couponInfoStatus;
    }
    public void setCouponInfoStatus(String couponInfoStatus){
    this.couponInfoStatus=couponInfoStatus;
    }
    public String getCouponInfoStatusDesc(){
     return couponInfoStatusDesc;
    }
    public void setCouponInfoStatusDesc(String couponInfoStatusDesc){
    this.couponInfoStatusDesc=couponInfoStatusDesc;
    }
    public Integer getStoreId(){
     return storeId;
    }
    public void setStoreId(Integer storeId){
    this.storeId=storeId;
    }
    public Date getUsedTime(){
     return usedTime;
    }
    public void setUsedTime(Date usedTime){
    this.usedTime=usedTime;
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
    public String getMobile(){
     return mobile;
    }
    public void setMobile(String mobile){
    this.mobile=mobile;
    }
    public String getName(){
     return name;
    }
    public void setName(String name){
    this.name=name;
    }
    public String getCouponName(){
     return couponName;
    }
    public void setCouponName(String couponName){
    this.couponName=couponName;
    }
    public BigDecimal getMoney(){
     return money;
    }
    public void setMoney(BigDecimal money){
    this.money=money;
    }
    public Integer getGroupId(){
     return groupId;
    }
    public void setGroupId(Integer groupId){
    this.groupId=groupId;
    }
    public String getCouponStatus(){
     return couponStatus;
    }
    public void setCouponStatus(String couponStatus){
    this.couponStatus=couponStatus;
    }
    public Date getBeginTime(){
     return beginTime;
    }
    public void setBeginTime(Date beginTime){
    this.beginTime=beginTime;
    }
    public Date getEndTime(){
     return endTime;
    }
    public void setEndTime(Date endTime){
    this.endTime=endTime;
    }
    public String getCouponGroupName(){
     return couponGroupName;
    }
    public void setCouponGroupName(String couponGroupName){
    this.couponGroupName=couponGroupName;
    }
    public String getStoreName(){
     return storeName;
    }
    public void setStoreName(String storeName){
    this.storeName=storeName;
    }
    public String getSuitStoreIds(){
    return suitStoreIds;
    }
    public void setSuitStoreIds(String suitStoreIds){
    this.suitStoreIds=suitStoreIds;
    }
    public String getUuid(){
    return uuid;
    }
    public void setUuid(String uuid){
    this.uuid=uuid;
    }
    public Date getCancelTime(){
    return cancelTime;
    }
    public void setCancelTime(Date cancelTime){
    this.cancelTime=cancelTime;
    }
    public Date getEndCancelTime(){
    return endCancelTime;
    }
    public void setEndCancelTime(Date endCancelTime){
        this.endCancelTime=endCancelTime;
    }
}


package com.fuint.application.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.fuint.application.dao.entities.MtOrder;

import java.math.BigDecimal;
import java.util.Date;

/**
 * mt_order Repository
 * Created by FSQ
 * Contact wx fsq_better
 */
@Repository 
public interface MtOrderRepository extends BaseRepository<MtOrder, Integer> {
   /**
    * 获取订单总数
    *
    * @return
    */
   @Query("SELECT count(t.id) as num FROM MtOrder t")
   Long getOrderCount();

   /**
    * 根据订单号查询订单
    *
    * @return
    * */
   @Query("select t from MtOrder t where t.orderSn = :orderSn")
   MtOrder findByOrderSn(@Param("orderSn") String orderSn);

   /**
    * 获取订单支付总金额
    *
    * @return
    */
   @Query("SELECT sum(t.amount) as num FROM MtOrder t where t.payStatus='B' and t.payTime <= :endTime and t.payTime >= :beginTime")
   BigDecimal getPayMoney( @Param("beginTime") Date beginTime, @Param("endTime") Date endTime);
}


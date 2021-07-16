package com.fuint.application.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.fuint.application.dao.entities.MtOrder;

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
}


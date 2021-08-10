package com.fuint.application.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.fuint.application.dao.entities.MtRefund;

/**
 * mt_refund Repository
 * Created by FSQ
 * Contact wx fsq_better
 */
@Repository 
public interface MtRefundRepository extends BaseRepository<MtRefund, Integer> {
   /**
    * 获取售后订单总数
    *
    * @return
    */
   @Query("SELECT count(t.id) as num FROM MtRefund t")
   Long getRefundCount();
}


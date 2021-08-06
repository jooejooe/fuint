package com.fuint.application.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.stereotype.Repository;
import com.fuint.application.dao.entities.MtRefund;

   /**
    * mt_refund Repository
    * Created by FSQ
    * Fri Aug 06 08:33:07 GMT+08:00 2021
    */ 
@Repository 
public interface MtRefundRepository extends BaseRepository<MtRefund, Integer> {
}


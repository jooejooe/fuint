package com.fuint.application.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.stereotype.Repository;
import com.fuint.application.dao.entities.MtOrderGoods;

   /**
    * mt_order_goods Repository
    * Created by FSQ
    * Sun Oct 17 17:06:30 GMT+08:00 2021
    */ 
@Repository 
public interface MtOrderGoodsRepository extends BaseRepository<MtOrderGoods, Integer> {
}


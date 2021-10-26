package com.fuint.application.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.stereotype.Repository;
import com.fuint.application.dao.entities.MtGoods;

   /**
    * mt_goods Repository
    * Created by FSQ
    * Wed Oct 13 22:09:44 GMT+08:00 2021
    */ 
@Repository 
public interface MtGoodsRepository extends BaseRepository<MtGoods, Integer> {
}


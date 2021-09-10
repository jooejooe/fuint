package com.fuint.application.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.stereotype.Repository;
import com.fuint.application.dao.entities.MtOpenGiftItem;

   /**
    * mt_open_gift_item Repository
    * Created by FSQ
    * Wed Sep 08 17:23:32 GMT+08:00 2021
    */ 
@Repository 
public interface MtOpenGiftItemRepository extends BaseRepository<MtOpenGiftItem, Integer> {
}


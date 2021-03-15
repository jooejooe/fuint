package com.fuint.application.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.stereotype.Repository;
import com.fuint.application.dao.entities.MtPoint;

   /**
    * mt_point Repository
    * Created by zach
    * Mon Mar 15 16:31:03 GMT+08:00 2021
    */ 
@Repository 
public interface MtPointRepository extends BaseRepository<MtPoint, Integer> {
}


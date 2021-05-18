package com.fuint.application.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.stereotype.Repository;
import com.fuint.application.dao.entities.MtSetting;

   /**
    * mt_setting Repository
    * Created by zach
    * Tue May 18 22:32:02 GMT+08:00 2021
    */ 
@Repository 
public interface MtSettingRepository extends BaseRepository<MtSetting, Integer> {
}


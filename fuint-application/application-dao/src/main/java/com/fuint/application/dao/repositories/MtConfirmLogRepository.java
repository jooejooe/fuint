package com.fuint.application.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.fuint.application.dao.entities.MtConfirmLog;

/**
 * Created by FSQ
 * Contact wx fsq_better
 */
@Repository 
public interface MtConfirmLogRepository extends BaseRepository<MtConfirmLog, Integer> {
   /**
    * 获取订单总数
    *
    * @return
    */
   @Query("SELECT count(t.id) as num FROM MtConfirmLog t")
   Long getConfirmLogCount();
}


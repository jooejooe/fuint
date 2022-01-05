package com.fuint.application.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.fuint.application.dao.entities.MtConfirmLog;
import java.util.Date;

/**
 * Created by FSQ
 * Contact wx fsq_better
 */
@Repository 
public interface MtConfirmLogRepository extends BaseRepository<MtConfirmLog, Integer> {
   /**
    * 获取核销总数
    *
    * @return
    */
   @Query("SELECT count(t.id) as num FROM MtConfirmLog t where t.createTime <= :endTime and t.createTime >= :beginTime")
   Long getConfirmLogCount(@Param("beginTime") Date beginTime, @Param("endTime") Date endTime);
}


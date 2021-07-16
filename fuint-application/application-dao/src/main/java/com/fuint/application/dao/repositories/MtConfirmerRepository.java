package com.fuint.application.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.fuint.application.dao.entities.MtConfirmer;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;

/**
 * mt_confirmer_personel Repository
 * Created by FSQ
 * Contact wx fsq_better
 */
@Repository 
public interface MtConfirmerRepository extends BaseRepository<MtConfirmer, Integer> {

      /**
       * 根据更新状态
       *
       * @return
       */
      @Transactional
      @Modifying
      @Query(value = "update MtConfirmer p set p.auditedStatus =:statusenum,p.updateTime=:currentDT where p.id in (:ids)")
      int updateStatus(@Param("ids") List<Integer> ids, @Param("statusenum") String statusenum,@Param("currentDT") Date currentDT );

      /**
       * 根据手机号查找核销人员
       *
       * @return
       */
      @Query("select t from MtConfirmer t where t.mobile = :mobile")
      MtConfirmer queryConfirmerByMobile(@Param("mobile") String mobile);

      /**
       * 根据用户ID查找核销人员
       *
       * @return
       */
      @Query("select t from MtConfirmer t where t.userId = :userId")
      MtConfirmer queryConfirmerByUserId(@Param("userId") Integer userId);
}


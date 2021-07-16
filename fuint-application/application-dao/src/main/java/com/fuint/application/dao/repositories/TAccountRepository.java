package com.fuint.application.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import com.fuint.application.dao.entities.MtCoupon;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.fuint.application.dao.entities.TAccount;

import java.util.List;

/**
 * t_account Repository
 * Created by FSQ
 * Contact wx fsq_better
 */
@Repository 
public interface TAccountRepository extends BaseRepository<TAccount, Integer> {

   /**
    * 根据店铺id获取列表
    *
    * @param storeId
    * @return
    */
   @Query("SELECT t FROM TAccount t WHERE storeId=:storeId AND t.accountStatus='1'")
   List<TAccount> queryTAccountByStoreId(@Param("storeId") Integer storeId);
}


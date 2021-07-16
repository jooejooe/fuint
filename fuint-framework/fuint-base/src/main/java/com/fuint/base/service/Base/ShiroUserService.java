package com.fuint.base.service.Base;

import org.apache.shiro.authc.SimpleAuthenticationInfo;

/**
 * shiro user 接口
 *
 * Created by FSQ
 * Contact wx fsq_better
 */
public interface ShiroUserService {

    /**
     * 根据登录账户名称创建shiro user
     *
     * @param accountName
     * @return
     */
    public SimpleAuthenticationInfo createShiroUserByAccountName(String accountName);
}

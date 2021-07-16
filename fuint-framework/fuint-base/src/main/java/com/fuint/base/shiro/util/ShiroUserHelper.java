package com.fuint.base.shiro.util;

import com.fuint.base.shiro.ShiroUser;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

/**
 * 会话工具类
 * <p/>
 * Created by FSQ
 * Contact wx fsq_better
 */
public class ShiroUserHelper {


    /**
     * 获取当前用户
     *
     * @return
     */
    public static ShiroUser getCurrentShiroUser() {
        Subject subject = getSubject();
        ShiroUser shiroUser = (ShiroUser) subject.getPrincipal();
        return shiroUser;
    }

    public static void cleanUser(){
        Subject subject = getSubject();
        if(subject.isAuthenticated()){
            subject.logout();
        }
    }

    private static Subject getSubject() {
        return SecurityUtils.getSubject();
    }
}

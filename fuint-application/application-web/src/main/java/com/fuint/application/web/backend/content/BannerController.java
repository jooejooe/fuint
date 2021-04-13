package com.fuint.application.web.backend.content;

import com.fuint.application.dto.BannerDto;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.base.shiro.util.ShiroUserHelper;
import com.fuint.base.util.RequestHandler;
import com.fuint.exception.BusinessCheckException;
import com.fuint.exception.BusinessRuntimeException;
import com.fuint.util.StringUtil;
import com.fuint.application.dao.entities.*;
import com.fuint.application.dto.ReqResult;
import com.fuint.application.service.banner.BannerService;
import com.fuint.application.util.CommonUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Banner信息管理类controller
 * Created by zach on 2021-04-12
 */
@Controller
@RequestMapping(value = "/backend/banner")
public class BannerController {

    /**
     * Banner服务接口
     */
    @Autowired
    private BannerService bannerService;

    /**
     * banner信息列表查询
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     * @return banner信息列表展现页面
     */
    @RequiresPermissions("backend/banner/queryList")
    @RequestMapping(value = "/queryList")
    public String queryList(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);
        String bannerId = request.getParameter("EQ_id");
        String title = request.getParameter("LIKE_title");
        String status = request.getParameter("EQ_status");
        Map<String, Object> params = paginationRequest.getSearchParams();
        if (params == null) {
            params = new HashMap<>();
            if (StringUtils.isNotEmpty(bannerId)) {
                params.put("EQ_id", bannerId);
            }

            if (StringUtils.isNotEmpty(title)) {
                params.put("LIKE_title", title);
            }

            if (StringUtils.isNotEmpty(status)) {
                params.put("EQ_status", status);
            }
        }

        paginationRequest.setSearchParams(params);
        PaginationResponse<MtBanner> paginationResponse = bannerService.queryBannerListByPagination(paginationRequest);

        model.addAttribute("paginationResponse", paginationResponse);
        model.addAttribute("params", params);

        return "banner/list";
    }

    /**
     * 激活banner
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("backend/banner/active/{id}")
    @RequestMapping(value = "/active/{id}")
    public String activeStore(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable("id") Integer id) throws BusinessCheckException {
        MtBanner info = bannerService.queryBannerById(id);
        BannerDto dto = new BannerDto();

        bannerService.updateBanner(dto);
        ReqResult reqResult = new ReqResult();

        reqResult.setResult(true);

        return "redirect:/backend/banner/queryList";
    }

    /**
     * 删除banner
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("backend/banner/delete/{id}")
    @RequestMapping(value = "/delete/{id}")
    public String delete(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable("id") Integer id) throws BusinessCheckException {
        String operator;
        try {
            operator = ShiroUserHelper.getCurrentShiroUser().getAcctName();
        } catch (Exception e) {
            operator = "sysadmin";
        }

        bannerService.deleteBanner(id, operator);
        ReqResult reqResult = new ReqResult();

        reqResult.setResult(true);

        return "redirect:/backend/banner/queryList";
    }

    /**
     * 添加初始化页面
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("backend/banner/add")
    @RequestMapping(value = "/add")
    public String add(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        return "banner/add";
    }

    /**
     * 新增页面
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     */
    @RequiresPermissions("backend/banner/create")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String addHandler(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        BannerDto info = new BannerDto();

        String id = request.getParameter("id");
        String title = CommonUtil.replaceXSS(request.getParameter("title"));
        String description = CommonUtil.replaceXSS(request.getParameter("description"));

        info.setTitle(title);
        info.setDescription(description);

        try {
            if (StringUtil.isNotEmpty(id)) {
                info.setId(Integer.parseInt(id));
            }
        } catch (Exception e) {
            throw new BusinessRuntimeException("整型转化异常" + e.getMessage());
        }

        bannerService.addBanner(info);
        return "redirect:/backend/banner/queryList";
    }

    /**
     * 编辑初始化页面
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("backend/banner/editInit/{id}")
    @RequestMapping(value = "/editInit/{id}")
    public String editInit(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable("id") Integer id) throws BusinessCheckException {
        MtBanner info = bannerService.queryBannerById(id);
        model.addAttribute("info", info);

        return "editInit/edit";
    }
}

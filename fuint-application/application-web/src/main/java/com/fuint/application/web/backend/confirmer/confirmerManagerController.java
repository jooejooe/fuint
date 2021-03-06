package com.fuint.application.web.backend.confirmer;

import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.base.util.RequestHandler;
import com.fuint.exception.BusinessCheckException;
import com.fuint.exception.BusinessRuntimeException;
import com.fuint.application.dao.entities.MtStore;
import com.fuint.application.dao.entities.MtConfirmer;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.service.store.StoreService;
import com.fuint.application.service.confirmer.ConfirmerService;
import com.fuint.application.util.CommonUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 员工管理
 * Created by FSQ
 * Contact wx fsq_better
 */
@Controller
@RequestMapping(value = "/backend/confirmer")
public class confirmerManagerController {
    private static final Logger logger = LoggerFactory.getLogger(confirmerManagerController.class);

    /**
     * 核销人员
     */
    @Autowired
    private ConfirmerService confirmerService;

    /**
     * 店铺信息管理服务接口
     */
    @Autowired
    private StoreService storeService;

    /**
     * 员工列表查询
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     * @return 员工列表展现页面
     */
    @RequiresPermissions("backend/confirmer/queryList")
    @RequestMapping(value = "/queryList")
    public String queryList(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);
        String mobile = request.getParameter("LIKE_mobile");
        String auditedStatus = request.getParameter("EQ_auditedStatus");
        String storeId = request.getParameter("EQ_storeId");

        Map<String, Object> params = paginationRequest.getSearchParams();
        Map<String, Object> paramsStore = new HashMap<>();
        if (StringUtils.isNotEmpty(mobile)) {
            paramsStore.put("EQ_mobile", mobile);
        }
        if (StringUtils.isNotEmpty(auditedStatus)) {
            params.put("EQ_auditedStatus", auditedStatus);
        }
        if (StringUtils.isNotEmpty(storeId)) {
            paramsStore.put("EQ_storeId", storeId);
        }

        paginationRequest.setSearchParams(params);
        PaginationResponse<MtConfirmer> paginationResponse = confirmerService.queryConfirmerListByPagination(paginationRequest);
        for (MtConfirmer m : paginationResponse.getContent()) {
            MtStore mtStore = storeService.queryStoreById(m.getStoreId());
            if (mtStore != null) {
                m.setStoreName(mtStore.getName());
            }
        }

        List<MtStore> storeList = storeService.queryStoresByParams(paramsStore);
        model.addAttribute("paginationResponse", paginationResponse);
        model.addAttribute("params", params);
        model.addAttribute("storeList", storeList);

        return "confirmer/confirmer_list";
    }

    /**
     * 审核通过
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("backend/confirmer/audited/{id}")
    @RequestMapping(value = "/audited/{id}")
    public String audited(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable("id") Long id) throws BusinessCheckException {
        List<Integer> ids = new ArrayList<Integer>();
        ids.add(id.intValue());

        confirmerService.updateAuditedStatus(ids, StatusEnum.ENABLED.getKey());

        return "redirect:/backend/confirmer/queryList";
    }

    /**
     * 审核 不 通过， 无效删除状态
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("backend/confirmer/unaudited/{id}")
    @RequestMapping(value = "/unaudited/{id}")
    public String unaudited(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable("id") Long id) throws BusinessCheckException {
        List<Integer> ids = new ArrayList<Integer>();
        ids.add(id.intValue());

        confirmerService.updateAuditedStatus(ids, StatusEnum.DISABLE.getKey());

        return "redirect:/backend/confirmer/queryList";
    }

    /**
     * 编辑初始化页面
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("backend/confirmer/add")
    @RequestMapping(value = "/add")
    public String addInit(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        Map<String, Object> params_store = new HashMap<>();
        params_store.put("EQ_status", StatusEnum.ENABLED.getKey());
        List<MtStore> storeList = storeService.queryStoresByParams(params_store);

        model.addAttribute("storeList", storeList);

        return "confirmer/confirmer_edit";
    }

    /**
     * 编辑初始化页面
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("backend/confirmer/confirmerEditInit/{id}")
    @RequestMapping(value = "/confirmerEditInit/{id}")
    public String confirmerEditInit(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable("id") Integer id) throws BusinessCheckException {
        MtConfirmer mtConfirmer;
        mtConfirmer = confirmerService.queryConfirmerById(id);

        Map<String, Object> params_store = new HashMap<>();
        params_store.put("EQ_status", StatusEnum.ENABLED.getKey());
        List<MtStore> storeList = storeService.queryStoresByParams(params_store);

        model.addAttribute("confirmer", mtConfirmer);
        model.addAttribute("storeList", storeList);

        return "confirmer/confirmer_edit";
    }

    /**
     * 审核通过
     *
     * @param request
     * @return
     */
    @RequiresPermissions("backend/confirmer/doEdit")
    @RequestMapping(value = "/doEdit")
    public String doEdit(HttpServletRequest request) throws BusinessCheckException {
        String id_str = request.getParameter("id");
        Integer id = 0;
        if (StringUtils.isNotEmpty(id_str)) {
            id = Integer.parseInt(id_str);
        }

        String storeId_str = request.getParameter("storeId");
        Integer storeId = 0;
        if (StringUtils.isNotEmpty(storeId_str)) {
            storeId = Integer.parseInt(storeId_str);
        }

        String mobile = CommonUtil.replaceXSS(request.getParameter("mobile"));
        String realName = CommonUtil.replaceXSS(request.getParameter("realName"));

        MtConfirmer mtConfirmer = confirmerService.queryConfirmerById(id);
        if (mtConfirmer == null && id > 0) {
            throw new BusinessRuntimeException("记录不存在!");
        }

        if (null == mtConfirmer) {
            mtConfirmer = new MtConfirmer();
            mtConfirmer.setAuditedStatus(StatusEnum.UnAudited.getKey());
        }

        mtConfirmer.setStoreId(storeId);
        mtConfirmer.setRealName(realName);
        mtConfirmer.setMobile(mobile);

        if (StringUtils.isEmpty(mtConfirmer.getMobile())) {
            throw new BusinessRuntimeException("手机号码不能为空");
        } else {
            MtConfirmer tempUser = confirmerService.queryConfirmerByMobile(mtConfirmer.getMobile());
            if (null != tempUser && tempUser.getId() != mtConfirmer.getId()) {
                throw new BusinessCheckException("该会员手机号码已经存在!");
            }
        }

        confirmerService.addConfirmer(mtConfirmer);

        return "redirect:/backend/confirmer/queryList";
    }
}

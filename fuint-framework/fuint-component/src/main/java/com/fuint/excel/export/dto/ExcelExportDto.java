package com.fuint.excel.export.dto;

import java.io.OutputStream;
import java.util.Map;

/**
 * 导出Excel文件DTO
 * Created by liuguofang on 2016/11/25.
 */
public class ExcelExportDto {
    /**
     * 模板文件名称
     */
    private String srcTemplateFileName;

    /**
     * 模板文件所在resource路径
     */
    private String srcPath;
    /**
     * 数据
     */
    private Map dataMap;
    /**
     * 输出流
     */
    private OutputStream out;

    public String getSrcTemplateFileName() {
        return srcTemplateFileName;
    }

    public void setSrcTemplateFileName(String srcTemplateFileName) {
        this.srcTemplateFileName = srcTemplateFileName;
    }

    public Map getDataMap() {
        return dataMap;
    }

    public void setDataMap(Map dataMap) {
        this.dataMap = dataMap;
    }

    public OutputStream getOut() {
        return out;
    }

    public void setOut(OutputStream out) {
        this.out = out;
    }

    public String getSrcPath() {
        return srcPath;
    }

    public void setSrcPath(String srcPath) {
        this.srcPath = srcPath;
    }
}

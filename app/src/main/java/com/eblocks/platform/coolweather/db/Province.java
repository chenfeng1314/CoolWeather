package com.eblocks.platform.coolweather.db;

import org.litepal.crud.LitePalSupport;

/**
 * author : chenhongsheng
 * e-mail : chenhongshengadam@dingtalk.com
 * date   : 2018/12/4  11:22
 * desc   :
 * version: 1.0
 */
public class Province extends LitePalSupport {

    private int id;
    private String provinceName;
    private int provinceCode;

    public Province(int id, String provinceName, int provinceCode) {
        this.id = id;
        this.provinceName = provinceName;
        this.provinceCode = provinceCode;
    }

    public Province() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}

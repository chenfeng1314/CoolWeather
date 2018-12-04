package com.eblocks.platform.coolweather.ui.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.eblocks.platform.coolweather.R;
import com.eblocks.platform.coolweather.db.City;
import com.eblocks.platform.coolweather.db.County;
import com.eblocks.platform.coolweather.db.Province;
import com.eblocks.platform.coolweather.util.HttpUtil;
import com.eblocks.platform.coolweather.util.Utility;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * author : chenhongsheng
 * e-mail : chenhongshengadam@dingtalk.com
 * date   : 2018/12/4  13:26
 * desc   :
 * version: 1.0
 */
public class ChooseAreaFragment extends Fragment implements View.OnClickListener {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    public static final String PROVINCE = "province";
    public static final String CITY = "city";
    public static final String COUNTY = "county";
    private ProgressDialog progressDialog;
    private TextView mTextTitle;
    private Button mButtonBack;
    private ListView mViewList;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    private static final String TAG = "ChooseAreaFragment";
    /**
     * 省列表
     */
    private List<Province> provinceList = new ArrayList<>();
    /**
     * 市列表
     */
    private List<City> cityList;
    /**
     * 县列表
     */
    private List<County> countyList;
    /**
     * 选中的省份
     */
    private Province selectedProvince;
    /**
     * 选中的市
     */
    private City selectedCity;
    /**
     * 当前选中的级别
     */
    private int currentLevel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        initView(view);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        mViewList.setAdapter(adapter);
        return view;
    }

    private void initView(@NonNull final View itemView) {
        mTextTitle = (TextView) itemView.findViewById(R.id.title_text);
        mButtonBack = (Button) itemView.findViewById(R.id.back_button);
        mButtonBack.setOnClickListener(this);
        mViewList = (ListView) itemView.findViewById(R.id.list_view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCounties();
                }
            }
        });
        queryProvinces();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_button:
                // TODO 18/12/04
                if (currentLevel == LEVEL_COUNTY) {
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    queryProvinces();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 查询选中的市内的所有县级数据，优先去数据库查询，数据库没有再去查询网络
     */
    private void queryCounties() {
        mTextTitle.setText(selectedCity.getCityName());
        mButtonBack.setVisibility(View.VISIBLE);
        countyList = LitePal.where("cityId = ?", String.valueOf(selectedCity.getCityCode())).find(County.class);
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            mViewList.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            queryFromServer(address, COUNTY);
        }

    }

    /**
     * 查询选中的省内的所有市级，优先查询数据库，数据库没有再去查询网络
     */
    private void queryCities() {
        mTextTitle.setText(selectedProvince.getProvinceName());
        mButtonBack.setVisibility(View.VISIBLE);
        cityList = LitePal.where("provinceId =  ?", String.valueOf(selectedProvince.getId())).find(City.class);
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            mViewList.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(address, CITY);
        }
    }

    /**
     * 查询全国所有的省，优先查询数据库，数据库没有再去查询网络
     */
    private void queryProvinces() {
        mTextTitle.setText("中国");
        mButtonBack.setVisibility(View.GONE);
        provinceList = LitePal.findAll(Province.class);
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            mViewList.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else {
            String address = "http://guolin.tech/api/china";
            queryFromServer(address, PROVINCE);
        }
    }

    /**
     * 根据传入的地址和类型去服务器上查询省市县的数据
     *
     * @param address 地址
     * @param type    类型
     */
    private void queryFromServer(String address, final String type) {
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getActivity(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if (PROVINCE.equals(type)) {
                    result = Utility.handleProvinceResponce(responseText);
                } else if (CITY.equals(type)) {
                    result = Utility.handleCityResponce(responseText, selectedProvince.getId());
                } else if (COUNTY.equals(type)) {
                    result = Utility.handleCountyResponse(responseText, selectedCity.getId());
                }
                if (result) {
                    closeProgressDialog();
                    if (PROVINCE.equalsIgnoreCase(type)) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                queryProvinces();
                            }
                        });
                    } else if (CITY.equalsIgnoreCase(type)) {
                        queryCities();
                    } else if (COUNTY.equalsIgnoreCase(type)) {
                        queryCounties();
                    }
                }
            }
        });
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
}

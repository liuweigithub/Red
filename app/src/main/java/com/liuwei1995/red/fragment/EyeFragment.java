package com.liuwei1995.red.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.PhoneUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.liuwei1995.red.BaseApplication;
import com.liuwei1995.red.R;
import com.liuwei1995.red.adapter.RedAdapter;
import com.liuwei1995.red.adapter.RedViewHolder;
import com.liuwei1995.red.db.impl.OFOEntityEntityDaoImpl;
import com.liuwei1995.red.entity.OFOEntity;
import com.liuwei1995.red.http.HttpCallback;
import com.liuwei1995.red.http.HttpUtils;
import com.liuwei1995.red.service.OFOEntitySaveIntentService;
import com.liuwei1995.red.util.UserJSON;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dell on 2017/5/5
 */

public class EyeFragment extends BaseFragment implements TextWatcher{

    private static final String TAG = EyeFragment.class.getSimpleName();

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String TYPE = "type";

    public EyeFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static EyeFragment newInstance(int type,boolean isPrepared) {
        EyeFragment fragment = new EyeFragment();
        Bundle args = new Bundle();
        args.putInt(TYPE, type);
        args.putBoolean("isPrepared",isPrepared);
        fragment.setArguments(args);
        return fragment;
    }
    int type = 0;
    private Context mContext;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        Bundle arguments = getArguments();
        if(arguments != null){
           type = arguments.getInt(TYPE, 0);
        }
    }
    private RecyclerView rv_content;
    private AutoCompleteTextView actv_license_plate_number;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_eye, container, false);
         rv_content = $(rootView,R.id.rv_frg_eye_content);
        actv_license_plate_number = $(rootView, R.id.actv_license_plate_number);
        $(rootView,R.id.btn_Search).setOnClickListener(this);
        actv_license_plate_number.addTextChangedListener(this);
         rv_content.setLayoutManager(new LinearLayoutManager(mContext));
        if (!isPrepared){
            initData();
        }
//        页码 页数 角标  pageNumber pageIndex  搜索
        return rootView;
    }
    private int pageNumber = 30;
    private int pageIndex = 1;

    private OFOEntityEntityDaoImpl impl;
    public void getNativeData(String acc){
        if(impl == null){
            synchronized (this){
                if(impl == null){
                    impl = new OFOEntityEntityDaoImpl(mContext);
                }
            }
        }
        synchronized (this){
            List<OFOEntity> ofoEntities = null;
            if(acc == null || TextUtils.isEmpty(acc)){
                ofoEntities = impl.rawQuery("SELECT *FROM "+OFOEntity.class.getSimpleName()+" ORDER BY account");
            }else {
                ofoEntities = impl.rawQuery("SELECT *FROM "+OFOEntity.class.getSimpleName()+" WHERE account LIKE '%"+acc+"%' LIMIT 0,30");
            }
            if(ofoEntities != null){
                if (pageIndex == 1){
                    list.clear();
                }
                for (int i = 0; i < ofoEntities.size(); i++) {
                    list.add(ofoEntities.get(i));
                }
                setAdapter();
            }
        }
    }
    @Override
    protected void initData() {
        if (type == 0){
            getNativeData("");
        }else if(type == 1){
            Map<String, Object> map = new HashMap<>();
            map.put("pageNumber",pageNumber);
            map.put("pageIndex",pageIndex);
            HttpUtils.ofoGetAccountPassword(map, new HttpCallback<JSONObject>() {
                @Override
                public void onResponse(Boolean isSuccess, JSONObject result) {
                    if (isSuccess){
                        int code_time = UserJSON.getInt(result, "code_time");
                        if (code_time == 1){
                            int code_success = UserJSON.getInt(result, "code_success");
                            if(code_success == 1){
                                JSONArray content = UserJSON.getJSONArray(result, "content");
                                List<OFOEntity> ofoEntities = UserJSON.parsUser(OFOEntity.class, content);
                                if (pageIndex == 1){
                                    list.clear();
                                }
                                if(ofoEntities != null){
                                    for (int i = 0; i < ofoEntities.size(); i++) {
                                        list.add(ofoEntities.get(i));
                                    }
                                    list_quanbu.clear();
                                    list_quanbu.addAll(list);
                                }
                                setAdapter();
                            }else {
                                Toast.makeText(mContext, UserJSON.getString(result,"message"), Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(mContext, UserJSON.getString(result,"message"), Toast.LENGTH_SHORT).show();
                        }
                    }else Toast.makeText(mContext, "网络开小差啦", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onInvisible() {

    }

    private List<OFOEntity> list = new ArrayList<>();
    private List<OFOEntity> list_quanbu = new ArrayList<>();
    private RedAdapter<OFOEntity> adapter;
    public void setAdapter(){
        if(adapter == null){
            adapter = new RedAdapter<OFOEntity>(list,R.layout.item_fragment_eye) {
                @Override
                public void convert(RedViewHolder holder, final OFOEntity item, int position) {
                    holder.setText(R.id.tv_account,item.getAccount());
                    holder.setText(R.id.tv_accountPassword,item.getAccountPassword());
                    Button btn_submit = holder.getView(R.id.btn_submit);
                    btn_submit.setVisibility(View.VISIBLE);
                    if(type == 0){
                        Integer submitState = item.getSubmitState();
                        if(submitState == 1){
                            btn_submit.setVisibility(View.GONE);
                        }else {
                            btn_submit.setText("提交");
                            btn_submit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    submit(item);
                                }
                            });
                        }
                    }else if (type == 1){
                        Integer submitState = item.getSubmitState();
                        if(submitState == 1){
                            btn_submit.setVisibility(View.GONE);
                        }else {
                            btn_submit.setVisibility(View.VISIBLE);
                            btn_submit.setText("保存");
                            btn_submit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    save(item);
                                }
                            });
                        }
                    }
                }
            };
            rv_content.setAdapter(adapter);
        }else {
            adapter.notifyDataSetChanged();
//            adapter.notifyItemRangeChanged(0,list.size());
        }
    }

    private void save(final OFOEntity item) {
        if(item == null)return;
        synchronized (this){
            OFOEntityEntityDaoImpl impl = new OFOEntityEntityDaoImpl(mContext);
            List<OFOEntity> ofoEntities = impl.rawQuery("SELECT *FROM "+OFOEntity.class.getSimpleName()+" WHERE account = '" + item.getAccount()+"'");
            if(ofoEntities != null && !ofoEntities.isEmpty()){
                item.setSubmitState(1);
                Toast.makeText(mContext, "保存成功", Toast.LENGTH_SHORT).show();
            }else {
                item.setSubmitState(1);
                long insert = impl.insert(item);
                if(insert > 0){
                    OFOEntitySaveIntentService.startActionFoo(mContext,item);
                    Toast.makeText(mContext, "保存成功", Toast.LENGTH_SHORT).show();
                }
            }
            setAdapter();
        }
    }
    public void submit(final OFOEntity item){

        Map<String, Object> map = new HashMap<>();
        String account = item.getAccount();
        map.put("account",account);

//        String phoneNumber = item.getPhoneNumber();
        map.put("phoneNumber", PhoneUtils.getPhoneNumber(mContext));

        String androidID = DeviceUtils.getAndroidID();
        item.setAndroidID(androidID);
        map.put("androidID",item.getAndroidID());

        item.setIMEI(PhoneUtils.getIMEI());
        map.put("IMEI",item.getIMEI());

        String accountPassword = item.getAccountPassword();
        map.put("accountPassword",accountPassword);

        int androidSDK = item.getAndroidSDK();
        map.put("androidSDK",androidSDK);

//        String versionName = item.getVersionName();
        map.put("versionName", BaseApplication.versionName);

//        int versionCode = item.getVersionCode();
        map.put("versionCode",BaseApplication.versionCode);

        String androidVersion = item.getAndroidVersion();
        map.put("androidVersion",androidVersion);

        String buildManufacturer = item.getBuildManufacturer();
        map.put("buildManufacturer",buildManufacturer);

        String buildModel = item.getBuildModel();
        map.put("buildModel",buildModel);
        map.put("deviceType","Android");

        HttpUtils.saveAccountPassword(map, new HttpCallback<JSONObject>() {
            @Override
            public void onResponse(Boolean isSuccess, JSONObject result) {
                if (isSuccess){
                    int code_time = UserJSON.getInt(result, "code_time");
                    if (code_time == 1) {
                        int code_success = UserJSON.getInt(result, "code_success");
                        if (code_success == 1) {
                            if (item.getSubmitState() != 1){
                                OFOEntityEntityDaoImpl impl = new OFOEntityEntityDaoImpl(mContext);
                                item.setSubmitState(1);
                                impl.update(item);
                            }
                            setAdapter();
                            Toast.makeText(mContext, "提交成功", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(mContext, UserJSON.getString(result,"message"), Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(mContext, UserJSON.getString(result,"message"), Toast.LENGTH_SHORT).show();
                    }
                }else ToastUtils.showShortToast("网络开小差啦");
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_Search){
            if(type == 1){
                Map<String, Object> map = new HashMap<>();

                String trim = actv_license_plate_number.getText().toString().trim();
                if(TextUtils.isEmpty(trim))return;
                map.put("account",trim);
                map.put("pageNumber",pageNumber);
                map.put("pageIndex",pageIndex);
                HttpUtils.ofoSearchAccountPassword(map, new HttpCallback<JSONObject>() {
                    @Override
                    public void onResponse(Boolean isSuccess, JSONObject result) {
                        if (isSuccess){
                            int code_time = UserJSON.getInt(result, "code_time");
                            if (code_time == 1){
                                int code_success = UserJSON.getInt(result, "code_success");
                                if(code_success == 1){
                                    JSONArray content = UserJSON.getJSONArray(result, "content");
                                    List<OFOEntity> ofoEntities = UserJSON.parsUser(OFOEntity.class, content);
                                    if (pageIndex == 1){
                                        list.clear();
                                    }
                                    if(ofoEntities != null){
                                        for (int i = 0; i < ofoEntities.size(); i++) {
                                            list.add(ofoEntities.get(i));
                                        }
                                    }
                                    setAdapter();
                                }else {
                                    Toast.makeText(mContext, UserJSON.getString(result,"message"), Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                Toast.makeText(mContext, UserJSON.getString(result,"message"), Toast.LENGTH_SHORT).show();
                            }
                        }else Toast.makeText(mContext, "网络开小差啦", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if(type == 0){
            getNativeData(s.toString());
        }else if (type == 1){
            list.clear();
            for (int i = 0; i < list_quanbu.size(); i++) {
                list.add(list_quanbu.get(i));
            }
            setAdapter();
        }
    }
}

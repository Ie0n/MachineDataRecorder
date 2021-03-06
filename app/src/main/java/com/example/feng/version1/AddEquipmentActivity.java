package com.example.feng.version1;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.feng.version1.Public.PublicData;
import com.example.feng.version1.Util.SoftHideKeyBoardUtil;
import com.example.feng.version1.Util.ToastUtil;
import com.example.feng.version1.adapter.EquipmentAdapter;
import com.example.feng.version1.bean.Device;
import com.example.feng.version1.bean.DeviceCreateResponse;
import com.example.feng.version1.bean.Equipment;
import com.example.feng.version1.bean.StatusResponse;
import com.example.feng.version1.bean.User;
import com.example.feng.version1.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddEquipmentActivity extends AppCompatActivity implements View.OnClickListener, Callback {

    private RecyclerView recyclerView;
    private Context mContext;
    private FloatingActionButton add,confirm;
    private List<Equipment> equipmentList;
    private EquipmentAdapter adapter;
    private LinearLayoutManager layoutManager;
    private int count = 0;
    private EditText driverName;
    private String deviceNo,site,task;
    private Device device = new Device();
    private int isRight = 0;
    private String [] tabs= {
            "仪表一","仪表二","仪表三","仪表四","仪表五","仪表六","仪表七","仪表八"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (!isTaskRoot()) {
//            finish();
//            return;
//        }
        setContentView(R.layout.activity_add_equipment);
        SoftHideKeyBoardUtil.assistActivity(this);
        mContext = this;
        Intent intent = getIntent();
        deviceNo = intent.getStringExtra("deviceNo");
        site = intent.getStringExtra("site");
        task = intent.getStringExtra("task");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        initView();
    }
    private void initView(){
        driverName = findViewById(R.id.driver_name);
        recyclerView = findViewById(R.id.rv_equipment);
        layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        add = findViewById(R.id.add);
        confirm = findViewById(R.id.confirm);
        add.setOnClickListener(this);
        confirm.setOnClickListener(this);
        equipmentList = new ArrayList<>();
        adapter = new EquipmentAdapter(mContext,equipmentList);
        adapter.setOnItemListener(new EquipmentAdapter.OnItemListener() {
            @Override
            public void onItemClick(View view, int position) {

            }
        });
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add:
                if (equipmentList.size()<8){
                    equipmentList.add(new Equipment(tabs[count],11L));
                    count++;
                    adapter.notifyDataSetChanged();
                }else {
                    Toast.makeText(mContext,"已超过最多仪表数量",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.confirm:
                confirm();
        }
    }

    private void confirm(){
        device.setUserNo(User.getInstance().getuserNo());
        String text = driverName.getText().toString();
        if (TextUtils.isEmpty(text)){
            Toast.makeText(mContext,"设备名不能为空",Toast.LENGTH_LONG).show();
            return;
        }else {
            device.setDeviceName(text);
        }
        checkMeter();
        if (isRight == 1){
            return;
        }
        String cookies = PublicData.getCookie(mContext);
        String url = PublicData.DOMAIN+"/api/admin/addDevice";
        RequestBody requestBody = new FormBody.Builder()
                .add("userNo",String.valueOf(User.getInstance().getuserNo()))
                .add("deviceName", device.getDeviceName())
                .add("deviceNo", deviceNo)
                .add("site",site)
                .add("task",task)
                .build();
        HttpRequest.getInstance().post(url,requestBody,this,cookies);
    }

    @Override
    public void onFailure(Call call, IOException e) {
        e.printStackTrace();
        Toast.makeText(mContext,"添加失败"+e.getMessage(),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        if (response.isSuccessful()){
            Gson gson = new GsonBuilder().create();
            String body = (response.body().string());
            DeviceCreateResponse createResponse = gson.fromJson(body,DeviceCreateResponse.class);
            if (createResponse.getStatus() == 1200){
                addMeters();
            }else if (createResponse.getStatus()== 1404) {
                ToastUtil.ToastTextThread(mContext,createResponse.getStatusinfo().getMessage());
            }
        }
    }

    private void checkMeter(){
        for (int i = 0; i < count; i++) {
            View view = layoutManager.findViewByPosition(i);
            EditText up = view.findViewById(R.id.edit_item_up);
            EditText low = view.findViewById(R.id.edit_item_low);
            String a = up.getText().toString();
            String b = low.getText().toString();
            if (!isNumber(a)||!isNumber(b)){
                ToastUtil.ToastTextThread(mContext,"请输入数字");
                isRight = 1;
                return;
            }
            if (Double.parseDouble(up.getText().toString())<Double.parseDouble(low.getText().toString())){
                ToastUtil.ToastTextThread(mContext,"请检查上下限是否正确");
                isRight = 1;
                return;
            }
            if ((isNumber(a) && isNumber(b) )&&(Double.parseDouble(up.getText().toString()) > Double.parseDouble(low.getText().toString()))){
                isRight = 0;
            }
        }
    }

    public static boolean isNumber(String value) {
        return isInteger(value) || isDouble(value);
    }
    /**
     * 判断字符串是否是整数
     */
    public static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 判断字符串是否是浮点数
     */
    public static boolean isDouble(String value) {
        try {
            Double.parseDouble(value);
            if (value.contains("."))
                return true;
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void addMeters(){
        Runnable runnable = new Runnable(){
            @Override
            public void run() {
                String url = PublicData.DOMAIN + "/api/admin/addMeter";
                String cookie = PublicData.getCookie(mContext);
                try {
                    for (int i = 0; i < count; i++) {
                        View view = layoutManager.findViewByPosition(i);
                        EditText editText = view.findViewById(R.id.text_item_equipment);
                        EditText up = view.findViewById(R.id.edit_item_up);
                        EditText low = view.findViewById(R.id.edit_item_low);
                        String name = editText.getText().toString();
                        String upper = up.getText().toString();
                        String lower = low.getText().toString();
                        RequestBody requestBody = new FormBody.Builder()
                                .add("userNo", String.valueOf(User.getInstance().getuserNo()))
                                .add("deviceNo", deviceNo)
                                .add("meterName", name)
                                .add("dataUpper",upper)
                                .add("dataLower",lower)
                                .add("task",task)
                                .build();
                        Response response = HttpRequest.getInstance().post(url, requestBody, cookie);
                        if (response.isSuccessful()) {
                            String body = (response.body().string());
                            Log.d("body: ",body);
                            Gson gson = new GsonBuilder().create();
                            StatusResponse r = gson.fromJson(body,StatusResponse.class);
                            if (r.getStatus() != 1200){
                                ToastUtil.ToastTextThread(mContext,r.getStatusinfo().getMessage());
                            }else {
                                ToastUtil.ToastTextThread(mContext,"添加仪表:"+name+"成功");
                            }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    AddEquipmentActivity.this.finish();
                    ToastUtil.ToastTextThread(AddEquipmentActivity.this,"上传结束");
                    EventBus.getDefault().post(new MessageEvent());
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

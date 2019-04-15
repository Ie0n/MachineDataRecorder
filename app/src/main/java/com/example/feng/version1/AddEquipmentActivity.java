package com.example.feng.version1;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.feng.version1.Public.PublicData;
import com.example.feng.version1.adapter.EquipmentAdapter;
import com.example.feng.version1.bean.Device;
import com.example.feng.version1.bean.DeviceCreateResponse;
import com.example.feng.version1.bean.Equipment;
import com.example.feng.version1.bean.User;
import com.example.feng.version1.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
    private int count = 0;
    private EditText driverName;
    private String [] tabs= {
            "仪表一","仪表二","仪表三","仪表四","仪表五","仪表六","仪表七","仪表八"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_equipment);
        mContext = this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        initView();
    }
    private void initView(){
        driverName = findViewById(R.id.driver_name);
        recyclerView = findViewById(R.id.rv_equipment);
        add = findViewById(R.id.add);
        confirm = findViewById(R.id.confirm);
        add.setOnClickListener(this);
        confirm.setOnClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
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
        Device device = new Device();
        device.setUserNo(User.getInstance().getuserNo());
        String text = driverName.getText().toString();
        if (TextUtils.isEmpty(text)){
            Toast.makeText(mContext,"设备名不能为空",Toast.LENGTH_LONG).show();
            return;
        }else {
            device.setDeviceName(text);
            device.setDeviceNo("number1");
        }
        String cookies = PublicData.getCookie(mContext);
        String url = PublicData.DOMAIN+"/api/user/addDevice";
        RequestBody requestBody = new FormBody.Builder()
                .add("deviceName", device.getDeviceName())
                .add("deviceNo", device.getDeviceNo())
                .add("userNo",String.valueOf(device.getUserNo()))
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
            String body = PublicData.clearChar(response.body().string());
            DeviceCreateResponse createResponse = gson.fromJson(body,DeviceCreateResponse.class);
            if (createResponse.getStatus() == 1200){
                Util.ToastTextThread(mContext,createResponse.getStatusInfo().getMessage());
            }else if (createResponse.getStatus()== 1404) {
                Util.ToastTextThread(mContext,createResponse.getStatusInfo().getMessage());
            }
        }
    }

    private void addMeters(){

    }
}

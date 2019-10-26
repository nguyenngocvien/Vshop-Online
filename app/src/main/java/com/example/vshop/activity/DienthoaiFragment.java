package com.example.vshop.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.vshop.R;
import com.example.vshop.adapter.DienthoaiAdapter;
import com.example.vshop.model.Sanpham;
import com.example.vshop.ultil.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DienthoaiFragment extends Fragment {

    View viewDienthoai;
    ListView listviewdt;
    DienthoaiAdapter dienthoaiAdapter;
    ArrayList<Sanpham> mangdt = new ArrayList<>();
    int page = 1;
    View footerview;
    boolean isLoading = false;
    boolean limitdata = false;
    DienthoaiFragment.mHandler mHand;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewDienthoai = inflater.inflate(R.layout.fragment_dienthoai, container, false);
        return viewDienthoai;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listviewdt = viewDienthoai.findViewById(R.id.listviewdienthoai);
        if (savedInstanceState != null){
            page = savedInstanceState.getInt("page");
            mangdt = (ArrayList<Sanpham>) savedInstanceState.getSerializable("listDienthoai");
            ViewAdapterSanpham();
            LoadMoreData();
        } else {
            ViewAdapterSanpham();
            GetData(page);
            LoadMoreData();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("page",page);
        outState.putSerializable("listDienthoai", mangdt);
    }

    private void LoadMoreData() {

        listviewdt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(),ChiTietSanPham.class);
                intent.putExtra("thongtinsanpham", mangdt.get(position));
                startActivity(intent);
            }
        });

        listviewdt.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0 && isLoading==false && limitdata==false){
                    isLoading = true;
                    DienthoaiFragment.ThreadData threadData = new DienthoaiFragment.ThreadData();
                    threadData.start();
                }
            }
        });
    }

    private void GetData(int Page) {
        final RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String duongdan = Server.duongDanDienthoai+String.valueOf(Page);
        StringRequest stringRequest =  new StringRequest(Request.Method.POST, duongdan, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int id=0;
                String Tendt="";
                int Giadt=0;
                String Hinhanhdt="";
                String Mota="";
                int Idspdt=0;
                if (response != null && response.length() != 2){
                    listviewdt.removeFooterView(footerview);
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i=0; i<jsonArray.length();i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            id = jsonObject.getInt("id");
                            Tendt = jsonObject.getString("tensp");
                            Giadt = jsonObject.getInt("giasp");
                            Hinhanhdt = jsonObject.getString("hinhanhsp");
                            Mota = jsonObject.getString("motasp");
                            Idspdt = jsonObject.getInt("idsanpham");
                            mangdt.add(new Sanpham(id,Tendt,Giadt,Hinhanhdt,Mota,Idspdt));
                            dienthoaiAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                }else{
                    limitdata = true;
                    listviewdt.removeFooterView(footerview);
                    //CheckConnection.showToast_Short(getContext(),"Đã hết dữ liệu");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                HashMap<String,String> param = new HashMap<String, String>();
                param.put("idsanpham",String.valueOf(1)); // id của điện thoại là 1
                return param;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void ViewAdapterSanpham() {
        dienthoaiAdapter = new DienthoaiAdapter(getContext(),mangdt);
        listviewdt.setAdapter(dienthoaiAdapter);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerview = inflater.inflate(R.layout.progressbar,null);
        mHand = new DienthoaiFragment.mHandler();
    }

    public class mHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    listviewdt.addFooterView(footerview);
                    break;
                case 1:
                    GetData(++page);
                    isLoading = false;
                    break;
            }
            super.handleMessage(msg);
        }
    }

    public class ThreadData extends Thread{
        @Override
        public void run() {
            mHand.sendEmptyMessage(0); // cho mHandler thực hiện đầu tiên
            try{
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Message message = mHand.obtainMessage(1); // phương thức obtainMessage liên kết các Thread với Handler
            mHand.sendMessage(message);
            super.run();
        }
    }
}

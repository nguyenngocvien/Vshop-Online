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
import com.example.vshop.adapter.LaptopAdapter;
import com.example.vshop.model.Sanpham;
import com.example.vshop.ultil.CheckConnection;
import com.example.vshop.ultil.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public class LaptopFragment extends Fragment {

    private View viewLaptop;
    private ListView listviewlaptop;
    private LaptopAdapter laptopAdapter;
    private ArrayList<Sanpham> manglaptop = new ArrayList<>();
    private int idlaptop = 0;
    private int page = 1;
    private View footerview;
    private boolean isLoading = false;
    private LaptopFragment.mHandler mHand;
    private boolean limitdata = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewLaptop = inflater.inflate(R.layout.fragment_laptop, container, false);
        return viewLaptop;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listviewlaptop = viewLaptop.findViewById(R.id.listviewlaptop);
        if (savedInstanceState != null){
            page = savedInstanceState.getInt("page");
            manglaptop = (ArrayList<Sanpham>) savedInstanceState.getSerializable("listLaptop");
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
        outState.putInt("page", page);
        outState.putSerializable("listLaptop", manglaptop);
    }

    private void GetData(int Page) {
        try {
            final RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());//tạo phương thức gửi lên cho server thông qua biến request
            String duongdan = Server.duongDanDienthoai+String.valueOf(Page); //page để sử dụng cho load more
            StringRequest stringRequest =  new StringRequest(Request.Method.POST, duongdan, new Response.Listener<String>() { //đọc dữ liệu về dưới dạng JSON
                @Override
                public void onResponse(String response) {
                    int id=0;
                    String Tenlaptop="";
                    int Gialaptop=0;
                    String Hinhanhlaptop="";
                    String Motalaptop="";
                    int Idsplaptop=0;
                    if (response != null && response.length() != 2){
                        listviewlaptop.removeFooterView(footerview);//tắt thanh progress bar khi dữ liệu đổ về
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i=0; i<jsonArray.length();i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                id = jsonObject.getInt("id");
                                Tenlaptop = jsonObject.getString("tensp");
                                Gialaptop = jsonObject.getInt("giasp");
                                Hinhanhlaptop = jsonObject.getString("hinhanhsp");
                                Motalaptop = jsonObject.getString("motasp");
                                Idsplaptop = jsonObject.getInt("idsanpham");
                                manglaptop.add(new Sanpham(id,Tenlaptop,Gialaptop,Hinhanhlaptop,Motalaptop,Idsplaptop));
                                laptopAdapter.notifyDataSetChanged(); //cập nhật lại khi đổ dữ liệu vào
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }else{
                        limitdata = true;
                        listviewlaptop.removeFooterView(footerview);
                        //CheckConnection.showToast_Short(getContext(),"Đã hết dữ liệu");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            }){
                @Override
                protected Map<String,String> getParams() throws AuthFailureError { //giá trị trong function này để truyền cho phuong thức POST
                    HashMap<String,String> param = new HashMap<String, String>();
                    param.put("idsanpham",String.valueOf(2)); //key phải trùng với key trong php, id của laptop là 2
                    return param;
                }
            };
            requestQueue.add(stringRequest);
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    private void LoadMoreData() {

        listviewlaptop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(),ChiTietSanPham.class);
                intent.putExtra("thongtinsanpham", manglaptop.get(position));
                startActivity(intent);
            }
        });

        listviewlaptop.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //function này hỗ trợ kéo đến vị trí nào đó sẽ thực hiện
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0 && isLoading==false && limitdata==false){
                    isLoading = true;
                    LaptopFragment.ThreadData threadData = new LaptopFragment.ThreadData();
                    threadData.start();
                }
            }
        });
    }

    private void ViewAdapterSanpham() {
        laptopAdapter = new LaptopAdapter(getContext(),manglaptop);
        listviewlaptop.setAdapter(laptopAdapter);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerview = inflater.inflate(R.layout.progressbar,null);
        mHand = new LaptopFragment.mHandler();
    }

    public class mHandler extends Handler {
        //Phân bố công việc cho Thread thực hiện
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    listviewlaptop.addFooterView(footerview);
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
        //tạo luồng dữ liệu, 1 luồng load dữ liệu về và 1 luồng đổ dữ liệu lên
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

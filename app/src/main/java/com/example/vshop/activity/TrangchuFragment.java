package com.example.vshop.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.vshop.R;
import com.example.vshop.adapter.SanphamAdapter;
import com.example.vshop.model.Giohang;
import com.example.vshop.model.Sanpham;
import com.example.vshop.ultil.Server;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TrangchuFragment extends Fragment {
    private View viewTrangchu;
    private ViewFlipper viewFlipper;
    private RecyclerView recyclerViewmanhinhchinh;
    ArrayList<Sanpham> mangsanpham;
    SanphamAdapter sanphamAdapter;
    public static ArrayList<Giohang> manggiohang;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewTrangchu = inflater.inflate(R.layout.fragment_trangchu, container, false);
        return viewTrangchu;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MappingView();
        ActionViewFlipper();
        mangsanpham = new ArrayList<>();

        if (savedInstanceState != null){
            mangsanpham = (ArrayList<Sanpham>) savedInstanceState.getSerializable("listSpMoiNhat");
            ViewAdapter();
        } else {
            ViewAdapter();
            GetDuLieuSpMoiNhat();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("listSpMoiNhat", mangsanpham);
    }

    private void GetDuLieuSpMoiNhat() {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Server.duongDanSpMoiNhat, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response != null) {
                    int ID = 0;
                    String Tensanpham = "";
                    Integer Giasanpham = 0;
                    String Hinhanhsanpham = "";
                    String Motasanpham = "";
                    int IDsanpham = 0;
                    for (int i=0;i<response.length();i++){
                        try{
                            JSONObject jsonObject = response.getJSONObject(i);
                            ID = jsonObject.getInt("id");
                            Tensanpham = jsonObject.getString("tensp");
                            Giasanpham = jsonObject.getInt("giasp");
                            Hinhanhsanpham = jsonObject.getString("hinhanhsp");
                            Motasanpham = jsonObject.getString("motasp");
                            IDsanpham = jsonObject.getInt("idsanpham");
                            mangsanpham.add(new Sanpham(ID,Tensanpham,Giasanpham,Hinhanhsanpham,Motasanpham,IDsanpham));
                            sanphamAdapter.notifyDataSetChanged();
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void ActionViewFlipper() {
        ArrayList<String> mangquangcao = new ArrayList<>();
        mangquangcao.add("https://cdn.tgdd.vn/2019/09/banner/oppo-A9-800-300-800x300-(1).png");
        mangquangcao.add("https://cdn.tgdd.vn/2019/09/banner/DH-Thong-minh-thoi-trang-hotsale-800-300-800x300-(1).png");
        mangquangcao.add("https://cdn.tgdd.vn/2019/09/banner/PK-Trung-thu-800-300-800x300-(1).png");
        mangquangcao.add("https://cdn.tgdd.vn/2019/09/banner/800-300-800x300-(6).png");

        for (int i=0; i<mangquangcao.size();i++){
            ImageView imageView = new ImageView(getContext());
            Picasso.get().load(mangquangcao.get(i)).into(imageView); //load ảnh đổ vào imageView
            //Picasso.with(getApplicationContext()).load(mangquangcao.get(i)).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY); //scale ảnh khớp với khung viewflipper
            viewFlipper.addView(imageView);
        }
        viewFlipper.setFlipInterval(5000); //chạy trong 3s
        viewFlipper.setAutoStart(true); // tự chạy
        Animation animation_slide_in = AnimationUtils.loadAnimation(getContext(),R.anim.slide_in_left);
        Animation animation_slide_out = AnimationUtils.loadAnimation(getContext(),R.anim.slide_out_right);
        viewFlipper.setInAnimation(animation_slide_in);
        viewFlipper.setOutAnimation(animation_slide_out);
    }

    private void ViewAdapter(){
        sanphamAdapter = new SanphamAdapter(mangsanpham,getContext());
        recyclerViewmanhinhchinh.setAdapter(sanphamAdapter);//hiện thị
        recyclerViewmanhinhchinh.setHasFixedSize(true);//Nếu các Item có cùng chiều cao và độ rộng thì có thể tối ưu  để khi cuộn danh sách được mượt mà hơn
        recyclerViewmanhinhchinh.setLayoutManager(new GridLayoutManager(getContext(),2));//chia listview thành 2 cột
        recyclerViewmanhinhchinh.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        recyclerViewmanhinhchinh.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.HORIZONTAL));
        if (manggiohang != null){

        }else {
            manggiohang = new ArrayList<>();
        }
    }

    private void MappingView() {
        viewFlipper = viewTrangchu.findViewById(R.id.viewlipper);
        recyclerViewmanhinhchinh = viewTrangchu.findViewById(R.id.recyclerview);
    }
}

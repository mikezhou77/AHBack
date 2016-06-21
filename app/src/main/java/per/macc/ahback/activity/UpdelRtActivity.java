package per.macc.ahback.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import per.macc.ahback.R;
import per.macc.ahback.bean.Roomtype;

/**
 * 显示所有客房类型
 * Created by Macc on 2016/6/13.
 */
public class UpdelRtActivity extends AppCompatActivity
{
    private List<Roomtype> roomtypeList = new ArrayList<>();
    private static int size24 = 24;
    private static int size22 = 22;
    private LinearLayout rootview;
    private LinearLayout infoview;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent info)
    {
        switch (requestCode)
        {
            case 1:                                     //是否成功添加客房类型
                if (resultCode == RESULT_OK)
                {
                    if(info.getIntExtra("info", 1) == 1)
                    {
                        //在最底部的布局中显示成功消息
                        Snackbar.make(infoview, getString(R.string.uprt_success),
                                Snackbar.LENGTH_SHORT).show();
                        getallroomtype();
                    }
                    else
                    {
                        Snackbar.make(infoview, getString(R.string.delrt_success),
                                Snackbar.LENGTH_SHORT).show();
                        getallroomtype();
                    }
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updel_roomtype);
        init();
    }

    private void init()
    {
        rootview = (LinearLayout)findViewById(R.id.updelrt_rootview);
        infoview = (LinearLayout)findViewById(R.id.updelrt_infoview);
        Button back = (Button)findViewById(R.id.back);
        assert back != null;
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
        getallroomtype();
    }

    private void addroomtype()
    {
        rootview.removeAllViews();            //预先移除原来的所有内容

        //以下4个属性分别为显示每一行两个客房信息的layout，四个客房信息的布局，按钮的布局以及分割线的布局属性
        LinearLayout.LayoutParams singlertLayout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams textLayout = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        LinearLayout.LayoutParams buttonLayout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        buttonLayout.gravity = Gravity.END;                             //让按钮靠最右侧显示
        LinearLayout.LayoutParams partlineLayout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1);
        partlineLayout.setMargins(0, 0, 0, 16);             //距离底部10px

        for(int i = 0; i < roomtypeList.size(); i++)                    //循环添加得到的所有客房类型
        {
            //设置第一行的Layout
            LinearLayout singleop1 = new LinearLayout(this);
            singleop1.setLayoutParams(singlertLayout);
            singleop1.setOrientation(LinearLayout.HORIZONTAL);

            //设置第二行的Layout
            LinearLayout singleop2 = new LinearLayout(this);
            singleop2.setLayoutParams(singlertLayout);
            singleop2.setOrientation(LinearLayout.HORIZONTAL);

            //设置客房类型名显示
            final TextView name = new TextView(this);
            name.setText(roomtypeList.get(i).getRtname());
            name.setTextSize(size24);
            name.setLayoutParams(textLayout);

            //设置客房大小显示
            final TextView size = new TextView(this);
            size.setText(roomtypeList.get(i).getRtsize() + getString(R.string.rt_squaremeter));
            size.setTextSize(size24);
            size.setLayoutParams(textLayout);

            //设置客房基础价格显示
            final TextView baseprice = new TextView(this);
            baseprice.setText(roomtypeList.get(i).getBaseprice() + getString(R.string.yuan));
            baseprice.setTextSize(size24);
            baseprice.setLayoutParams(textLayout);

            //设置客房可预订数量显示
            final TextView num = new TextView(this);
            num.setText(getString(R.string.canbook) + roomtypeList.get(i).getRtnum() + getString(R.string.jian));
            num.setTextSize(size24);
            num.setLayoutParams(textLayout);

            //设置修改按钮
            final Button updatert = new Button(this);
            updatert.setLayoutParams(buttonLayout);
            updatert.setId(roomtypeList.get(i).getRtno());               //id设置为客房类型的id
            updatert.setTextSize(size22);
            updatert.setText(getString(R.string.btn_update));
            updatert.setTextColor(getResources().getColor(R.color.lightblue));
            updatert.setBackgroundColor(Color.TRANSPARENT);             //背景为透明
            updatert.setGravity(Gravity.CENTER);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                updatert.setStateListAnimator(null);                    //无边框
            }
            updatert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateroomtype(updatert.getId());
                }
            });

            //设置分割线
            View partline = new View(this);
            partline.setLayoutParams(partlineLayout);
            partline.setBackgroundColor(
                    ContextCompat.getColor(this, R.color.gray));        //分割线颜色为灰色

            singleop1.addView(name);
            singleop1.addView(size);
            singleop2.addView(baseprice);
            singleop2.addView(num);
            rootview.addView(singleop1);
            rootview.addView(singleop2);
            rootview.addView(updatert);
            rootview.addView(partline);
        }

    }

    /**
     * 获取所有客房类型
     */
    private void getallroomtype()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String url = getString(R.string.url_http_real) + getString(R.string.action_roomtype_getallrt);
        //String url = getString(R.string.url_roomtype_virtual) + getString(R.string.action_roomtype_getallop);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        try
                        {
                            //json对象中对的list先转成jsonarray，再通过其获取对象
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("roomtypelist");
                            if(jsonArray.length() != 0)
                            {
                                roomtypeList.clear();
                                for(int i = 0; i < jsonArray.length(); i++)
                                {
                                    Roomtype roomtype = new Roomtype();
                                    jsonObject = jsonArray.getJSONObject(i);
                                    roomtype.setRtno(jsonObject.getInt("rtno"));
                                    roomtype.setRtname(jsonObject.getString("rtname"));
                                    roomtype.setRtsize(jsonObject.getInt("rtsize"));
                                    roomtype.setRtnum(jsonObject.getInt("rtnum"));
                                    roomtype.setBaseprice(jsonObject.getInt("baseprice"));
                                    roomtype.setRtinfo(jsonObject.getString("rtinfo"));
                                    roomtypeList.add(roomtype);
                                }
                                addroomtype();
                            }
                            else
                            {
                                TextView rtname = new TextView(UpdelRtActivity.this);
                                rtname.setText(getString(R.string.updelrt_nort));
                                rtname.setTextSize(size24);
                            }
                        }
                        catch(JSONException e)
                        {
                            Log.d("数据转换异常", "从服务器获得的JSON数据异常");
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        new  AlertDialog.Builder(UpdelRtActivity.this)
                                .setTitle("连接失败" )
                                .setMessage(getString(R.string.intent_wrong))
                                .setPositiveButton(getString(R.string.ok),  null )
                                .show();
                        Log.d("连接失败", "连接失败");
                    }
                });
        requestQueue.add(stringRequest);
    }


    /**
     * 获取目标客房类型
     */
    private void updateroomtype(int id)
    {
        for(int i = 0; i < roomtypeList.size(); i++)
        {
            if(roomtypeList.get(i).getRtno() == id)
            {
                //获取回传数据后将需要修改的信息传递到下一个intent
                Intent intent = new Intent(UpdelRtActivity.this, UpdateRtActivity.class);
                intent.putExtra("rtno", id);
                intent.putExtra("rtname", roomtypeList.get(i).getRtname());
                intent.putExtra("rtsize", roomtypeList.get(i).getRtsize());
                intent.putExtra("rtnum", roomtypeList.get(i).getRtnum());
                intent.putExtra("baseprice", roomtypeList.get(i).getBaseprice());
                intent.putExtra("rtinfo", roomtypeList.get(i).getRtinfo());
                startActivityForResult(intent, 1);
                break;
            }
        }

    }

}

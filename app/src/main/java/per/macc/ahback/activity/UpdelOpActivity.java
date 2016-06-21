package per.macc.ahback.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Path;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
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
import per.macc.ahback.bean.Operator;

/**
 * 操作员显示主页面，在操作员管理页面点击“管理”跳转
 * Created by Macc on 2016/6/5.
 */
public class UpdelOpActivity extends AppCompatActivity
{
    private List<Operator> operatorList = new ArrayList<>();
    private static int size24 = 24;
    private static int size22 = 22;
    private LinearLayout rootview;
    private LinearLayout infoview;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent info)
    {
        switch (requestCode)
        {
            case 1:                                     //是否成功添加操作员
                if (resultCode == RESULT_OK)
                {
                    if(info.getIntExtra("info", 1) == 1)
                    {
                        //在最底部的布局中显示成功消息
                        Snackbar.make(infoview, getString(R.string.upop_success),
                                Snackbar.LENGTH_SHORT).show();
                        getalloperator();
                    }
                    else
                    {
                        Snackbar.make(infoview, getString(R.string.delop_success),
                                Snackbar.LENGTH_SHORT).show();
                        getalloperator();
                    }
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updel_operator);
        init();
    }

    private void init()
    {
        rootview = (LinearLayout)findViewById(R.id.updelop_rootview);
        infoview = (LinearLayout)findViewById(R.id.updelop_infoview);
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
        getalloperator();
    }

    private void addoperator()
    {
        rootview.removeAllViews();            //预先移除原来的所有内容

        //以下三个属性分别为显示单个操作员的layout，账号显示和分割线的布局属性
        LinearLayout.LayoutParams singleopLayout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams textLayout = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        LinearLayout.LayoutParams partlineLayout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1);

        for(int i = 0; i < operatorList.size(); i++)                    //循环添加得到的所有操作员
        {
            //设置每一行的Layout
            LinearLayout singleop = new LinearLayout(this);
            singleop.setLayoutParams(singleopLayout);
            singleop.setOrientation(LinearLayout.HORIZONTAL);

            //设置操作员账号显示
            final TextView ousername = new TextView(this);
            ousername.setText(operatorList.get(i).getOusername());
            ousername.setTextSize(size24);
            ousername.setLayoutParams(textLayout);

            //设置修改按钮
            final Button updateop = new Button(this);
            assert updateop != null;
            updateop.setId(operatorList.get(i).getOno());               //id设置为操作员的id
            updateop.setTextSize(size22);
            updateop.setText(getString(R.string.btn_update));
            updateop.setTextColor(getResources().getColor(R.color.lightblue));
            updateop.setBackgroundColor(Color.TRANSPARENT);             //背景为透明
            updateop.setGravity(Gravity.CENTER);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                updateop.setStateListAnimator(null);                    //无边框
            }
            updateop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //点击“修改”后将id和账号名传递到下一个intent
                    Intent intent = new Intent(UpdelOpActivity.this, UpdateOpActivity.class);
                    intent.putExtra("ousername", ousername.getText());
                    intent.putExtra("ono", updateop.getId());
                    startActivityForResult(intent, 1);
                }
            });

            //设置分割线
            View partline = new View(this);
            partline.setLayoutParams(partlineLayout);
            partline.setBackgroundColor(
                    ContextCompat.getColor(this, R.color.gray));        //分割线颜色为灰色

            singleop.addView(ousername);
            singleop.addView(updateop);
            rootview.addView(singleop);
            rootview.addView(partline);
        }
    }

    /**
     * 获取所有操作员
     */
    private void getalloperator()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
      //  String url = getString(R.string.url_operator_virtual) + getString(R.string.action_operator_getallop);
        String url = getString(R.string.url_http_real) + getString(R.string.action_operator_getallop);
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
                            JSONArray jsonArray = jsonObject.getJSONArray("operatorlist");
                            if(jsonArray.length() != 0)
                            {
                                operatorList.clear();
                                for(int i = 0; i < jsonArray.length(); i++)
                                {
                                    Operator operator = new Operator();
                                    jsonObject = jsonArray.getJSONObject(i);
                                    operator.setOusername(jsonObject.getString("ousername"));
                                    operator.setOno(jsonObject.getInt("ono"));
                                    operatorList.add(operator);
                                }
                                addoperator();
                            }
                            else
                            {
                                TextView ousername = new TextView(UpdelOpActivity.this);
                                ousername.setText(getString(R.string.updelop_noop));
                                ousername.setTextSize(size24);
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
                        new  AlertDialog.Builder(UpdelOpActivity.this)
                                .setTitle("连接失败" )
                                .setMessage(getString(R.string.intent_wrong))
                                .setPositiveButton(getString(R.string.ok),  null )
                                .show();
                        Log.d("连接失败", "连接失败");
                    }
                });
        requestQueue.add(stringRequest);
    }
}

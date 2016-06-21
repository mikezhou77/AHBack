package per.macc.ahback.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import per.macc.ahback.R;

/**
 * 修改客房类型
 * Created by Macc on 2016/6/13.
 */
public class UpdateRtActivity extends AppCompatActivity
{
    private Pattern pattern = Pattern.compile("[0-9]*");      //判断输入是否是数字
    int rtno;
    String rtname, rtinfo, rtsize, baseprice, rtnum;
    TextInputLayout uprt_name, uprt_size, uprt_baseprice, uprt_info;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_roomtype);
        Intent intent = getIntent();
        rtno = intent.getIntExtra("rtno", 1);
        rtsize = String.valueOf(intent.getIntExtra("rtsize", 1));
        baseprice = String.valueOf(intent.getIntExtra("baseprice", 1));
        rtnum = String.valueOf(intent.getIntExtra("rtnum", 1));
        rtname = intent.getStringExtra("rtname");
        rtinfo = intent.getStringExtra("rtinfo");

        uprt_name = (TextInputLayout)findViewById(R.id.uprt_name);
        uprt_size = (TextInputLayout)findViewById(R.id.uprt_size);

        uprt_baseprice = (TextInputLayout)findViewById(R.id.uprt_baseprice);
        uprt_info = (TextInputLayout)findViewById(R.id.uprt_info);

        uprt_name.getEditText().setText(rtname);
        uprt_size.getEditText().setText(rtsize);
        uprt_baseprice.getEditText().setText(baseprice);
        uprt_info.getEditText().setText(rtinfo);

        //提交按钮
        Button uprt_submit = (Button)findViewById(R.id.uprt_submit);
        assert uprt_submit != null;
        uprt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rtname = uprt_name.getEditText().getText().toString();
                rtsize = uprt_size.getEditText().getText().toString();
                baseprice = uprt_baseprice.getEditText().getText().toString();
                rtinfo = uprt_info.getEditText().getText().toString();

                Matcher sizematcher = pattern.matcher(rtsize);
                Matcher pricematcher = pattern.matcher(baseprice);

                if(rtname.equals(""))
                {
                    uprt_name.setError(getString(R.string.rt_emptyname));
                }
                else if(!sizematcher.matches() && rtsize.equals(""))
                {
                    uprt_size.setError(getString(R.string.digitalonly));
                }
                else if(!pricematcher.matches() && baseprice.equals(""))
                {
                    uprt_baseprice.setError(getString(R.string.digitalonly));
                }
                else
                {
                    updateroomtype();
                }

            }
        });

        //删除按钮
        Button uprt_delete = (Button)findViewById(R.id.uprt_delete);
        assert uprt_delete != null;
        uprt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteroomtype();
            }
        });

        //返回按钮
        Button uprt_back = (Button)findViewById(R.id.uprt_back);
        assert uprt_back != null;
        uprt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
    }

    /**
     * 通过后台修改客房类型
     */
    private void updateroomtype()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String url = getString(R.string.url_http_real) + getString(R.string.action_roomtype_uprt);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        try
                        {
                            JSONObject jsonObject = new JSONObject(response);
                            String result = jsonObject.getString(getString(R.string.tag_upresult));
                            Log.d(getString(R.string.tag_resultinfo), result);
                            //通过获取的result来判断是否修改成功
                            if(result.equals(getString(R.string.tag_wrongname)))
                            {
                                Log.d("重复", "客房类型名重复");
                                uprt_name.setError(getString(R.string.addrt_wrongname));
                            }
                            if(result.equals(getString(R.string.tag_success)))
                            {
                                //修改客房类型成功，返回主界面,并且返回成功的tag
                                Intent intent = new Intent();
                                intent.putExtra("info", 1);     //1为修改成功
                                setResult(RESULT_OK, intent);
                                finish();
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
                        if(com.android.volley.NetworkError.class == error.getClass())
                        {
                            Log.d("连接错误", "网络连接有误");
                        }
                        new  AlertDialog.Builder(UpdateRtActivity.this)
                                .setTitle("添加失败" )
                                .setMessage("网络异常，请检查网络连接后重试" )
                                .setPositiveButton("好" ,  null )
                                .show();
                        Log.d("修改客房类型失败", "修改客房类型失败");
                    }
                })
        {

            //在这里设置需要post的参数
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("roomtype.rtno", String.valueOf(rtno));
                params.put("roomtype.rtname", rtname);
                params.put("roomtype.rtsize", rtsize);
                params.put("roomtype.rtnum", rtnum);
                params.put("roomtype.baseprice", baseprice);
                params.put("roomtype.rtinfo", rtinfo);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    /**
     * 通过后台删除客房类型
     */
    private void deleteroomtype()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
       // String url = getString(R.string.url_roomtype_virtual) + getString(R.string.action_roomtype_delrt);
        String url = getString(R.string.url_http_real) + getString(R.string.action_roomtype_delrt);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        try
                        {
                            JSONObject jsonObject = new JSONObject(response);
                            String result = jsonObject.getString(getString(R.string.tag_delresult));
                            Log.d(getString(R.string.tag_resultinfo), result);
                            //通过获取的result来判断是否删除成功
                            if(result.equals(getString(R.string.tag_success)))
                            {
                                //删除操作员成功，返回主界面,并且返回成功的tag
                                Intent intent = new Intent();
                                intent.putExtra("info", 2);     //2为删除成功
                                setResult(RESULT_OK, intent);
                                finish();
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
                        if(com.android.volley.NetworkError.class == error.getClass())
                        {
                            Log.d("连接错误", "网络连接有误");
                        }
                        new  AlertDialog.Builder(UpdateRtActivity.this)
                                .setTitle("删除失败")
                                .setMessage(getString(R.string.intent_wrong))
                                .setPositiveButton("好",  null )
                                .show();
                        Log.d("删除客房类型失败", "删除客房类型失败");
                    }
                })
        {

            //在这里设置需要post的参数
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("roomtype.rtno", String.valueOf(rtno));
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}
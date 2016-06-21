package per.macc.ahback.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

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
 * 添加客房类型
 * Created by Macc on 2016/6/13.
 */
public class AddRtActivity extends AppCompatActivity
{
    private Pattern pattern = Pattern.compile("[0-9]*");      //判断输入是否是数字
    private String name;
    private String size;
    private String num;
    private String baseprice;
    private String info;

    private TextInputLayout getname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_roomtype);

        //获取所有输入内容
        getname = (TextInputLayout) findViewById(R.id.addrt_name);
        final TextInputLayout getsize = (TextInputLayout) findViewById(R.id.addrt_size);
        final TextInputLayout getnum = (TextInputLayout) findViewById(R.id.addrt_num);
        final TextInputLayout getbaseprice = (TextInputLayout) findViewById(R.id.addrt_baseprice);
        final TextInputLayout getinfo = (TextInputLayout) findViewById(R.id.addrt_info);

        Button addop_submit = (Button)findViewById(R.id.submit_add);
        assert addop_submit != null;
        addop_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                name = getname.getEditText().getText().toString();
                size = getsize.getEditText().getText().toString();
                num = getnum.getEditText().getText().toString();
                baseprice = getbaseprice.getEditText().getText().toString();
                info = getinfo.getEditText().getText().toString();

                Matcher sizematcher = pattern.matcher(size);
                Matcher nummatcher = pattern.matcher(num);
                Matcher pricematcher = pattern.matcher(baseprice);

                if(name.equals(""))
                {
                    getname.setError(getString(R.string.rt_emptyname));
                }
                else if(!sizematcher.matches() && size.equals(""))
                {
                    getsize.setError(getString(R.string.digitalonly));
                }
                else if(!nummatcher.matches() && num.equals(""))
                {
                    getnum.setError(getString(R.string.digitalonly));
                }
                else if(!pricematcher.matches() && baseprice.equals(""))
                {
                    getbaseprice.setError(getString(R.string.digitalonly));
                }
                else
                {
                    addroomtype();
                }
            }
        });

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
    }

    /**
     * 添加客房类型
     */
    private void addroomtype()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
     //   String url = getString(R.string.url_http_virtual) + getString(R.string.action_roomtype_addrt);
        String url = getString(R.string.url_http_real) + getString(R.string.action_roomtype_addrt);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        try
                        {
                            JSONObject jsonObject = new JSONObject(response);
                            String result = jsonObject.getString(getString(R.string.tag_addresult));
                            Log.d(getString(R.string.tag_resultinfo), result);
                            //通过获取的result来判断是否登录成功
                            if(result.equals(getString(R.string.tag_wrongname)))
                            {
                                Log.d("重复", "客房类型名重复");
                                getname.setError(getString(R.string.addrt_wrongname));
                            }
                            if(result.equals(getString(R.string.tag_success)))
                            {
                                //添加操作员成功，返回主界面,并且返回成功的tag
                                Intent intent = new Intent();
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
                        new  AlertDialog.Builder(AddRtActivity.this)
                                .setTitle("添加失败" )
                                .setMessage("网络异常，请检查网络连接后重试" )
                                .setPositiveButton("好" ,  null )
                                .show();
                        Log.d("添加客房类型失败", "添加客房类型失败");
                    }
                })
        {

            //在这里设置需要post的参数
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("roomtype.rtname", name);
                params.put("roomtype.rtsize", size);
                params.put("roomtype.rtnum", num);
                params.put("roomtype.baseprice", baseprice);
                params.put("roomtype.rtinfo", info);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}

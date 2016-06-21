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
import android.widget.EditText;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import per.macc.ahback.R;
import per.macc.ahback.bean.Operator;

/**
 * 操作员修改页面
 * Created by Macc on 2016/6/5.
 */
public class UpdateOpActivity extends AppCompatActivity
{
    int ono;
    String ousername, password, cpassword;
    TextInputLayout upop_name, upop_password, upop_cpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_operator);
        Intent intent = getIntent();
        ousername = intent.getStringExtra("ousername");
        ono = intent.getIntExtra("ono", 1);

        upop_name = (TextInputLayout)findViewById(R.id.upop_name);
        upop_password = (TextInputLayout)findViewById(R.id.upop_password);
        upop_cpassword = (TextInputLayout)findViewById(R.id.upop_cpassword);
        upop_name.getEditText().setText(ousername);

        upop_cpassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                password = upop_password.getEditText().getText().toString();
                cpassword = upop_cpassword.getEditText().getText().toString();
                if(!cpassword.equals(password))
                {
                    upop_cpassword.setError(getString(R.string.addop_differentpassword));
                }
                else
                {
                    upop_cpassword.setError("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //提交按钮
        Button upop_submit = (Button)findViewById(R.id.upop_submit);
        assert upop_submit != null;
        upop_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ousername = upop_name.getEditText().getText().toString();
                password = upop_password.getEditText().getText().toString();
                cpassword = upop_cpassword.getEditText().getText().toString();
                if(ousername.equals(""))
                {
                    upop_name.setError(getString(R.string.login_emptyname));
                }
                else if(password.equals(""))
                {
                    upop_password.setError(getString(R.string.login_emptypassword));
                }
                else if(!cpassword.equals(password))
                {
                    upop_cpassword.setError(getString(R.string.addop_differentpassword));
                }
                else
                {
                    updateoperator(ono, ousername, password);
                }

            }
        });

        //删除按钮
        Button upop_delete = (Button)findViewById(R.id.upop_delete);
        assert upop_delete != null;
        upop_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteoperator(ono);
            }
        });

        //返回按钮
        Button upop_back = (Button)findViewById(R.id.upop_back);
        assert upop_back != null;
        upop_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
    }

    /**
     * 通过后台修改的操作员
     * @param ono 用户id
     * @param ousername 用户名
     * @param password  密码
     */
    private void updateoperator(final int ono, final String ousername, final String password)
    {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
      //  String url = getString(R.string.url_operator_virtual) + getString(R.string.action_operator_upop);
        String url = getString(R.string.url_http_real) + getString(R.string.action_operator_upop);
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
                                Log.d("重复", "用户名重复");
                                upop_name.setError(getString(R.string.addop_wrongname));
                            }
                            if(result.equals(getString(R.string.tag_success)))
                            {
                                //修改操作员成功，返回主界面,并且返回成功的tag
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
                        new  AlertDialog.Builder(UpdateOpActivity.this)
                                .setTitle("添加失败" )
                                .setMessage("网络异常，请检查网络连接后重试" )
                                .setPositiveButton("好" ,  null )
                                .show();
                        Log.d("修改操作员失败", "修改操作员失败");
                    }
                })
        {

            //在这里设置需要post的参数
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("operator.ono", String.valueOf(ono));
                params.put("operator.ousername", ousername);
                params.put("operator.opassword", password);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    /**
     * 通过后台删除操作员
     * @param ono 操作员id
     */
    private void deleteoperator(final int ono)
    {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
       // String url = getString(R.string.url_operator_virtual) + getString(R.string.action_operator_delop);
        String url = getString(R.string.url_http_real) + getString(R.string.action_operator_delop);

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
                        new  AlertDialog.Builder(UpdateOpActivity.this)
                                .setTitle("添加失败")
                                .setMessage(getString(R.string.intent_wrong))
                                .setPositiveButton("好",  null )
                                .show();
                        Log.d("删除操作员失败", "删除操作员失败");
                    }
                })
        {

            //在这里设置需要post的参数
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("operator.ono", String.valueOf(ono));
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}

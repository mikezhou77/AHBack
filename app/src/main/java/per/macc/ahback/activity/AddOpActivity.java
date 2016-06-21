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

import per.macc.ahback.R;

/**
 * 添加操作员
 * Created by Macc on 2016/6/1.
 */
public class AddOpActivity extends AppCompatActivity
{
    private String name;
    private String password;
    private String cpassword;
    private EditText addop_cpassword;
    private TextInputLayout getcpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_operator);

        //分别获取密码，确认密码框
        final TextInputLayout getpassword = (TextInputLayout) findViewById(R.id.addop_password);
        assert getpassword != null;

        getcpassword = (TextInputLayout) findViewById(R.id.addop_cpassword);
        assert getpassword != null;
        addop_cpassword = getcpassword.getEditText();
        assert addop_cpassword != null;
        addop_cpassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //根据输入的内容动态验证两次密码是否一致
                password = getpassword.getEditText().getText().toString();
                if(!addop_cpassword.getText().toString().equals(password))
                {
                    getcpassword.setError(getString(R.string.addop_differentpassword));
                }
                else
                {
                    getcpassword.setError("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Button addop_submit = (Button)findViewById(R.id.submit_add);
        assert addop_submit != null;
        addop_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                TextInputLayout getname = (TextInputLayout) findViewById(R.id.addop_name);
                assert getname != null;
                name = getname.getEditText().getText().toString();
                password = getpassword.getEditText().getText().toString();
                cpassword = getcpassword.getEditText().getText().toString();
                if(name.equals(""))
                {
                    getname.setError(getString(R.string.login_emptyname));
                }
                else if(password.equals(""))
                {
                    getpassword.setError(getString(R.string.login_emptypassword));
                }
                else if(!cpassword.equals(password))
                {
                    getcpassword.setError(getString(R.string.addop_differentpassword));
                }
                else
                {
                    addcheck(getname);
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
     * 添加操作员
     * @param getname 操作员名输入框
     */
    private void addcheck(final TextInputLayout getname)
    {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
       // String url = getString(R.string.url_http_virtual) + getString(R.string.action_operator_addop);
        String url = getString(R.string.url_http_real) + getString(R.string.action_operator_addop);
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
                                Log.d("重复", "用户名重复");
                                getname.setError(getString(R.string.addop_wrongname));
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
                        new  AlertDialog.Builder(AddOpActivity.this)
                                .setTitle("添加失败" )
                                .setMessage("网络异常，请检查网络连接后重试" )
                                .setPositiveButton("好" ,  null )
                                .show();
                        Log.d("添加操作员失败", "添加操作员失败");
                    }
                })
        {

            //在这里设置需要post的参数
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("operator.ousername", name);
                params.put("operator.opassword", password);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}

package per.macc.ahback.activity;

import android.Manifest;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity {

    String aname;
    FingerprintManager manager;
    KeyguardManager mKeyManager;
    RelativeLayout rootLayout;
    private static final int INTENET_REQUEST_CODE = 1;    //网络访问权限唯一的ID
    private final static int REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS = 0;       //指纹模块是否存在
    private final static String TAG = "finger_log";

    /**
     * 判断并申请网络访问权限
     */
    public void getIntenetPermission()
    {
        //如果没有权限则申请权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET},
                    INTENET_REQUEST_CODE);
        }
    }

    /**
     * 申请之后的权限处理
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == INTENET_REQUEST_CODE) {
            //权限被拒绝
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getIntenetPermission();                     //权限判断

        //指纹验证模块
        rootLayout = (RelativeLayout)findViewById(R.id.rootLayout);
        manager = (FingerprintManager) this.getSystemService(Context.FINGERPRINT_SERVICE);
        mKeyManager = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
        Button btn_finger = (Button) findViewById(R.id.btn_login_finger);
        assert btn_finger != null;
        btn_finger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFinger()) {
                    Snackbar.make(rootLayout, getString(R.string.finger_login_start),
                            Snackbar.LENGTH_LONG).show();
                    Log.d("fingerstart", "指纹验证开始");
                    startListening(null);
                }
            }
        });

        ImageView loginbackground = (ImageView)findViewById(R.id.login_background);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
        {
            assert loginbackground != null;
            loginbackground.setImageAlpha(180);
        }
        Button loginsubmit = (Button)findViewById(R.id.button_login);
        assert loginsubmit != null;
        loginsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //首先获取管理员输入的账户名和密码
                TextInputLayout getaname = (TextInputLayout) findViewById(R.id.login_aname);
                assert getaname != null;
                aname = getaname.getEditText().getText().toString();

                TextInputLayout getapassword = (TextInputLayout) findViewById(R.id.login_apassword);
                assert getapassword != null;
                String apassword = getapassword.getEditText().getText().toString();

                //先检测用户名密码是否为空
                if(aname.equals(""))
                {
                    getaname.setError(getString(R.string.login_emptyname));
                }
                else if(apassword.equals(""))
                {
                    getapassword.setError(getString(R.string.login_emptypassword));
                }
                else
                {
                    logincheck(apassword, getaname ,getapassword);
                    //将用户名密码上传服务器进行比对
                    //    Toast t = Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT);
                    //   t.show();
                }

            }
        });
    }


    public boolean isFinger() {

        //android studio 上，没有这个会报错
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(rootLayout, getString(R.string.finger_login_nopermission),
                    Snackbar.LENGTH_SHORT).show();
            return false;
        }
        //判断硬件是否支持指纹识别
        if (!manager.isHardwareDetected()) {
            Snackbar.make(rootLayout, getString(R.string.finger_login_nohardware),
                    Snackbar.LENGTH_SHORT).show();
            return false;
        }

        //判断是否有指纹录入
        if (!manager.hasEnrolledFingerprints()) {
            Snackbar.make(rootLayout, getString(R.string.finger_login_nofingerinfo),
                    Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    CancellationSignal mCancellationSignal = new CancellationSignal();
    //回调方法
    FingerprintManager.AuthenticationCallback mSelfCancelled = new FingerprintManager.AuthenticationCallback() {
        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            //但多次指纹密码验证错误后，进入此方法；并且，不能短时间内调用指纹验证
            Snackbar.make(rootLayout, errString,
                    Snackbar.LENGTH_SHORT).show();
            showAuthenticationScreen();
        }

        @Override
        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {

            Snackbar.make(rootLayout, helpString,
                    Snackbar.LENGTH_SHORT).show();
        }

        //指纹识别成功时
        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result)
        {
            Snackbar.make(rootLayout, getString(R.string.finger_login_success),
                    Snackbar.LENGTH_LONG).show();
            handler.sendEmptyMessageDelayed(1, 500);        //延迟0.5秒跳转登陆功能
            //fingerlogin();
        }

        @Override
        public void onAuthenticationFailed() {
            Snackbar.make(rootLayout, getString(R.string.finger_login_failed),
                    Snackbar.LENGTH_SHORT).show();
        }
    };


    public void startListening(FingerprintManager.CryptoObject cryptoObject) {
        //android studio 上，没有这个会报错
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "没有指纹识别权限", Toast.LENGTH_SHORT).show();
            return;
        }
        manager.authenticate(cryptoObject, mCancellationSignal, 0, mSelfCancelled, null);


    }

    /**
     * 锁屏密码
     */
    private void showAuthenticationScreen() {

        Intent intent = mKeyManager.createConfirmDeviceCredentialIntent("finger", "测试指纹识别");
        if (intent != null) {
            startActivityForResult(intent, REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS);
        }
    }


    private void fingerlogin()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String url = getString(R.string.url_http_real) + getString(R.string.action_admin_fingerlogin);
        //String url = getString(R.string.url_login_virtual) + getString(R.string.action_admin_fingerlogin);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        try
                        {
                            JSONObject jsonObject = new JSONObject(response);
                            String result = jsonObject.getString("result");
                            Log.d("resultinfo", result);
                            //通过获取的result来判断是否登录成功
                            if(result.equals("success"))
                            {
                                //登录成功，跳转界面
                                Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                                intent.putExtra("aname", "macc");        //将管理员账号传到下一个页面
                                startActivity(intent);
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
                        new  AlertDialog.Builder(MainActivity.this)
                                .setTitle("登录失败" )
                                .setMessage("网络异常，请检查网络连接后重试" )
                                .setPositiveButton("好" ,  null )
                                .show();
                        Log.d("登录失败", "登录失败");
                    }
                });
        requestQueue.add(stringRequest);
    }


    private void logincheck(final String apassword,
                            final TextInputLayout getaname, final TextInputLayout getapassword)
    {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        //    String url = getString(R.string.url_login_virtual) + getString(R.string.action_admin_loginresult);
        String url = getString(R.string.url_http_real) + getString(R.string.action_admin_loginresult);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        try
                        {
                            JSONObject jsonObject = new JSONObject(response);
                            String result = jsonObject.getString("result");
                            Log.d("resultinfo", result);
                            //通过获取的result来判断是否登录成功
                            if(result.equals("wrongname"))
                            {
                                getaname.setError(getString(R.string.login_wrongname));
                            }
                            if(result.equals("wrongpassword"))
                            {
                                getapassword.setError(getString(R.string.login_wrongpassword));
                            }
                            if(result.equals("success"))
                            {
                                //登录成功，跳转界面
                                getaname.setErrorEnabled(false);
                                getapassword.setErrorEnabled(false);
                                Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                                intent.putExtra("aname", aname);        //将管理员账号传到下一个页面
                                startActivity(intent);
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
                        new  AlertDialog.Builder(MainActivity.this)
                                .setTitle("登录失败" )
                                .setMessage("网络异常，请检查网络连接后重试" )
                                .setPositiveButton("好" ,  null )
                                .show();
                        Log.d("登录失败", "登录失败");
                    }
                })
        {

            //在这里设置需要post的参数
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("admin.ausername", aname);
                params.put("admin.apassword", apassword);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    /**
     * 消息处理句柄
     */
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    fingerlogin();
                    break;
            }
        }
    };
}

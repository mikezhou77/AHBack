package per.macc.ahback.activity;

import android.content.DialogInterface;
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
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import per.macc.ahback.R;
import per.macc.ahback.bean.Roomtype;

/**
 * 客房管理
 * Created by Macc on 2016/6/21.
 */
public class ManageRoomActivity  extends AppCompatActivity {

    private Pattern pattern = Pattern.compile("[0-9]*");      //判断输入是否是数字
    private Matcher matcher;
    private int updatertno;
    private String updatenum;
    private List<Roomtype> roomtypeList = new ArrayList<>();
    private static int size24 = 24;
    private static int size22 = 22;
    private LinearLayout rootview;
    private LinearLayout infoview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_room);
        init();
    }

    private void init()
    {
        rootview = (LinearLayout)findViewById(R.id.room_rootview);
        infoview = (LinearLayout)findViewById(R.id.room_infoview);
        Button back = (Button)findViewById(R.id.room_back);
        assert back != null;
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
        getallroom();
    }

    private void addroomview()
    {
        rootview.removeAllViews();            //预先移除原来的所有内容

        //以下3个属性分别为显示三行行两个客房信息的layout，文字的布局，以及分割线的布局属性
        LinearLayout.LayoutParams singleroomLayout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams textLayout = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        LinearLayout.LayoutParams partlineLayout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1);
        partlineLayout.setMargins(0, 0, 0, 14);             //距离底部10px

        for(int i = 0; i < roomtypeList.size(); i++)                    //循环添加得到的所有客房类型
        {
            //设置第一行的Layout
            LinearLayout singleop1 = new LinearLayout(this);
            singleop1.setLayoutParams(singleroomLayout);
            singleop1.setOrientation(LinearLayout.HORIZONTAL);

            //设置第二行的Layout
            LinearLayout singleop2 = new LinearLayout(this);
            singleop2.setLayoutParams(singleroomLayout);
            singleop2.setOrientation(LinearLayout.HORIZONTAL);

            //设置第三行的Layout
            LinearLayout singleop3 = new LinearLayout(this);
            singleop3.setLayoutParams(singleroomLayout);
            singleop3.setOrientation(LinearLayout.HORIZONTAL);

            //设置客房类型名显示
            final TextView name = new TextView(this);
            name.setText(roomtypeList.get(i).getRtname());
            name.setTextSize(size24);
            name.setGravity(Gravity.CENTER);
            name.setLayoutParams(textLayout);

            //设置客房可定数量显示
            final TextView num = new TextView(this);
            num.setText(getString(R.string.canbook) + roomtypeList.get(i).getRtnum() + getString(R.string.jian));
            num.setTextSize(size24);
            num.setGravity(Gravity.CENTER);
            num.setLayoutParams(textLayout);

            //添加客房文字
            final TextView addroom = new TextView(this);
            addroom.setGravity(Gravity.CENTER);
            addroom.setText(getString(R.string.room_add));
            addroom.setTextSize(size24);
            addroom.setLayoutParams(textLayout);

            //删除客房文字
            final TextView deleteroom = new TextView(this);
            deleteroom.setText(getString(R.string.room_delete));
            deleteroom.setGravity(Gravity.CENTER);
            deleteroom.setTextSize(size24);
            deleteroom.setLayoutParams(textLayout);

            //请选择文字(添加)
            final TextView addselect = new TextView(this);
            addselect.setId(roomtypeList.get(i).getRtno() + 100);      //添加为客房类型id+100
            addselect.setText(getString(R.string.room_select));
            addselect.setGravity(Gravity.CENTER);
            addselect.setTextSize(size24);
            addselect.setTextColor(getColor(R.color.black));
            addselect.setLayoutParams(textLayout);
            addselect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final EditText inputNum = new EditText(ManageRoomActivity.this);
                    inputNum.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
                    new  AlertDialog.Builder(ManageRoomActivity.this)
                        .setTitle(getString(R.string.room_inputnum))
                        .setView(inputNum)
                        .setNegativeButton(getString(R.string.cancel), null)
                        .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                matcher = pattern.matcher(inputNum.getText().toString());
                                if(matcher.matches() && !inputNum.getText().toString().equals(""))
                                {
                                    updatenum = inputNum.getText().toString();
                                    addselect.setText(updatenum);
                                }
                                else
                                {
                                    Snackbar.make(infoview, getString(R.string.digitalonly),
                                            Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .show();
                }
            });

            //请选择文字(删除)
            final TextView delselect = new TextView(this);
            addselect.setId(roomtypeList.get(i).getRtno() + 200);       //删除为客房类型id+200
            delselect.setText(getString(R.string.room_select));
            delselect.setGravity(Gravity.CENTER);
            delselect.setTextSize(size24);
            delselect.setTextColor(getColor(R.color.black));
            delselect.setLayoutParams(textLayout);
            delselect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final EditText inputNum = new EditText(ManageRoomActivity.this);
                    inputNum.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
                    new  AlertDialog.Builder(ManageRoomActivity.this)
                            .setTitle(getString(R.string.room_inputnum))
                            .setView(inputNum)
                            .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //判断输入的是否为纯数字
                                    matcher = pattern.matcher(inputNum.getText().toString());
                                    if(matcher.matches() && !inputNum.getText().toString().equals(""))
                                    {
                                        updatenum = inputNum.getText().toString();
                                        delselect.setText(updatenum);
                                    }
                                    else
                                    {
                                        inputNum.setText("");
                                        Snackbar.make(infoview, getString(R.string.digitalonly),
                                                Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .setNegativeButton(getString(R.string.cancel), null)
                            .show();
                }
            });

            //设置添加按钮
            final Button btn_addroom = new Button(this);
            btn_addroom.setId(roomtypeList.get(i).getRtno());               //id设置为客房类型的id
            btn_addroom.setTextSize(size22);
            btn_addroom.setText(getString(R.string.btn_add));
            btn_addroom.setTextColor(getColor(R.color.deepgreen));
            btn_addroom.setBackgroundColor(Color.TRANSPARENT);             //背景为透明
            btn_addroom.setGravity(Gravity.CENTER);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                btn_addroom.setStateListAnimator(null);                    //无边框
            }
            btn_addroom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updatertno = btn_addroom.getId();
                    addroom();
                }
            });

            //设置删除按钮
            final Button btn_deleteroom = new Button(this);
                    btn_deleteroom.setId(roomtypeList.get(i).getRtno());               //id设置为客房类型的id
                    btn_deleteroom.setTextSize(size22);
                    btn_deleteroom.setText(getString(R.string.btn_delete));
                    btn_deleteroom.setTextColor(getColor(R.color.red));
                    btn_deleteroom.setBackgroundColor(Color.TRANSPARENT);             //背景为透明
                    btn_deleteroom.setGravity(Gravity.CENTER);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    {
                        btn_deleteroom.setStateListAnimator(null);                    //无边框
                    }
                    btn_deleteroom.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            updatertno = btn_deleteroom.getId();
                            deleteroom();
                }
            });

            //设置分割线
            View partline = new View(this);
            partline.setLayoutParams(partlineLayout);
            partline.setBackgroundColor(
                    ContextCompat.getColor(this, R.color.gray));        //分割线颜色为灰色

            singleop1.addView(name);
            singleop1.addView(num);
            singleop2.addView(addroom);
            singleop2.addView(addselect);
            singleop2.addView(btn_addroom);
            singleop3.addView(deleteroom);
            singleop3.addView(delselect);
            singleop3.addView(btn_deleteroom);
            rootview.addView(singleop1);
            rootview.addView(singleop2);
            rootview.addView(singleop3);
            rootview.addView(partline);
        }

    }

    /**
     * 获取所有客房
     */
    private void getallroom()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String url = getString(R.string.url_http_real) + getString(R.string.action_room_getallroom);
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
                                roomtype.setRtnum(jsonObject.getInt("rtnum"));
                                roomtypeList.add(roomtype);
                            }
                            addroomview();
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
                    new  AlertDialog.Builder(ManageRoomActivity.this)
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
     * 添加目标客房
     */
    private void addroom()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String url = getString(R.string.url_http_real) + getString(R.string.action_room_addroom);
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
                            String result = jsonObject.getString("result");
                            if(result.equals("wrongnum"))
                            {
                                Snackbar.make(infoview, getString(R.string.addroom_numtoobig),
                                        Snackbar.LENGTH_SHORT).show();
                                TextView delselect = (TextView) findViewById(updatertno+100);
                                assert delselect != null;
                                delselect.setText(getString(R.string.room_select));
                            }
                            else
                            {
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
                                        roomtype.setRtnum(jsonObject.getInt("rtnum"));
                                        roomtypeList.add(roomtype);
                                    }
                                    Snackbar.make(infoview, getString(R.string.addroom_success),
                                            Snackbar.LENGTH_SHORT).show();
                                    addroomview();
                                }
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
                        new  AlertDialog.Builder(ManageRoomActivity.this)
                                .setTitle("连接失败" )
                                .setMessage(getString(R.string.intent_wrong))
                                .setPositiveButton(getString(R.string.ok),  null )
                                .show();
                        Log.d("连接失败", "连接失败");
                    }
                })

            {

                //在这里设置需要post的参数
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("roomtype.rtno", String.valueOf(updatertno));
                    params.put("inputNum", String.valueOf(updatenum));
                    return params;
                }
            };
        requestQueue.add(stringRequest);
    }


    /**
     * 删除目标客房
     */
    private void deleteroom()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String url = getString(R.string.url_http_real) + getString(R.string.action_room_delroom);
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
                        String result = jsonObject.getString("result");
                        if(result.equals("wrongnum"))
                        {
                            Snackbar.make(infoview, getString(R.string.delroom_numtoobig),
                                    Snackbar.LENGTH_SHORT).show();
                            TextView delselect = (TextView) findViewById(updatertno+200);
                            assert delselect != null;
                            delselect.setText(getString(R.string.room_select));
                        }
                        else
                        {
                            JSONArray jsonArray = jsonObject.getJSONArray("roomtypelist");
                            if (jsonArray.length() != 0) {
                                roomtypeList.clear();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    Roomtype roomtype = new Roomtype();
                                    jsonObject = jsonArray.getJSONObject(i);
                                    roomtype.setRtno(jsonObject.getInt("rtno"));
                                    roomtype.setRtname(jsonObject.getString("rtname"));
                                    roomtype.setRtnum(jsonObject.getInt("rtnum"));
                                    roomtypeList.add(roomtype);
                                }
                                Snackbar.make(infoview, getString(R.string.delroom_success),
                                        Snackbar.LENGTH_SHORT).show();
                                addroomview();
                            }
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
                    new  AlertDialog.Builder(ManageRoomActivity.this)
                            .setTitle("连接失败" )
                            .setMessage(getString(R.string.intent_wrong))
                            .setPositiveButton(getString(R.string.ok),  null )
                            .show();
                    Log.d("连接失败", "连接失败");
                }
            })
            {
                //在这里设置需要post的参数
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("roomtype.rtno", String.valueOf(updatertno));
                    params.put("inputNum", updatenum);
                    return params;
                }
            };
        requestQueue.add(stringRequest);
    }
}

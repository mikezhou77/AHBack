package per.macc.ahback.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import per.macc.ahback.R;
import per.macc.ahback.adapter.SimpleFragmentPagerAdapter;

/**
 * Created by Macc on 2016/5/31.
 * 管理员登录之后的主界面
 */
public class AdminActivity extends AppCompatActivity {

    ActionBarDrawerToggle drawerToggle;
    private SimpleFragmentPagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private LinearLayout rootfrag;
    private DrawerLayout drawerLayout;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent info)
    {
        switch (requestCode)
        {
            case 1:                                     //是否成功添加操作员
                if (resultCode == RESULT_OK)
                {
                    //在最底部的布局中显示成功消息
                    drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
                    assert drawerLayout != null;
                    Snackbar.make(drawerLayout, getString(R.string.addop_success),
                            Snackbar.LENGTH_SHORT).show();
                }
                break;
            case 2:                                     //是否成功添加客房类型
                if (resultCode == RESULT_OK)
                {
                    //在最底部的布局中显示成功消息
                    drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
                    assert drawerLayout != null;
                    Snackbar.make(drawerLayout, getString(R.string.addrt_success),
                            Snackbar.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_manager);
        init();
    }

    private void init()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        String welcomeaname = getString(R.string.menu_welcome) + intent.getStringExtra("aname");
        //先获取NavigationView菜单页面，再获取菜单头部，再通过头部获取里面的元素
        //由于要设置的用户名不再主页面中而在菜单中，直接获取textview是为空的
        NavigationView navigation = (NavigationView) findViewById(R.id.navigation);
        assert navigation != null;
        View headerView = navigation.getHeaderView(0);
        TextView welcomeview = (TextView) headerView.findViewById(R.id.txt_welcometitle);
        assert welcomeview != null;
        welcomeview.setText(welcomeaname);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerToggle = new ActionBarDrawerToggle(AdminActivity.this, drawerLayout,
                R.string.app_name, R.string.app_name);
        drawerLayout.setDrawerListener(drawerToggle);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //对tab的每个标签进行初始化
        String [] tabtitles = {this.getResources().getString(R.string.tab_opmanage),
                this.getResources().getString(R.string.tab_rtmanage),
                this.getResources().getString(R.string.tab_romanage)};

        pagerAdapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(), this);
        pagerAdapter.setTitle(tabtitles);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        assert viewPager != null;
        viewPager.setAdapter(pagerAdapter);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        assert tabLayout != null;
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_logout) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
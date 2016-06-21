package per.macc.ahback.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import per.macc.ahback.R;
import per.macc.ahback.activity.AddOpActivity;
import per.macc.ahback.activity.AddRtActivity;
import per.macc.ahback.activity.ManageRoomActivity;
import per.macc.ahback.activity.UpdelOpActivity;
import per.macc.ahback.activity.UpdelRtActivity;

/**
 * Fragment
 * Created by Macc on 2016/5/31.
 */
public class PageFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;

    public static PageFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        PageFragment pageFragment = new PageFragment();
        pageFragment.setArguments(args);
        return pageFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment, container, false);
        LinearLayout rootfrag = (LinearLayout) view.findViewById(R.id.rootfrag);
        //第1页，对应的操作是操作员的添加和修改
        if(mPage == 1)
        {
            //操作员添加activity
            Button addop = (Button) view.findViewById(R.id.btn_add);
            addop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), AddOpActivity.class);
                        getActivity().startActivityForResult(intent, 1);      //1表示添加操作员返回结果处理
                }
            });

            //操作员修改Activity
            Button updateop = (Button)view.findViewById(R.id.btn_up);
            updateop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), UpdelOpActivity.class);
                    getActivity().startActivity(intent);
                }
            });
        }
        //第2页，对应的操作是客房类型的添加和修改
        if(mPage == 2)
        {
            //客房类型添加activity
            Button addop = (Button) view.findViewById(R.id.btn_add);
            addop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), AddRtActivity.class);
                    getActivity().startActivityForResult(intent, 2);      //2表示添加客房类型返回结果处理
                }
            });

            //客房类型修改Activity
            Button updateop = (Button)view.findViewById(R.id.btn_up);
            updateop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), UpdelRtActivity.class);
                    getActivity().startActivity(intent);
                }
            });
        }
        //第3页，对应的操作是客房管理

        if(mPage == 3)
        {
            //客房管理activity
            Button addop = (Button) view.findViewById(R.id.btn_add);
            addop.setText(getString(R.string.btn_manage));
            addop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ManageRoomActivity.class);
                    getActivity().startActivity(intent);
                }
            });

            Button updateop = (Button)view.findViewById(R.id.btn_up);
            rootfrag.removeView(updateop);              //移除无用的控件
        }
        return view;
    }

}
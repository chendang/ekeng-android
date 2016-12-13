package com.cnnet.otc.health.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cnnet.otc.health.MainActivity;
import com.foxchen.qbs.R;
import com.cnnet.otc.health.activities.AddMemberActivity;
import com.cnnet.otc.health.bean.Member;
import com.cnnet.otc.health.comm.CommConst;
import com.cnnet.otc.health.comm.SysApp;
import com.cnnet.otc.health.managers.JsonManager;
import com.cnnet.otc.health.managers.RequestManager;
import com.cnnet.otc.health.util.DialogUtil;
import com.cnnet.otc.health.util.NetUtil;
import com.cnnet.otc.health.util.StringUtil;
import com.cnnet.otc.health.views.EmptyLayout;
import com.cnnet.otc.health.views.adapter.MemberListAdapter;
import com.njw.xlistview.library.XListView;

import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by SZ512 on 2016/1/9.
 */
public class MemberFragment extends Fragment implements XListView.IXListViewListener,
        MemberListAdapter.OnClickCheckBoxListener,AdapterView.OnItemClickListener {

    private final String TAG = "MemberFragment";

    private boolean autoConnectServer = true;
    private int memberOffset = 0;
    private DisplayMemberTask displayMemberTask;
    private View root;
    @Bind(R.id.fg_member_bar_top)
    LinearLayout memberTopBar;
    @Bind(R.id.fg_member_search_bar_top)
    LinearLayout memberSearchBar;
    @Bind(R.id.bt_search_member)
    Button btnSearchMember;
    @Bind(R.id.bt_quit_search_member)
    Button btnQuitSearch;
    @Bind(R.id.bt_add_member)
    Button btnAddMember;
    @Bind(R.id.listview)
    XListView listview;
    @Bind(R.id.et_search_name_like)
    EditText etSearchNameLike;
    @Bind(R.id.et_search_mobile_like)
    EditText etSearchMobileLike;
    @Bind(R.id.et_search_idnumber_like)
    EditText etSearchIdNumberLike;

    MemberListAdapter adapter;
    private EmptyLayout emptyLayout;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG, "AAAAAAAAAA____onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "AAAAAAAAAA____onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "AAAAAAAAAA____onCreateView");
        return initView(inflater, container);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "AAAAAAAAAA____onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "AAAAAAAAAA____onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "AAAAAAAAAA____onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "AAAAAAAAAA____onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "AAAAAAAAAA____onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "AAAAAAAAAA____onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "AAAAAAAAAA____onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "AAAAAAAAAA____onDetach");
        DialogUtil.cancelDialog();
        ButterKnife.unbind(this);
        if(displayMemberTask != null) {
            displayMemberTask.cancel();
            displayMemberTask = null;
        }
    }



    private View initView(LayoutInflater inflater, ViewGroup container) {
        if(root == null) {
            root = inflater.inflate(R.layout.fragment_member, container, false);
            ButterKnife.bind(this, root);
            btnAddMember.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), AddMemberActivity.class);
                    startActivityForResult(intent, CommConst.INTENT_REQUEST_ADD_MEMBER);
                }
            });
            adapter = new MemberListAdapter(getActivity(), null, this);
            //adapter.setOnClisItemCheckBoxListener(this);
            listview = (XListView) root.findViewById(R.id.listview);
            listview.setXListViewListener(this);
            listview.setAutoLoad(true);
            listview.setPullRefreshEnable(true);
            listview.setAdapter(adapter);
            listview.setOnItemClickListener(this);
            emptyLayout = new EmptyLayout(getActivity(), listview);
            if (SysApp.getAccountBean() == null ||
                    SysApp.getAccountBean().getRole() == CommConst.FLAG_USER_ROLE_MEMBER) {
                emptyLayout.setErrorLayout(R.string.empty_message, null);
            } else {
                DialogUtil.loadProgress(getActivity(), getString(R.string.loading));
                startGetMemberData();
            }

            btnSearchMember.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    memberTopBar.setVisibility(View.GONE);
                    memberSearchBar.setVisibility(View.VISIBLE);
                    etSearchNameLike.requestFocus();
                    inVisiableInput(etSearchNameLike, true);
                }
            });

            btnQuitSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    memberTopBar.setVisibility(View.VISIBLE);
                    memberSearchBar.setVisibility(View.GONE);
                    inVisiableInput(etSearchNameLike, false);
                    onRefresh();
                }
            });

            etSearchNameLike.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        startSearchMember();
                        return true;
                    }
                    return false;
                }
            });

            etSearchMobileLike.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        startSearchMember();
                        return true;
                    }
                    return false;
                }
            });

            etSearchIdNumberLike.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        startSearchMember();
                        return true;
                    }
                    return false;
                }
            });
            /*etSearchLike.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if(keyCode == KeyEvent.KEYCODE_ENTER) {
                        String searchLike = etSearchLike.getText().toString().trim();
                        if (StringUtil.isNotEmpty(searchLike)) {
                            inVisiableInput(etSearchLike, false);
                            adapter.refreshData(SysApp.getMyDBManager().searchClientMember(SysApp.getAccountBean().getFaUniqueKey(), searchLike));
                            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (imm.isActive()) {
                                imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                            }
                        }
                        return true;
                    }
                    return false;
                }
            });*/
        }
        return root;
    }

    /**
     * 隐藏键盘
     * @param view
     * @param isVisiable
     */
    private void inVisiableInput(View view, boolean isVisiable) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(isVisiable) {
            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);  //表示强制显示
        } else {
            if (imm.isActive()) {
                imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0); //强制隐藏键盘
            }
        }
    }

    /**
     * 当点击键盘上的搜索按钮时，开启搜索功能
     */
    private void startSearchMember() {
        String searchName = etSearchNameLike.getText().toString().trim();
        String searchMobile = etSearchMobileLike.getText().toString().trim();
        String searchIdNumber = etSearchIdNumberLike.getText().toString().trim();
        if (StringUtil.isNotEmpty(searchName) || StringUtil.isNotEmpty(searchMobile) || StringUtil.isNotEmpty(searchIdNumber)) {
            DialogUtil.loadProgress(getActivity(), getString(R.string.loading));
            inVisiableInput(etSearchMobileLike, false);
            listview.setAutoLoad(false);
            adapter.refreshData(SysApp.getMyDBManager().searchClientMember(SysApp.getAccountBean().getFaUniqueKey(),
                    searchName, searchMobile, searchIdNumber));
            RequestManager.searchMemberBySuperClinic(getContext(), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    Log.d(TAG, "startSearchMember : " + jsonObject.toString());
                    if(JsonManager.getCode(jsonObject) == 0) {
                        List<Member> lists = JsonManager.getMemberListByJson(jsonObject, true);
                        if(lists != null && lists.size() > 0) {
                            adapter.setData(lists, 1);
                        }
                    }
                    DialogUtil.cancelDialog();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    DialogUtil.cancelDialog();
                }
            }, SysApp.getAccountBean().getFaUniqueKey(), searchName, searchMobile, searchIdNumber);
        }
    }

    /**
     * 获取会员数据
     */
    private void startGetMemberData() {
        autoConnectServer = false;
        if(NetUtil.checkNetState(getActivity())) {
            RequestManager.getMemberVersion(getActivity(), new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject jsonObject) {
                    Log.d(TAG, "result verison : " + jsonObject.toString());
                    if (jsonObject != null) {
                        requestServerGetMember(JsonManager.getVersion(jsonObject));
                        return;
                    }
                    displayMemberInfo(null);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    displayMemberInfo(null);
                }
            }, SysApp.getAccountBean().getUniqueKey());
        } else {
            displayMemberInfo(null);
        }
    }

    /**
     * 请求服务器获取会员列表
     * @param cloud_version
     */
    private void requestServerGetMember(final long cloud_version){
        long clientVersion = SysApp.getMyDBManager().getDataVersionByType(SysApp.getAccountBean().getFaUniqueKey(), CommConst.FLAG_USER_ROLE_MEMBER);
        Log.d(TAG, "clientVersion : " + clientVersion);
        if(clientVersion < cloud_version) {
            if(SysApp.getAccountBean().getRole() == CommConst.FLAG_USER_ROLE_DOCTOR) {
                requestServerGetMemberByDoctor(clientVersion);
            } else {
                requestServerGetMemberByNurse(clientVersion);
            }
        } else {
            displayMemberInfo(null);
        }
    }


    /**
     * 医生：请求服务器获取会员列表
     */
    private void requestServerGetMemberByDoctor(long clientVersion) {
        RequestManager.getMemberGroupByDoctor(getActivity(), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d(TAG, "result --- " + jsonObject.toString());
                if (jsonObject != null && JsonManager.getCode(jsonObject) == 0) {
                    displayMemberInfo(jsonObject);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d(TAG, "GetMemberByDoctor Error : " + volleyError.toString());
                displayMemberInfo(null);
            }
        }, SysApp.getAccountBean().getUniqueKey(), String.valueOf(clientVersion));
    }

    /**
     * 护士：请求服务器获取会员列表
     * @param clientVersion
     */
    private void requestServerGetMemberByNurse(final long clientVersion) {
        RequestManager.getMemberGroupByLower(getActivity(), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d(TAG, "result : " + jsonObject.toString());
                if (jsonObject != null && JsonManager.getCode(jsonObject) == 0) {
                    displayMemberInfo(jsonObject);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d(TAG, "GetMemberByNurse Error : " + volleyError.toString());
                displayMemberInfo(null);
            }
        }, SysApp.getAccountBean().getUniqueKey(), String.valueOf(clientVersion));
    }

    /**
     * 显示本地会员信息列表
     * @param obj
     */
    private void displayMemberInfo(JSONObject obj) {
        //List<Me>
        if(displayMemberTask!=null) {
            displayMemberTask.cancel(true);
        }
        displayMemberTask = new DisplayMemberTask();
        if(obj == null) {
            startTask(displayMemberTask, memberOffset);
        } else {
            startTask(displayMemberTask, obj, memberOffset);
        }
    }

    private void startTask(AsyncTask task, Object... params) {
        if(Build.VERSION.SDK_INT < 14) {
            task.execute(params);
        } else {
            task.executeOnExecutor(SysApp.getExecutorNum(), params);
        }
    }

    @Override
    public void onRefresh() {
        memberOffset = 0;
        listview.setAutoLoad(true);
        displayMemberInfo(null);
    }

    @Override
    public void onLoadMore() {
        if(autoConnectServer) {
            DialogUtil.loadProgress(getActivity(), getString(R.string.loading));
            startGetMemberData();
        } else {
            displayMemberInfo(null);
        }
    }

    public void onClick(int resId, boolean hadSelectAll) {
        switch (resId) {
            case R.id.select_all:
                adapter.setSelectAll(!hadSelectAll);
                break;
            case R.id.cancel:
                adapter.setSelectAll(false);
                break;
            case R.id.fl_add_insp:
                List<Member> list = adapter.getSelectAllMembers();
                /*if(list != null && list.size() > 0) {
                    for(Member member:list) {
                        SysApp.getMyDBManager().addWaitForInspector(member.getUniqueKey(),
                                SysApp.getAccountBean().getName(), SysApp.getAccountBean().getUniqueKey());
                    }
                }*/
                Log.d(TAG, "uniqueKey = " + SysApp.getAccountBean().getUniqueKey());
                SysApp.getMyDBManager().addWaitFroInsList(list,SysApp.getAccountBean().getName(), SysApp.getAccountBean().getUniqueKey());
                ((MainActivity) getActivity()).changeFramgment(R.id.fl_home);
                adapter.setSelectAll(false);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == getActivity().RESULT_OK) {
            if(requestCode == CommConst.INTENT_REQUEST_ADD_MEMBER) {
                onRefresh();
                //displayMemberInfo(null);
            } else if(requestCode == CommConst.INTENT_REQUEST_SEARCH_MEMBER) {

            }
        }
    }

    @Override
    public void onClickItemCheckBox(int position, boolean isAll) {
        List<Member> list = adapter.getSelectAllMembers();
        if(list == null){
            ((MainActivity)getActivity()).onClickCheckBox(0, false);
        }else{
            int size = list.size();
            isAll = size == adapter.getCount();
            ((MainActivity)getActivity()).onClickCheckBox(size, isAll);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(position == 0 || position == adapter.getCount()+1){
            return ;
        }
        List<Member> memberList = adapter.getSelectAllMembers();
        if(memberList != null && memberList.size() >0){
            adapter.selectItem(position - 1);
        }
    }


    /**
     * 解析返回的数据，并将本地的数据显示出来
     */
    class DisplayMemberTask extends AsyncTask<Object, Void, List<Member>> {

        private boolean isCancel = false;

        @Override
        protected List<Member> doInBackground(Object... params) {
            Log.d(TAG, "start loading ---------------- ");
            int offset;
            if(params.length >= 1 && params.length<=2) {
                if (params.length == 2) {
                    JsonManager.saveMemberInfo((JSONObject) params[0]);
                    offset = (int) params[1];
                } else {
                    offset = (int) params[0];
                }
                if(!isCancel) {
                    return SysApp.getMyDBManager().getClientMemberList(SysApp.getAccountBean().getFaUniqueKey(), offset);
                }
            }
            return null;
        }

        public void cancel() {
            this.isCancel = true;
        }

        @Override
        protected void onPostExecute(List<Member> members) {
            super.onPostExecute(members);
            listview.stopLoadMore();
            listview.stopRefresh();
            DialogUtil.cancelDialog();
            if(members != null) {
                int size = members.size();
                if(size < CommConst.DB_DATA_PAGE) {
                    //over
                    listview.setAutoLoad(false);
                }
                adapter.setData(members, memberOffset);
                memberOffset += size;
            } else {
                if(adapter.getCount() == 0){
                    emptyLayout.setEmptyLayout(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startGetMemberData();
                        }
                    });
                }
            }
        }
    }
}

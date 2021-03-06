package com.rdc.gdut_activity.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;

import com.rdc.gdut_activity.R;
import com.rdc.gdut_activity.adapter.SignUpAdapter;
import com.rdc.gdut_activity.base.BaseActivity;
import com.rdc.gdut_activity.bean.ActivityInfoBean;
import com.rdc.gdut_activity.constant.Constant;
import com.rdc.gdut_activity.contract.SignUpContract;
import com.rdc.gdut_activity.presenter.SignUpPresenter;
import com.rdc.gdut_activity.view.LoadingDialog;
import com.rdc.gdut_activity.view.TopBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;

public class SignUpActivity extends BaseActivity implements View.OnClickListener, SignUpContract.View {

    @InjectView(R.id.tb_sign_up)
    TopBar mTbSignUp;
    @InjectView(R.id.lv_sign_up)
    ListView mLvSignUp;
    @InjectView(R.id.sc_sign_up)
    ScrollView mScSignUp;
    Button mBtnSignUp;

    private Map<String, String> mFormMap;
    private List<String> mFormList;
    private SignUpAdapter mSignUpAdapter;
    private LoadingDialog mDialog;
    private SignUpPresenter mSignUpPresenter;
    private ActivityInfoBean mActivityInfoBean;
    private boolean isSign = false;
    private String mSignUpId;

    @Override
    protected int setLayoutResID() {
        return R.layout.activity_sign_up;
    }

    @Override
    protected void initData() {

        mActivityInfoBean = getIntent().getParcelableExtra("signup_info");
        mFormMap = mActivityInfoBean.getFormDataMap();
        mFormList = new ArrayList<>();
        for (String form : mFormMap.keySet()) {
            mFormList.add(form);
        }
        mSignUpAdapter = new SignUpAdapter(this, mFormList);
        mSignUpPresenter = new SignUpPresenter(this);
        mSignUpPresenter.signUped(mActivityInfoBean.getObjectId());
    }

    @Override
    protected void initView() {
        mTbSignUp.setButtonBackground(R.drawable.iv_back, 0);
        setFootView();
        mLvSignUp.setAdapter(mSignUpAdapter);
        mScSignUp.scrollTo(0, 0);
    }

    @Override
    protected void initListener() {
        mBtnSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_sign_up:
                if (checkInfoSuccess() && !isSign) {
                    mSignUpPresenter.signUp(mFormMap);
                } else if (isSign) {
                    mSignUpPresenter.updateSignUp(mFormMap);
                } else {
                    showToast("请填写完全部信息!");
                }
                break;
        }

    }

    /**
     * 设置底部button
     */
    private void setFootView() {
        View footView = LayoutInflater.from(this).inflate(R.layout.item_list_foot_sign_up, null);
        mBtnSignUp = (Button) footView.findViewById(R.id.btn_sign_up);
        mLvSignUp.addFooterView(footView);
    }

    /**
     * 检测是否为空
     *
     * @return
     */
    private boolean checkInfoSuccess() {
        mFormMap = mSignUpAdapter.getFormMap();
        if (mFormMap.size() < mFormList.size()) {
            return false;
        }
        for (String value : mFormMap.values()) {
            if ("".equals(value)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void signUpSuccess() {
        showToast("报名成功!");
        finish();
    }

    @Override
    public void signUpFailed(String error) {
        showToast("报名失败!");
    }

    @Override
    public void showProgress(boolean isShow) {
        if (mDialog == null) {
            mDialog = new LoadingDialog(this, Constant.LOADING_STYLE);
        }
        if (isShow) {
            mBtnSignUp.setEnabled(false);
            mDialog.show();
        } else {
            mDialog.dismiss();
            mBtnSignUp.setEnabled(true);
        }
    }

    /**
     * 若已经报名
     */
    @Override
    public void isSignUp(String signupId) {
        mSignUpId = signupId;
        showToast("你已经报名了,再次报名将更新信息!");
    }

    @Override
    public void setIsSign(boolean isSign) {
        this.isSign = isSign;
    }

    @Override
    public String getUserId() {
        return mActivityInfoBean.getObjectId();
    }

    @Override
    public String getSignupId() {
        return mSignUpId;
    }

    @Override
    public ActivityInfoBean getActivityBean() {
        if (mActivityInfoBean != null) {
            return mActivityInfoBean;
        }
        return null;
    }
}

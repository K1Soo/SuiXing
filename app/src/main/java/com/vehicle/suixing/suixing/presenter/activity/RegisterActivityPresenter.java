package com.vehicle.suixing.suixing.presenter.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.vehicle.suixing.suixing.bean.BmobBean.User;
import com.vehicle.suixing.suixing.ui.activity.MainActivity;
import com.vehicle.suixing.suixing.util.Log;
import com.vehicle.suixing.suixing.util.RegisterUtils.AuthCodeUtil;
import com.vehicle.suixing.suixing.util.RegisterUtils.BmobError;
import com.vehicle.suixing.suixing.util.RegisterUtils.SaveUser;
import com.vehicle.suixing.suixing.util.formatUtils.MD5Utils;
import com.vehicle.suixing.suixing.view.activity.RegisterActivityView;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.VerifySMSCodeListener;

/**
 * Created by KiSoo on 2016/4/4.
 */
public class RegisterActivityPresenter {
    private RegisterActivityView view;
    private Context context;
    private String TAG = RegisterActivityPresenter.class.getName();
    private static final int AUTH_CODING = 0;
    private static final int AUTH_CODED = 1;
    private int WAITING_SECONDS = 100;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                /**
                 * 改变ui
                 * */
                case AUTH_CODING:
                    view.setClickable(false);
                    view.sending(msg.arg1);
                    break;
                case AUTH_CODED:
                    view.setClickable(true);
                    view.sendable();
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public void sendAuth() {
        AuthCodeUtil.sendAuthCode(context, view.getTel(), handler);
        Log.e(TAG, "发送验证码中...");

//        rx.Observable.create(new rx.Observable.OnSubscribe<Integer>() {
//            @Override
//            public void call(final Subscriber<? super Integer> subscriber) {
//                Log.e(TAG,"调用call()");
//                view.showDialog();
//                BmobSMS.requestSMSCode(context, view.getTel(), "AuthCode", new RequestSMSCodeListener() {
//                    @Override
//                    public void done(Integer integer, BmobException e) {
//                        Log.e(TAG,"发送结果是"+e.getErrorCode());
//                        view.dismissDialog();
//                        if (e == null) {
//                            Log.i("smile", "短信id：" + integer);//用于查询本次短信发送详情
//                            subscriber.onNext(WAITING_SECONDS);
//                            int waited = WAITING_SECONDS;
//                            while (true) {
//                                subscriber.onNext(waited);
//                                if (waited == 0) {
//                                    break;
//                                }
//                                waited = waited - 1;
//                                try {
//                                    Thread.sleep(1000);
//                                    Log.e(TAG, "现在为" + waited + "秒");
//                                } catch (InterruptedException error) {
//                                    error.printStackTrace();
//                                }
//                            }
//                        } else {
//                            String error = BmobError.error(e.getErrorCode());
//                            if (e.equals("")) {
//                                Log.e(TAG, e.getMessage() + e.getErrorCode());
//                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
//                            } else {
//                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
//                            }
//
//                        }
//                    }
//                });
//            }
//        }).subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<Integer>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onNext(Integer integer) {
//                        if (integer == 0) {
//                            view.setClickable(true);
//                            view.sendable();
//                        } else {
//                            view.setClickable(false);
//                            view.sending(integer);
//                        }
//                    }
//                });
    }


    public RegisterActivityPresenter(RegisterActivityView view, Context context) {
        this.view = view;
        this.context = context;
    }

    public void register() {
        final String username = view.getUsername();
        final String password1 = view.getPassword1();
        final String password2 = view.getPassword2();
        final String authcode = view.getAuthCode();
        final String tel = view.getTel();
        //判断是我们要的验证码
        if (authcode.length() != 6) {
            Toast.makeText(context, "请输入正确的验证码", Toast.LENGTH_SHORT).show();
            return;
        }
        //判断是否以字母开头
        if (!startWithLetter(username)) {
            Toast.makeText(context, "用户名要以字母开头哦！", Toast.LENGTH_SHORT).show();
            return;
        }
        //判断用户名长度
        if (username.length() < 8) {
            Toast.makeText(context, "用户名长度要大于8位", Toast.LENGTH_SHORT).show();
            return;
        }
        //判断密码长度大于等于6位
        if (password1.length() < 6) {
            Toast.makeText(context, "密码长度要大于6位哦！", Toast.LENGTH_SHORT).show();
            return;
        }
        //判断两次输入的密码相同
        if (!TextUtils.equals(password1, password2)) {
            Toast.makeText(context, "两次输入的密码不太一样哦", Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog dialog = ProgressDialog.show(context, "提示", "正在注册中...");
        BmobSMS.verifySmsCode(context, tel, authcode, new VerifySMSCodeListener() {
            @Override
            public void done(final BmobException e) {
                if (e == null) {
                    /**
                     * 验证通过
                     * */
                    final String deUsername = MD5Utils.ecoder(username);
                    final String dePassword = MD5Utils.ecoder(password1);
                    User user = new User();
                    user.setUsername(deUsername);
                    user.setPassword(dePassword);
                    user.setMobilePhoneNumber(tel);
                    user.setName("待修改");
                    user.setMotto("待修改");
                    user.signUp(context, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            /**
                             * 注册成功
                             * */
                            SaveUser.save(deUsername, dePassword, context);
                            Toast.makeText(context, "注册成功", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            context.startActivity(new Intent(context, MainActivity.class));
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            /**
                             * 注册失败
                             * */
                            dialog.dismiss();
                            String error = BmobError.error(i);
                            if (error.equals("")) {
                                Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                            }
                            Log.e(TAG, s + "错误码" + i);
                        }
                    });
                } else {
                    /**
                     * 验证失败，注册失败
                     * */
                    dialog.dismiss();
                    String error = BmobError.error(e.getErrorCode());
                    if (error.equals("")) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    }
                    Log.e(TAG, e.getMessage() + "错误码" + e.getErrorCode());
                }
            }
        });

    }

    private static boolean startWithLetter(String s) {
        char c;
        c = s.charAt(0);
        if (c >= 'A' && c <= 'z') {
            return true;
        } else {
            return false;
        }
    }
}

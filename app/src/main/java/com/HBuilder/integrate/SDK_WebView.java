package com.HBuilder.integrate;

import butterknife.ButterKnife;
import io.dcloud.EntryProxy;
import io.dcloud.common.DHInterface.ICore;
import io.dcloud.common.DHInterface.ICore.ICoreStatusListener;
import io.dcloud.common.DHInterface.ISysEventListener.SysEventType;
import io.dcloud.common.DHInterface.IWebview;
import io.dcloud.common.DHInterface.IWebviewStateListener;
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																												import io.dcloud.feature.internal.sdk.SDK;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.webkit.JavascriptInterface;

import com.cnnet.otc.health.comm.CommConst;
import com.cnnet.otc.health.comm.SysApp;
import com.cnnet.otc.health.db.MyDBManager;
import com.cnnet.otc.health.interfaces.SubmitServerListener;
import com.cnnet.otc.health.tasks.UploadAllNewInfoTask;
import com.cnnet.otc.health.util.AppCheckUtil;
import com.cnnet.otc.health.util.DialogUtil;
import com.cnnet.otc.health.util.NetUtil;
import com.cnnet.otc.health.util.ToastUtil;

import static com.android.volley.VolleyLog.TAG;
import static io.dcloud.common.adapter.util.AndroidResources.getString;

/**
 * 本demo为以webview控件方式集成5+ sdk， 
 *
 */
public class SDK_WebView extends Activity {

	boolean doHardAcc = true;
	EntryProxy mEntryProxy = null;
	private static Context ctx;
	private boolean stopThread=false;
	private Thread newThread=null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ctx=this;

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (mEntryProxy == null) {
			FrameLayout rootView = new FrameLayout(this);
			// 创建5+内核运行事件监听
			WebviewModeListener wm = new WebviewModeListener(this, rootView);
			// 初始化5+内核
			mEntryProxy = EntryProxy.init(this, wm);
			// 启动5+内核

			mEntryProxy.onCreate(this, savedInstanceState, SDK.IntegratedMode.WEBVIEW, null);
			setContentView(rootView);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return mEntryProxy.onActivityExecute(this, SysEventType.onCreateOptionMenu, menu);
	}
	@Override
	public void onPause() {
		super.onPause();
		mEntryProxy.onPause(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		mEntryProxy.onResume(this);
		startSyncInfo();
	}

	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (intent.getFlags() != 0x10600000) {// 非点击icon调用activity时才调用newintent事件
			mEntryProxy.onNewIntent(this, intent);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mEntryProxy.onStop(this);
		ButterKnife.unbind(this);
		if(SysApp.getMyDBManager() != null) {
			SysApp.getMyDBManager().destory();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean _ret = mEntryProxy.onActivityExecute(this, SysEventType.onKeyDown, new Object[] { keyCode, event });
		return _ret ? _ret : super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		boolean _ret = mEntryProxy.onActivityExecute(this, SysEventType.onKeyUp, new Object[] { keyCode, event });
		return _ret ? _ret : super.onKeyUp(keyCode, event);
	}

	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		boolean _ret = mEntryProxy.onActivityExecute(this, SysEventType.onKeyLongPress, new Object[] { keyCode, event });
		return _ret ? _ret : super.onKeyLongPress(keyCode, event);
	}

	public void onConfigurationChanged(Configuration newConfig) {
		try {
			int temp = this.getResources().getConfiguration().orientation;
			if (mEntryProxy != null) {
				mEntryProxy.onConfigurationChanged(this, temp);
			}
			super.onConfigurationChanged(newConfig);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		mEntryProxy.onActivityExecute(this, SysEventType.onActivityResult, new Object[] { requestCode, resultCode, data });
	}
	/**
	 * 启动时，就开始同步数据中
	 */

	private Handler handler = new Handler(){

		public void handleMessage(Message msg)
		 {
		// TODO Auto-generated method stub

		 Log.e(TAG, Thread.currentThread().getName() + " " +msg.obj);
			 setTitle("" +msg.obj);
			 if (msg.what == 0) {
				 ToastUtil.TextToast(getBaseContext(), R.string.sync_success, 2000);
			 }
			 if (msg.what == 1) {
				 ToastUtil.TextToast(getBaseContext(), R.string.mobile_network_error, 2000);
			 }

		}
	};
	private void startSyncInfo() {
//		if(SysApp.getAccountBean() != null && SysApp.getAccountBean().getRole() < CommConst.FLAG_USER_ROLE_MEMBER) {

		handler.post(new Runnable() {
			@Override
			public void run() {
				if(NetUtil.checkNetState(ctx) && !stopThread) {
//					DialogUtil.loadProgressUnClose(ctx, getString(R.string.syncing));
					UploadAllNewInfoTask.SynchronizationOtcInfo(ctx, new SubmitServerListener() {

						@Override
						public void onResult(int result) {
//							DialogUtil.cancelDialog();
							if (result == 0) {
								if(UploadAllNewInfoTask.isSync) {
//									mHandler.sendEmptyMessage(0);

									ToastUtil.TextToast(getBaseContext(), R.string.sync_success, 2000);
									stopThread=true;
							}

							} else {
//								mHandler.sendEmptyMessage(1);
								AppCheckUtil.toastErrMsgByConnectResultCode(getBaseContext(), result);
							}
//							Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//							startActivityForResult(enableIntent, 3);
						}
					});

				}
			}
		});


	}
//	}
}

class WebviewModeListener implements ICoreStatusListener {

	LinearLayout btns = null;
	Activity activity = null;
	ViewGroup mRootView = null;
	IWebview webview = null;
	ProgressDialog pd = null;

	public WebviewModeListener(Activity activity, ViewGroup rootView) {
		this.activity = activity;
		mRootView = rootView;
		btns = new LinearLayout(activity);
		mRootView.setBackgroundColor(0xffffffff);
		mRootView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				webview.onRootViewGlobalLayout(mRootView);
			}
		});
	}

	/**
	 * 5+内核初始化完成时触发
	 * */
	@Override
	public void onCoreInitEnd(ICore coreHandler) {
		// 设置单页面集成的appid
		String appid = "TestAppid";
		// 单页面集成时要加载页面的路径，可以是本地文件路径也可以是网络路径
				String url = "file:///android_asset/apps/H5Plugin/www/selectdevice_1.html";
		webview = SDK.createWebview(activity, url, appid, new IWebviewStateListener() {
			@Override
			public Object onCallBack(int pType, Object pArgs) {
				switch (pType) {
				case IWebviewStateListener.ON_WEBVIEW_READY:
					// 准备完毕之后添加webview到显示父View中，设置排版不显示状态，避免显示webview时，html内容排版错乱问题
					((IWebview) pArgs).obtainFrameView().obtainMainView().setVisibility(View.INVISIBLE);
					SDK.attach(mRootView, ((IWebview) pArgs));
					break;
				case IWebviewStateListener.ON_PAGE_STARTED:
					// 首页面开始加载事件
					break;
				case IWebviewStateListener.ON_PROGRESS_CHANGED:
					// 首页面加载进度变化
					break;
				case IWebviewStateListener.ON_PAGE_FINISHED:
					// 页面加载完毕，设置显示webview
					webview.obtainFrameView().obtainMainView().setVisibility(View.VISIBLE);
					break;
				}
				return null;
			}
		});

		final WebView webviewInstance = webview.obtainWebview();


		// 监听返回键
		webviewInstance.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					if (webviewInstance.canGoBack()) {
						webviewInstance.goBack();
						return true;
					}
				}
				return false;
			}
		});
	}

	// 5+SDK 开始初始化时触发
	@Override
	public void onCoreReady(ICore coreHandler) {
		try {
			// 初始化5+ SDK，
			// 5+SDK的其他接口需要在SDK初始化后才能調用
			SDK.initSDK(coreHandler);
			// 当前应用可使用全部5+API
			SDK.requestAllFeature();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

/**
*  	// 通过代码注册扩展插件的示例
*	private void regNewApi() {
*       // 扩展插件在js层的标识
*		String featureName = "T";
*		// 扩展插件的原生类名
*		String className = "com.HBuilder.integrate.webview.WebViewMode_FeatureImpl";
*		// 扩展插件的JS层封装的方法
*		String content = "(function(plus){function test(){return plus.bridge.execSync('T','test',[arguments]);}plus.T = {test:test};})(window.plus);";
*	 	// 向5+SDK注册扩展插件
*		SDK.registerJsApi(featureName, className, content);
*	}
**/

	@Override
	public boolean onCoreStop() {
		// TODO Auto-generated method stub
		return false;
	}

}

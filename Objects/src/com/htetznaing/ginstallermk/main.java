package com.htetznaing.ginstallermk;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = false;
	public static final boolean includeTitle = true;
    public static WeakReference<Activity> previousOne;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isFirst) {
			processBA = new BA(this.getApplicationContext(), null, null, "com.htetznaing.ginstallermk", "com.htetznaing.ginstallermk.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
				p.finish();
			}
		}
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		mostCurrent = this;
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(processBA, wl, false))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "com.htetznaing.ginstallermk", "com.htetznaing.ginstallermk.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "com.htetznaing.ginstallermk.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEventFromUI(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return main.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeydown", this, new Object[] {keyCode, event}))
            return true;
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeyup", this, new Object[] {keyCode, event}))
            return true;
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null) //workaround for emulator bug (Issue 2423)
            return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        processBA.setActivityPaused(true);
        mostCurrent = null;
        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
			if (mostCurrent == null || mostCurrent != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
		    processBA.raiseEvent(mostCurrent._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        for (int i = 0;i < permissions.length;i++) {
            Object[] o = new Object[] {permissions[i], grantResults[i] == 0};
            processBA.raiseEventFromDifferentThread(null,null, 0, "activity_permissionresult", true, o);
        }
            
    }

public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.objects.Timer _v5 = null;
public static anywheresoftware.b4a.phone.Phone.ContentChooser _v6 = null;
public static anywheresoftware.b4a.phone.Phone.ContentChooser _v7 = null;
public static anywheresoftware.b4a.phone.Phone.ContentChooser _v0 = null;
public static anywheresoftware.b4a.phone.Phone.ContentChooser _vv1 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _vvv1 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _vvv6 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _vvv2 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _vvv3 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _vvv4 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _vvv5 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _vvv7 = null;
public anywheresoftware.b4a.objects.LabelWrapper _vvv0 = null;
public static String _vv6 = "";
public static String _vv7 = "";
public com.AB.ABZipUnzip.ABZipUnzip _vv0 = null;
public nnl.zipsigner.NNLZipSigner _vv5 = null;
public MLfiles.Fileslib.MLfiles _vvvv1 = null;
public anywheresoftware.b4a.admobwrapper.AdViewWrapper _vv3 = null;
public anywheresoftware.b4a.admobwrapper.AdViewWrapper.InterstitialAdWrapper _vv4 = null;
public static int _vv2 = 0;
public com.htetznaing.ginstallermk.starter _vvvv2 = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
return vis;}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 37;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 39;BA.debugLine="If GetDeviceLayoutValues.ApproximateScreenSize <";
if (anywheresoftware.b4a.keywords.Common.GetDeviceLayoutValues(mostCurrent.activityBA).getApproximateScreenSize()<6) { 
 //BA.debugLineNum = 40;BA.debugLine="If 100%x > 100%y Then h = 32dip Else h = 50dip";
if (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA)>anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA)) { 
_vv2 = anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (32));}
else {
_vv2 = anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50));};
 }else {
 //BA.debugLineNum = 42;BA.debugLine="h = 90dip";
_vv2 = anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (90));
 };
 //BA.debugLineNum = 46;BA.debugLine="B.Initialize2(\"B\",\"ca-app-pub-4173348573252986/43";
mostCurrent._vv3.Initialize2(mostCurrent.activityBA,"B","ca-app-pub-4173348573252986/4361816156",mostCurrent._vv3.SIZE_SMART_BANNER);
 //BA.debugLineNum = 47;BA.debugLine="Activity.AddView(B, 0dip, 100%y - h, 100%x, h)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._vv3.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)),(int) (anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA)-_vv2),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),_vv2);
 //BA.debugLineNum = 48;BA.debugLine="B.LoadAd";
mostCurrent._vv3.LoadAd();
 //BA.debugLineNum = 49;BA.debugLine="Log(B)";
anywheresoftware.b4a.keywords.Common.Log(BA.ObjectToString(mostCurrent._vv3));
 //BA.debugLineNum = 52;BA.debugLine="I.Initialize(\"I\",\"ca-app-pub-4173348573252986/879";
mostCurrent._vv4.Initialize(mostCurrent.activityBA,"I","ca-app-pub-4173348573252986/8792015757");
 //BA.debugLineNum = 53;BA.debugLine="I.LoadAd";
mostCurrent._vv4.LoadAd();
 //BA.debugLineNum = 54;BA.debugLine="If I.Ready = False Then";
if (mostCurrent._vv4.getReady()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 55;BA.debugLine="I.LoadAd";
mostCurrent._vv4.LoadAd();
 };
 //BA.debugLineNum = 58;BA.debugLine="T.Initialize(\"T\",5000)";
_v5.Initialize(processBA,"T",(long) (5000));
 //BA.debugLineNum = 59;BA.debugLine="T.Enabled=False";
_v5.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 60;BA.debugLine="zs.Initialize";
mostCurrent._vv5.Initialize(processBA);
 //BA.debugLineNum = 61;BA.debugLine="apk = \"htetznaing.apk\"";
mostCurrent._vv6 = "htetznaing.apk";
 //BA.debugLineNum = 62;BA.debugLine="tempFolder = \".HtetzGInstallerMaker\"";
mostCurrent._vv7 = ".HtetzGInstallerMaker";
 //BA.debugLineNum = 63;BA.debugLine="If File.Exists(File.DirRootExternal&\"/\"&tempFolde";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/"+mostCurrent._vv7,mostCurrent._vv6)==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 64;BA.debugLine="File.Copy(File.DirAssets,apk,File.DirRootExterna";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),mostCurrent._vv6,anywheresoftware.b4a.keywords.Common.File.getDirRootExternal(),"htetz.zip");
 //BA.debugLineNum = 65;BA.debugLine="zip.ABUnzip(File.DirRootExternal&\"/htetz.zip\",Fi";
mostCurrent._vv0.ABUnzip(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/htetz.zip",anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/"+mostCurrent._vv7);
 //BA.debugLineNum = 66;BA.debugLine="File.Delete(File.DirRootExternal,\"htetz.zip\")";
anywheresoftware.b4a.keywords.Common.File.Delete(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal(),"htetz.zip");
 };
 //BA.debugLineNum = 69;BA.debugLine="cAPK.Initialize(\"cAPK\")";
_v6.Initialize("cAPK");
 //BA.debugLineNum = 70;BA.debugLine="cIcon.Initialize(\"cIcon\")";
_v7.Initialize("cIcon");
 //BA.debugLineNum = 71;BA.debugLine="cObb.Initialize(\"cObb\")";
_v0.Initialize("cObb");
 //BA.debugLineNum = 72;BA.debugLine="cData.Initialize(\"cData\")";
_vv1.Initialize("cData");
 //BA.debugLineNum = 74;BA.debugLine="edName.Initialize(\"edName\")";
mostCurrent._vvv1.Initialize(mostCurrent.activityBA,"edName");
 //BA.debugLineNum = 75;BA.debugLine="edName.Hint = \"Enter App Name\"";
mostCurrent._vvv1.setHint("Enter App Name");
 //BA.debugLineNum = 76;BA.debugLine="Activity.AddView(edName,5%x,1%y,80%x,10%y)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._vvv1.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (5),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (1),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (80),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (10),mostCurrent.activityBA));
 //BA.debugLineNum = 78;BA.debugLine="b1.Initialize(\"b1\")";
mostCurrent._vvv2.Initialize(mostCurrent.activityBA,"b1");
 //BA.debugLineNum = 79;BA.debugLine="b1.Text = \"Choose Icon + Image\"";
mostCurrent._vvv2.setText(BA.ObjectToCharSequence("Choose Icon + Image"));
 //BA.debugLineNum = 80;BA.debugLine="Activity.AddView(b1,20%x,edName.Height+edName.Top+";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._vvv2.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (20),mostCurrent.activityBA),(int) (mostCurrent._vvv1.getHeight()+mostCurrent._vvv1.getTop()+anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (1),mostCurrent.activityBA)),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (60),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (10),mostCurrent.activityBA));
 //BA.debugLineNum = 82;BA.debugLine="b2.Initialize(\"b2\")";
mostCurrent._vvv3.Initialize(mostCurrent.activityBA,"b2");
 //BA.debugLineNum = 83;BA.debugLine="b2.Text = \"Game APK\"";
mostCurrent._vvv3.setText(BA.ObjectToCharSequence("Game APK"));
 //BA.debugLineNum = 84;BA.debugLine="Activity.AddView(b2,20%x,b1.Height+b1.Top+1%y,60%x";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._vvv3.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (20),mostCurrent.activityBA),(int) (mostCurrent._vvv2.getHeight()+mostCurrent._vvv2.getTop()+anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (1),mostCurrent.activityBA)),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (60),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (10),mostCurrent.activityBA));
 //BA.debugLineNum = 86;BA.debugLine="b3.Initialize(\"b3\")";
mostCurrent._vvv4.Initialize(mostCurrent.activityBA,"b3");
 //BA.debugLineNum = 87;BA.debugLine="b3.Text=\"obb Zip File\"";
mostCurrent._vvv4.setText(BA.ObjectToCharSequence("obb Zip File"));
 //BA.debugLineNum = 88;BA.debugLine="Activity.AddView(b3,20%x,b2.Height+b2.Top+1%y,60%x";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._vvv4.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (20),mostCurrent.activityBA),(int) (mostCurrent._vvv3.getHeight()+mostCurrent._vvv3.getTop()+anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (1),mostCurrent.activityBA)),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (60),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (10),mostCurrent.activityBA));
 //BA.debugLineNum = 90;BA.debugLine="b4.Initialize(\"b4\")";
mostCurrent._vvv5.Initialize(mostCurrent.activityBA,"b4");
 //BA.debugLineNum = 91;BA.debugLine="b4.Text = \"data Zip File\"";
mostCurrent._vvv5.setText(BA.ObjectToCharSequence("data Zip File"));
 //BA.debugLineNum = 92;BA.debugLine="Activity.AddView(b4,20%x,b3.Height+b3.Top+1%y,60%x";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._vvv5.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (20),mostCurrent.activityBA),(int) (mostCurrent._vvv4.getHeight()+mostCurrent._vvv4.getTop()+anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (1),mostCurrent.activityBA)),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (60),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (10),mostCurrent.activityBA));
 //BA.debugLineNum = 94;BA.debugLine="edDevName.Initialize(\"edDevName\")";
mostCurrent._vvv6.Initialize(mostCurrent.activityBA,"edDevName");
 //BA.debugLineNum = 95;BA.debugLine="edDevName.Hint = \"Enter Creator/Powered By Name\"";
mostCurrent._vvv6.setHint("Enter Creator/Powered By Name");
 //BA.debugLineNum = 96;BA.debugLine="Activity.AddView(edDevName,5%x,b4.Height+b4.Top+1%";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._vvv6.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (5),mostCurrent.activityBA),(int) (mostCurrent._vvv5.getHeight()+mostCurrent._vvv5.getTop()+anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (1),mostCurrent.activityBA)),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (90),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (10),mostCurrent.activityBA));
 //BA.debugLineNum = 98;BA.debugLine="b5.Initialize(\"b5\")";
mostCurrent._vvv7.Initialize(mostCurrent.activityBA,"b5");
 //BA.debugLineNum = 99;BA.debugLine="b5.Text = \"Create Game Installer APK\"";
mostCurrent._vvv7.setText(BA.ObjectToCharSequence("Create Game Installer APK"));
 //BA.debugLineNum = 100;BA.debugLine="Activity.AddView(b5,5%x,edDevName.Top+edDevName.H";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._vvv7.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (5),mostCurrent.activityBA),(int) (mostCurrent._vvv6.getTop()+mostCurrent._vvv6.getHeight()+anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (1),mostCurrent.activityBA)),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (90),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (10),mostCurrent.activityBA));
 //BA.debugLineNum = 102;BA.debugLine="lb.Initialize(\"lb\")";
mostCurrent._vvv0.Initialize(mostCurrent.activityBA,"lb");
 //BA.debugLineNum = 103;BA.debugLine="lb.Text=\"Developed By Khun Htetz Naing\"";
mostCurrent._vvv0.setText(BA.ObjectToCharSequence("Developed By Khun Htetz Naing"));
 //BA.debugLineNum = 104;BA.debugLine="lb.Gravity = Gravity.CENTER";
mostCurrent._vvv0.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 105;BA.debugLine="lb.Typeface = Typeface.DEFAULT_BOLD";
mostCurrent._vvv0.setTypeface(anywheresoftware.b4a.keywords.Common.Typeface.DEFAULT_BOLD);
 //BA.debugLineNum = 106;BA.debugLine="Activity.AddView(lb,0%x,b5.Height+b5.Top+2%y,100%";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._vvv0.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (0),mostCurrent.activityBA),(int) (mostCurrent._vvv7.getHeight()+mostCurrent._vvv7.getTop()+anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (2),mostCurrent.activityBA)),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (10),mostCurrent.activityBA));
 //BA.debugLineNum = 107;BA.debugLine="End Sub";
return "";
}
public static boolean  _activity_keypress(int _keycode) throws Exception{
int _answ = 0;
 //BA.debugLineNum = 265;BA.debugLine="Sub Activity_KeyPress (KeyCode As Int) As Boolean";
 //BA.debugLineNum = 266;BA.debugLine="Dim Answ As Int";
_answ = 0;
 //BA.debugLineNum = 267;BA.debugLine="If KeyCode = KeyCodes.KEYCODE_BACK Then";
if (_keycode==anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_BACK) { 
 //BA.debugLineNum = 268;BA.debugLine="Answ = Msgbox2(\"Do you want to exit ? \",\"Attenti";
_answ = anywheresoftware.b4a.keywords.Common.Msgbox2(BA.ObjectToCharSequence("Do you want to exit ? "),BA.ObjectToCharSequence("Attention!"),"Yes","","No",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null),mostCurrent.activityBA);
 //BA.debugLineNum = 269;BA.debugLine="If Answ = DialogResponse.POSITIVE Then";
if (_answ==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
 //BA.debugLineNum = 270;BA.debugLine="If I.Ready Then I.Show Else I.LoadAd";
if (mostCurrent._vv4.getReady()) { 
mostCurrent._vv4.Show();}
else {
mostCurrent._vv4.LoadAd();};
 //BA.debugLineNum = 271;BA.debugLine="Return False";
if (true) return anywheresoftware.b4a.keywords.Common.False;
 };
 //BA.debugLineNum = 273;BA.debugLine="If Answ = DialogResponse.NEGATIVE Then";
if (_answ==anywheresoftware.b4a.keywords.Common.DialogResponse.NEGATIVE) { 
 //BA.debugLineNum = 274;BA.debugLine="If I.Ready Then I.Show Else I.LoadAd";
if (mostCurrent._vv4.getReady()) { 
mostCurrent._vv4.Show();}
else {
mostCurrent._vv4.LoadAd();};
 //BA.debugLineNum = 275;BA.debugLine="Return True";
if (true) return anywheresoftware.b4a.keywords.Common.True;
 };
 };
 //BA.debugLineNum = 278;BA.debugLine="End Sub";
return false;
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 284;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 286;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 280;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 282;BA.debugLine="End Sub";
return "";
}
public static String  _b_adscreendismissed() throws Exception{
 //BA.debugLineNum = 296;BA.debugLine="Sub B_AdScreenDismissed";
 //BA.debugLineNum = 297;BA.debugLine="Log(\"B Dismissed\")";
anywheresoftware.b4a.keywords.Common.Log("B Dismissed");
 //BA.debugLineNum = 298;BA.debugLine="End Sub";
return "";
}
public static String  _b_failedtoreceivead(String _errorcode) throws Exception{
 //BA.debugLineNum = 289;BA.debugLine="Sub B_FailedToReceiveAd (ErrorCode As String)";
 //BA.debugLineNum = 290;BA.debugLine="Log(\"B failed: \" & ErrorCode)";
anywheresoftware.b4a.keywords.Common.Log("B failed: "+_errorcode);
 //BA.debugLineNum = 291;BA.debugLine="End Sub";
return "";
}
public static String  _b_receivead() throws Exception{
 //BA.debugLineNum = 292;BA.debugLine="Sub B_ReceiveAd";
 //BA.debugLineNum = 293;BA.debugLine="Log(\"B received\")";
anywheresoftware.b4a.keywords.Common.Log("B received");
 //BA.debugLineNum = 294;BA.debugLine="End Sub";
return "";
}
public static String  _b1_click() throws Exception{
 //BA.debugLineNum = 109;BA.debugLine="Sub b1_Click";
 //BA.debugLineNum = 110;BA.debugLine="If I.Ready Then I.Show Else I.LoadAd";
if (mostCurrent._vv4.getReady()) { 
mostCurrent._vv4.Show();}
else {
mostCurrent._vv4.LoadAd();};
 //BA.debugLineNum = 111;BA.debugLine="cIcon.Show(\"image/*\",\"Choose Image\")";
_v7.Show(processBA,"image/*","Choose Image");
 //BA.debugLineNum = 112;BA.debugLine="End Sub";
return "";
}
public static String  _b2_click() throws Exception{
 //BA.debugLineNum = 128;BA.debugLine="Sub b2_Click";
 //BA.debugLineNum = 129;BA.debugLine="If I.Ready Then I.Show Else I.LoadAd";
if (mostCurrent._vv4.getReady()) { 
mostCurrent._vv4.Show();}
else {
mostCurrent._vv4.LoadAd();};
 //BA.debugLineNum = 130;BA.debugLine="cAPK.Show(\"*/*\",\"Choose Game APK\")";
_v6.Show(processBA,"*/*","Choose Game APK");
 //BA.debugLineNum = 131;BA.debugLine="End Sub";
return "";
}
public static String  _b3_click() throws Exception{
 //BA.debugLineNum = 152;BA.debugLine="Sub b3_Click";
 //BA.debugLineNum = 153;BA.debugLine="If I.Ready Then I.Show Else I.LoadAd";
if (mostCurrent._vv4.getReady()) { 
mostCurrent._vv4.Show();}
else {
mostCurrent._vv4.LoadAd();};
 //BA.debugLineNum = 154;BA.debugLine="cObb.Show(\"*/*\",\"Choose Game Obb!\")";
_v0.Show(processBA,"*/*","Choose Game Obb!");
 //BA.debugLineNum = 155;BA.debugLine="End Sub";
return "";
}
public static String  _b4_click() throws Exception{
 //BA.debugLineNum = 176;BA.debugLine="Sub b4_Click";
 //BA.debugLineNum = 177;BA.debugLine="If I.Ready Then I.Show Else I.LoadAd";
if (mostCurrent._vv4.getReady()) { 
mostCurrent._vv4.Show();}
else {
mostCurrent._vv4.LoadAd();};
 //BA.debugLineNum = 178;BA.debugLine="cData.Show(\"*/*\",\"Choose Game Obb!\")";
_vv1.Show(processBA,"*/*","Choose Game Obb!");
 //BA.debugLineNum = 179;BA.debugLine="End Sub";
return "";
}
public static String  _b5_click() throws Exception{
 //BA.debugLineNum = 200;BA.debugLine="Sub b5_Click";
 //BA.debugLineNum = 201;BA.debugLine="If I.Ready Then I.Show Else I.LoadAd";
if (mostCurrent._vv4.getReady()) { 
mostCurrent._vv4.Show();}
else {
mostCurrent._vv4.LoadAd();};
 //BA.debugLineNum = 202;BA.debugLine="If edName.Text = \"\" Then";
if ((mostCurrent._vvv1.getText()).equals("")) { 
 //BA.debugLineNum = 203;BA.debugLine="ToastMessageShow(\"Please Set App Name!\",True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Please Set App Name!"),anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 206;BA.debugLine="If File.Exists(File.DirRootExternal & \"/\"&tempFol";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/"+mostCurrent._vv7+"/res/drawable","icon.png")) { 
 //BA.debugLineNum = 208;BA.debugLine="If File.Exists(File.DirRootExternal & \"/\"&tempF";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/"+mostCurrent._vv7+"/assets","game.apk")) { 
 //BA.debugLineNum = 210;BA.debugLine="If edDevName.Text = \"\" Then";
if ((mostCurrent._vvv6.getText()).equals("")) { 
 }else {
 //BA.debugLineNum = 212;BA.debugLine="File.WriteString(File.DirRootExternal & \"/\"&t";
anywheresoftware.b4a.keywords.Common.File.WriteString(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/"+mostCurrent._vv7+"/assets","dev.txt",mostCurrent._vvv6.getText());
 };
 //BA.debugLineNum = 214;BA.debugLine="ProgressDialogShow(\"Building Your Apk!\" & CRLF";
anywheresoftware.b4a.keywords.Common.ProgressDialogShow(mostCurrent.activityBA,BA.ObjectToCharSequence("Building Your Apk!"+anywheresoftware.b4a.keywords.Common.CRLF+"Please Wait..."));
 //BA.debugLineNum = 215;BA.debugLine="T.Enabled = True";
_v5.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 217;BA.debugLine="ToastMessageShow(\"Please Choose Game APK File\"";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Please Choose Game APK File"),anywheresoftware.b4a.keywords.Common.True);
 };
 }else {
 //BA.debugLineNum = 221;BA.debugLine="ToastMessageShow(\"Please Choose Icon\",True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Please Choose Icon"),anywheresoftware.b4a.keywords.Common.True);
 };
 };
 //BA.debugLineNum = 225;BA.debugLine="End Sub";
return "";
}
public static String  _capk_result(boolean _success,String _dir,String _filename) throws Exception{
 //BA.debugLineNum = 133;BA.debugLine="Sub cAPK_Result (Success As Boolean, Dir As String";
 //BA.debugLineNum = 134;BA.debugLine="If Success Then";
if (_success) { 
 //BA.debugLineNum = 135;BA.debugLine="If FileName.EndsWith(\".apk\") Then";
if (_filename.endsWith(".apk")) { 
 //BA.debugLineNum = 137;BA.debugLine="If File.Exists(File.DirRootExternal & \"/\"&tempFo";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/"+mostCurrent._vv7+"/assets","game.apk")) { 
 //BA.debugLineNum = 138;BA.debugLine="File.Delete(File.DirRootExternal & \"/\"&tempFold";
anywheresoftware.b4a.keywords.Common.File.Delete(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/"+mostCurrent._vv7+"/assets","game.apk");
 };
 //BA.debugLineNum = 141;BA.debugLine="File.Copy(Dir, FileName, File.DirRootExternal";
anywheresoftware.b4a.keywords.Common.File.Copy(_dir,_filename,anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/"+mostCurrent._vv7+"/assets","game.apk");
 //BA.debugLineNum = 143;BA.debugLine="If File.Exists(File.DirRootExternal & \"/\"&tempF";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/"+mostCurrent._vv7+"/assets","game.apk")) { 
 //BA.debugLineNum = 144;BA.debugLine="ToastMessageShow(\"APK Saved!!\",False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("APK Saved!!"),anywheresoftware.b4a.keywords.Common.False);
 };
 }else {
 //BA.debugLineNum = 147;BA.debugLine="ToastMessageShow(\"Error! No APK File :(\",True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Error! No APK File :("),anywheresoftware.b4a.keywords.Common.True);
 };
 };
 //BA.debugLineNum = 150;BA.debugLine="End Sub";
return "";
}
public static String  _cdata_result(boolean _success,String _dir,String _filename) throws Exception{
 //BA.debugLineNum = 181;BA.debugLine="Sub cData_Result (Success As Boolean, Dir As Strin";
 //BA.debugLineNum = 182;BA.debugLine="If Success Then";
if (_success) { 
 //BA.debugLineNum = 183;BA.debugLine="If FileName.EndsWith(\".zip\") Then";
if (_filename.endsWith(".zip")) { 
 //BA.debugLineNum = 185;BA.debugLine="If File.Exists(File.DirRootExternal & \"/\"&tempFo";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/"+mostCurrent._vv7+"/assets","data.zip")) { 
 //BA.debugLineNum = 186;BA.debugLine="File.Delete(File.DirRootExternal & \"/\"&tempFold";
anywheresoftware.b4a.keywords.Common.File.Delete(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/"+mostCurrent._vv7+"/assets","data.zip");
 };
 //BA.debugLineNum = 189;BA.debugLine="File.Copy(Dir, FileName, File.DirRootExternal";
anywheresoftware.b4a.keywords.Common.File.Copy(_dir,_filename,anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/"+mostCurrent._vv7+"/assets","data.zip");
 //BA.debugLineNum = 191;BA.debugLine="If File.Exists(File.DirRootExternal & \"/\"&tempF";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/"+mostCurrent._vv7+"/assets","data.zip")) { 
 //BA.debugLineNum = 192;BA.debugLine="ToastMessageShow(\"DATA Saved!!\",False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("DATA Saved!!"),anywheresoftware.b4a.keywords.Common.False);
 };
 }else {
 //BA.debugLineNum = 195;BA.debugLine="ToastMessageShow(\"Error! No ZIP File :(\",True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Error! No ZIP File :("),anywheresoftware.b4a.keywords.Common.True);
 };
 };
 //BA.debugLineNum = 198;BA.debugLine="End Sub";
return "";
}
public static String  _cicon_result(boolean _success,String _dir,String _filename) throws Exception{
 //BA.debugLineNum = 114;BA.debugLine="Sub cIcon_Result (Success As Boolean, Dir As Strin";
 //BA.debugLineNum = 115;BA.debugLine="If Success Then";
if (_success) { 
 //BA.debugLineNum = 116;BA.debugLine="File.Delete(File.DirRootExternal & \"/\"&tempFold";
anywheresoftware.b4a.keywords.Common.File.Delete(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/"+mostCurrent._vv7+"/res/drawable","icon.png");
 //BA.debugLineNum = 117;BA.debugLine="File.Copy(Dir, FileName, File.DirRootExternal";
anywheresoftware.b4a.keywords.Common.File.Copy(_dir,_filename,anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/"+mostCurrent._vv7+"/res/drawable","icon.png");
 //BA.debugLineNum = 119;BA.debugLine="File.Delete(File.DirRootExternal & \"/\"&temp";
anywheresoftware.b4a.keywords.Common.File.Delete(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/"+mostCurrent._vv7+"/assets","icon.png");
 //BA.debugLineNum = 120;BA.debugLine="File.Copy(Dir, FileName,File.DirRootExternal & \"";
anywheresoftware.b4a.keywords.Common.File.Copy(_dir,_filename,anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/"+mostCurrent._vv7+"/assets","icon.png");
 //BA.debugLineNum = 122;BA.debugLine="If File.Exists(File.DirRootExternal&\"/\"&tempFol";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/"+mostCurrent._vv7+"/res/drawable","icon.png")) { 
 //BA.debugLineNum = 123;BA.debugLine="ToastMessageShow(\"Image Saved!!\",False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Image Saved!!"),anywheresoftware.b4a.keywords.Common.False);
 };
 };
 //BA.debugLineNum = 126;BA.debugLine="End Sub";
return "";
}
public static String  _cobb_result(boolean _success,String _dir,String _filename) throws Exception{
 //BA.debugLineNum = 157;BA.debugLine="Sub cObb_Result (Success As Boolean, Dir As String";
 //BA.debugLineNum = 158;BA.debugLine="If Success Then";
if (_success) { 
 //BA.debugLineNum = 159;BA.debugLine="If FileName.EndsWith(\".zip\") Then";
if (_filename.endsWith(".zip")) { 
 //BA.debugLineNum = 161;BA.debugLine="If File.Exists(File.DirRootExternal & \"/\"&tempFo";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/"+mostCurrent._vv7+"/assets","obb.zip")) { 
 //BA.debugLineNum = 162;BA.debugLine="File.Delete(File.DirRootExternal & \"/\"&tempFold";
anywheresoftware.b4a.keywords.Common.File.Delete(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/"+mostCurrent._vv7+"/assets","obb.zip");
 };
 //BA.debugLineNum = 165;BA.debugLine="File.Copy(Dir, FileName, File.DirRootExternal";
anywheresoftware.b4a.keywords.Common.File.Copy(_dir,_filename,anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/"+mostCurrent._vv7+"/assets","obb.zip");
 //BA.debugLineNum = 167;BA.debugLine="If File.Exists(File.DirRootExternal & \"/\"&tempF";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/"+mostCurrent._vv7+"/assets","obb.zip")) { 
 //BA.debugLineNum = 168;BA.debugLine="ToastMessageShow(\"OBB Saved!!\",False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("OBB Saved!!"),anywheresoftware.b4a.keywords.Common.False);
 };
 }else {
 //BA.debugLineNum = 171;BA.debugLine="ToastMessageShow(\"Error! No ZIP File :(\",True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Error! No ZIP File :("),anywheresoftware.b4a.keywords.Common.True);
 };
 };
 //BA.debugLineNum = 174;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 24;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 25;BA.debugLine="Dim edName,edDevName As EditText";
mostCurrent._vvv1 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._vvv6 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Dim b1,b2,b3,b4,b5 As Button";
mostCurrent._vvv2 = new anywheresoftware.b4a.objects.ButtonWrapper();
mostCurrent._vvv3 = new anywheresoftware.b4a.objects.ButtonWrapper();
mostCurrent._vvv4 = new anywheresoftware.b4a.objects.ButtonWrapper();
mostCurrent._vvv5 = new anywheresoftware.b4a.objects.ButtonWrapper();
mostCurrent._vvv7 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Dim lb As Label";
mostCurrent._vvv0 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 28;BA.debugLine="Dim apk,tempFolder As String";
mostCurrent._vv6 = "";
mostCurrent._vv7 = "";
 //BA.debugLineNum = 29;BA.debugLine="Dim zip As ABZipUnzip";
mostCurrent._vv0 = new com.AB.ABZipUnzip.ABZipUnzip();
 //BA.debugLineNum = 30;BA.debugLine="Dim zs As NNLZipSigner";
mostCurrent._vv5 = new nnl.zipsigner.NNLZipSigner();
 //BA.debugLineNum = 31;BA.debugLine="Dim ml As MLfiles";
mostCurrent._vvvv1 = new MLfiles.Fileslib.MLfiles();
 //BA.debugLineNum = 32;BA.debugLine="Dim B As AdView";
mostCurrent._vv3 = new anywheresoftware.b4a.admobwrapper.AdViewWrapper();
 //BA.debugLineNum = 33;BA.debugLine="Dim I As InterstitialAd";
mostCurrent._vv4 = new anywheresoftware.b4a.admobwrapper.AdViewWrapper.InterstitialAdWrapper();
 //BA.debugLineNum = 34;BA.debugLine="Dim h As Int";
_vv2 = 0;
 //BA.debugLineNum = 35;BA.debugLine="End Sub";
return "";
}
public static String  _i_adclosed() throws Exception{
 //BA.debugLineNum = 301;BA.debugLine="Sub I_AdClosed";
 //BA.debugLineNum = 302;BA.debugLine="I.LoadAd";
mostCurrent._vv4.LoadAd();
 //BA.debugLineNum = 303;BA.debugLine="End Sub";
return "";
}
public static String  _i_adopened() throws Exception{
 //BA.debugLineNum = 314;BA.debugLine="Sub I_adopened";
 //BA.debugLineNum = 315;BA.debugLine="Log(\"I Opened\")";
anywheresoftware.b4a.keywords.Common.Log("I Opened");
 //BA.debugLineNum = 316;BA.debugLine="End Sub";
return "";
}
public static String  _i_failedtoreceivead(String _errorcode) throws Exception{
 //BA.debugLineNum = 309;BA.debugLine="Sub I_FailedToReceiveAd (ErrorCode As String)";
 //BA.debugLineNum = 310;BA.debugLine="Log(\"I not Received - \" &\"Error Code: \"&ErrorCode";
anywheresoftware.b4a.keywords.Common.Log("I not Received - "+"Error Code: "+_errorcode);
 //BA.debugLineNum = 311;BA.debugLine="I.LoadAd";
mostCurrent._vv4.LoadAd();
 //BA.debugLineNum = 312;BA.debugLine="End Sub";
return "";
}
public static String  _i_receivead() throws Exception{
 //BA.debugLineNum = 305;BA.debugLine="Sub I_ReceiveAd";
 //BA.debugLineNum = 306;BA.debugLine="Log(\"I Received\")";
anywheresoftware.b4a.keywords.Common.Log("I Received");
 //BA.debugLineNum = 307;BA.debugLine="End Sub";
return "";
}
public static String  _lb_click() throws Exception{
anywheresoftware.b4a.objects.IntentWrapper _facebook = null;
 //BA.debugLineNum = 252;BA.debugLine="Sub lb_Click";
 //BA.debugLineNum = 253;BA.debugLine="If I.Ready Then I.Show Else I.LoadAd";
if (mostCurrent._vv4.getReady()) { 
mostCurrent._vv4.Show();}
else {
mostCurrent._vv4.LoadAd();};
 //BA.debugLineNum = 254;BA.debugLine="Try";
try { //BA.debugLineNum = 255;BA.debugLine="Dim Facebook As Intent";
_facebook = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 256;BA.debugLine="Facebook.Initialize(Facebook.ACTION_VIEW, \"fb://";
_facebook.Initialize(_facebook.ACTION_VIEW,"fb://profile/100011339710114");
 //BA.debugLineNum = 257;BA.debugLine="StartActivity(Facebook)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(_facebook.getObject()));
 } 
       catch (Exception e7) {
			processBA.setLastException(e7); //BA.debugLineNum = 259;BA.debugLine="Dim Facebook As Intent";
_facebook = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 260;BA.debugLine="Facebook.Initialize(Facebook.ACTION_VIEW, \"https";
_facebook.Initialize(_facebook.ACTION_VIEW,"https://m.facebook.com/KHtetzNaing");
 //BA.debugLineNum = 261;BA.debugLine="StartActivity(Facebook)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(_facebook.getObject()));
 };
 //BA.debugLineNum = 263;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
starter._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 19;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 20;BA.debugLine="Dim T As Timer";
_v5 = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 21;BA.debugLine="Dim cAPK,cIcon,cObb,cData As ContentChooser";
_v6 = new anywheresoftware.b4a.phone.Phone.ContentChooser();
_v7 = new anywheresoftware.b4a.phone.Phone.ContentChooser();
_v0 = new anywheresoftware.b4a.phone.Phone.ContentChooser();
_vv1 = new anywheresoftware.b4a.phone.Phone.ContentChooser();
 //BA.debugLineNum = 22;BA.debugLine="End Sub";
return "";
}
public static String  _t_tick() throws Exception{
String[] _arg = null;
nnl.apktools.NNLPackageChanger _pc = null;
anywheresoftware.b4a.objects.IntentWrapper _in = null;
 //BA.debugLineNum = 227;BA.debugLine="Sub T_Tick";
 //BA.debugLineNum = 228;BA.debugLine="Dim arg(3) As String";
_arg = new String[(int) (3)];
java.util.Arrays.fill(_arg,"");
 //BA.debugLineNum = 229;BA.debugLine="Dim pc As NNLPackageChanger";
_pc = new nnl.apktools.NNLPackageChanger();
 //BA.debugLineNum = 230;BA.debugLine="arg(0) = File.DirRootExternal & \"/\"&tempFolder&\"/";
_arg[(int) (0)] = anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/"+mostCurrent._vv7+"/AndroidManifest.xml";
 //BA.debugLineNum = 231;BA.debugLine="arg(1) = \"com.htetznaing.ginstaller\"";
_arg[(int) (1)] = "com.htetznaing.ginstaller";
 //BA.debugLineNum = 232;BA.debugLine="arg(2) = edName.Text";
_arg[(int) (2)] = mostCurrent._vvv1.getText();
 //BA.debugLineNum = 233;BA.debugLine="pc.Change(arg)";
_pc.Change(_arg);
 //BA.debugLineNum = 235;BA.debugLine="zip.ABZipDirectory(File.DirRootExternal & \"/\"&tem";
mostCurrent._vv0.ABZipDirectory(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/"+mostCurrent._vv7,anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/"+mostCurrent._vvv1.getText()+".apk");
 //BA.debugLineNum = 236;BA.debugLine="zs.SignZip(File.DirRootExternal & \"/\"&edName.Text";
mostCurrent._vv5.SignZip(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/"+mostCurrent._vvv1.getText()+".apk",anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/"+"SIGNED_"+mostCurrent._vvv1.getText()+".apk");
 //BA.debugLineNum = 238;BA.debugLine="ml.RootCmd(\"rm /sdcard/\"&edName.Text&\".apk\",\"\",Nu";
mostCurrent._vvvv1.RootCmd("rm /sdcard/"+mostCurrent._vvv1.getText()+".apk","",(java.lang.StringBuilder)(anywheresoftware.b4a.keywords.Common.Null),(java.lang.StringBuilder)(anywheresoftware.b4a.keywords.Common.Null),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 239;BA.debugLine="ml.RootCmd(\"rm -rf /sdcard/\"&tempFolder,\"\",Null,N";
mostCurrent._vvvv1.RootCmd("rm -rf /sdcard/"+mostCurrent._vv7,"",(java.lang.StringBuilder)(anywheresoftware.b4a.keywords.Common.Null),(java.lang.StringBuilder)(anywheresoftware.b4a.keywords.Common.Null),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 240;BA.debugLine="ProgressDialogHide";
anywheresoftware.b4a.keywords.Common.ProgressDialogHide();
 //BA.debugLineNum = 241;BA.debugLine="ToastMessageShow (\"Successfully Created \" & CRLF";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Successfully Created "+anywheresoftware.b4a.keywords.Common.CRLF+"SIGNED_"+mostCurrent._vvv1.getText()+".apk in SdCard!"),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 242;BA.debugLine="T.Enabled = False";
_v5.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 243;BA.debugLine="Dim in As Intent";
_in = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 244;BA.debugLine="in.Initialize(in.ACTION_VIEW,\"file:///\"&File.DirR";
_in.Initialize(_in.ACTION_VIEW,"file:///"+anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/"+"SIGNED_"+mostCurrent._vvv1.getText()+".apk");
 //BA.debugLineNum = 245;BA.debugLine="in.SetType(\"application/vnd.android.package-archi";
_in.SetType("application/vnd.android.package-archive");
 //BA.debugLineNum = 246;BA.debugLine="If in.IsInitialized Then";
if (_in.IsInitialized()) { 
 //BA.debugLineNum = 247;BA.debugLine="StartActivity(in)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(_in.getObject()));
 };
 //BA.debugLineNum = 249;BA.debugLine="T.Enabled=False";
_v5.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 250;BA.debugLine="End Sub";
return "";
}
}

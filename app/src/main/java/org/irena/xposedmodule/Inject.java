package org.irena.xposedmodule;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Inject extends XC_MethodHook implements IXposedHookLoadPackage, IXposedHookZygoteInit {

    private static final AtomicBoolean Inject = new AtomicBoolean();
    public static String ModulePath;
    public static XC_LoadPackage.LoadPackageParam loadParam;

    @Override
    public void initZygote(StartupParam startupParam){
        ModulePath = startupParam.modulePath;
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam){
        if (lpparam.isFirstApplication && !Inject.getAndSet(true)) {
            loadParam = lpparam;
            XposedBridge.hookMethod(getAtInject(), this);
            if (!lpparam.packageName.equals("com.target.app")) return;
            try {
                Class<?> clazz = XposedHelpers.findClass("com.target.app", lpparam.classLoader);

                XposedHelpers.findAndHookMethod(clazz, "methodName",/*some.class*/ new XC_MethodHook() {

                    @Override
                    protected void beforeHookedMethod(MethodHookParam param){
                        //your code here
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param){
                        //your code here
                    }

                });
            } catch (Exception e) {
                XposedBridge.log("Error hooking method: " + e.getMessage());
            }
        }
    }

    private Method getAtInject() {
        try {
            if (loadParam.appInfo.name != null) {

                Class<?> clz = loadParam.classLoader.loadClass(loadParam.appInfo.name);

                try {
                    return clz.getDeclaredMethod("attachBaseContext", Context.class);
                } catch (Throwable i) {
                    try {
                        return clz.getDeclaredMethod("onCreate");
                    } catch (Throwable e) {
                        try {
                            return Objects.requireNonNull(clz.getSuperclass()).getDeclaredMethod("attachBaseContext", Context.class);
                        } catch (Throwable m) {
                            return Objects.requireNonNull(clz.getSuperclass()).getDeclaredMethod("onCreate");
                        }
                    }
                }
            }
        } catch (Throwable o) {
            XposedBridge.log("[error]" + Log.getStackTraceString(o));
        }
        try {
            return ContextWrapper.class.getDeclaredMethod("attachBaseContext", Context.class);
        } catch (Throwable u) {
            XposedBridge.log("[error]" + Log.getStackTraceString(u));
            return null;
        }
    }
}

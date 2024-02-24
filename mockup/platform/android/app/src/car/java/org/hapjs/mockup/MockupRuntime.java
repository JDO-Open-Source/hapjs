/*
 * Copyright (c) 2023, the hapjs-platform Project Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hapjs.mockup;

import org.hapjs.PlatformRuntime;
import org.hapjs.bridge.annotation.DependencyAnnotation;
import org.hapjs.cache.CacheException;
import org.hapjs.cache.CacheStorage;
import org.hapjs.common.utils.FileUtils;
import org.hapjs.distribution.DistributionProvider;
import org.hapjs.logging.LogProvider;
import org.hapjs.mockup.impl.CanvasProviderImpl;
import org.hapjs.mockup.impl.DistributionProviderImpl;
import org.hapjs.mockup.impl.LogProviderImpl;
import org.hapjs.net.DefaultNetLoaderProviderImpl;
import org.hapjs.net.NetLoaderProvider;
import org.hapjs.render.cutout.CutoutProvider;
import org.hapjs.render.cutout.DefaultCutoutProvider;
import org.hapjs.runtime.ProviderManager;
import org.hapjs.widgets.canvas.CanvasProvider;
import org.hapjs.widgets.video.manager.DefaultPlayerManagerProviderImpl;
import org.hapjs.widgets.video.manager.PlayerManagerProvider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@DependencyAnnotation(key = MockupRuntime.PROPERTY_RUNTIME_IMPL_CLASS)
public class MockupRuntime extends PlatformRuntime {
    public static final String APP_ID1 = "com.quickapp.center.carplay";
    public static final String APP_ID2 = "com.quick.babysong.car.auto";
    public static final String APP_ID3 = "com.cwz12123.wzcx";
    public static final String APP_ID4 = "com.application.demo.vui";
    public static final String APP_ID5 = "com.taobao.movie.hwauto.release";
    public static final String APP_ID6 = "com.tuhu.lightapp";
    public static final String APP_ID7 = "com.uxin.quickapp";
    public static final String APP_ID8 = "com.xiao.aiot.soundbox_template.mi";
    public static final String APP_ID9 = "org.hapjs.demo.extension";
    public static final String APP_ID10 = "org.hapjs.demo.sample2";
    public static final String APP_ID11 = "com.cwz12123.wzcx";
    public static final String APP_ID12 = "org.quickapp.union.sample.debug.1.9.0";
    public static final String taobao = "com.taobao.movie.hwauto.release";
    @Override
    protected void onAllProcessInit() {
        super.onAllProcessInit();
        ProviderManager pm = ProviderManager.getDefault();
        pm.addProvider(DistributionProvider.NAME, new DistributionProviderImpl(mContext));
        pm.addProvider(LogProvider.NAME, new LogProviderImpl());
        pm.addProvider(CanvasProvider.NAME, new CanvasProviderImpl());
        pm.addProvider(CutoutProvider.NAME, new DefaultCutoutProvider());
        pm.addProvider(NetLoaderProvider.NAME, new DefaultNetLoaderProviderImpl());
        pm.addProvider(PlayerManagerProvider.NAME, new DefaultPlayerManagerProviderImpl());
    }

    @Override
    protected void onMainProcessInit() {
        super.onMainProcessInit();
        installApp(APP_ID1);
        installApp(APP_ID2);
        installApp(APP_ID3);
        installApp(APP_ID4);
        installApp(APP_ID5);
        installApp(APP_ID6);
        installApp(APP_ID7);
        installApp(APP_ID8);
        installApp(APP_ID9);
        installApp(APP_ID10);
        installApp(APP_ID11);
        installApp(APP_ID12);
        installApp(taobao);
    }

    private void installApp(String appId) {
        // 检查快应用是否已安装，未安装时会尝试安装
        if (!CacheStorage.getInstance(mContext).hasCache(appId)) {
            //force update rpk
            InputStream in = null;
            try {
                // 从assets中读取快应用，也可以通过网络等方式获取快应用
                in = mContext.getAssets().open(appId + ".rpk");
                File rpk = new File(mContext.getCacheDir(), appId + ".rpk");
                FileUtils.saveToFile(in, rpk);
                // 安装快应用
                CacheStorage.getInstance(mContext).install(appId, rpk.getAbsolutePath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (CacheException e) {
                throw new RuntimeException(e);
            } finally {
                FileUtils.closeQuietly(in);
            }
        }
    }
}

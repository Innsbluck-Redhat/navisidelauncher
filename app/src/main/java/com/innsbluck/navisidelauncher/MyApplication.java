package com.innsbluck.navisidelauncher;

import android.app.Application;
import android.content.Context;
import com.innsbluck.navisidelauncher.preference.ActionsPref;
import com.innsbluck.navisidelauncher.preference.SettingPref;
import com.os.operando.garum.Configuration;
import com.os.operando.garum.Garum;
import com.os.operando.garum.models.PrefModel;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Context context = getApplicationContext();
        Configuration.Builder builder = new Configuration.Builder(context);
        builder.setModelClasses(ActionsPref.class, SettingPref.class, PrefModel.class);
        Garum.initialize(builder.create());
    }
}

package com.innsbluck.navisidelauncher.preference;

import com.os.operando.garum.annotations.Pref;
import com.os.operando.garum.annotations.PrefKey;
import com.os.operando.garum.models.PrefModel;

@Pref(name = "pref_setting")
public class SettingPref extends PrefModel {
    @PrefKey
    public Boolean isStackFromBottom;
}
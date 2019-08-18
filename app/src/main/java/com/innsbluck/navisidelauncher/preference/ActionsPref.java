package com.innsbluck.navisidelauncher.preference;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.innsbluck.navisidelauncher.data.Action;
import com.os.operando.garum.annotations.Pref;
import com.os.operando.garum.annotations.PrefKey;
import com.os.operando.garum.models.PrefModel;

import java.lang.reflect.Type;
import java.util.ArrayList;

@Pref(name = "actions_pref")
public class ActionsPref extends PrefModel {
    @PrefKey
    private String actions /*= new ArrayList<Action>() {{
        add(new Action("BUILDING", ""));
        add(new Action("NAVI", ""));
        add(new Action("IDE", ""));
    }}*/;

    public void setActions(ArrayList<Action> actions) {
        Type type = new TypeToken<ArrayList<Action>>() {
        }.getType();
        this.actions = new Gson().toJson(actions, type);
        Log.d("aorijgoajeriuagh", this.actions);
    }

    public ArrayList<Action> getActions() {
        Type type = new TypeToken<ArrayList<Action>>() {
        }.getType();
        return new Gson().fromJson(actions, type);
    }

    public void addAction(Action action) {
        ArrayList<Action> actions = getActions();
        actions.add(action);
        setActions(actions);
    }
}

package com.proyecto.travelia.ui.navigation;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.proyecto.travelia.ConfirmarReservaActivity;
import com.proyecto.travelia.ExplorarActivity;
import com.proyecto.travelia.InicioActivity;
import com.proyecto.travelia.favoritos.FavoritosActivity;
import com.proyecto.travelia.ui.BottomNavView;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Implementación simple basada en Activities. Centraliza la navegación para el BottomNav.
 */
public class ActivityBottomNavController implements BottomNavNavigationController {

    private final Activity activity;
    private final Map<BottomNavView.Tab, Supplier<Intent>> destinations = new EnumMap<>(BottomNavView.Tab.class);
    private final MutableLiveData<BottomNavView.Tab> currentTab = new MutableLiveData<>(BottomNavView.Tab.HOME);
    private boolean finishOnNavigate = true;

    public ActivityBottomNavController(@NonNull Activity activity) {
        this.activity = activity;
        destinations.put(BottomNavView.Tab.HOME, () -> new Intent(activity, InicioActivity.class));
        destinations.put(BottomNavView.Tab.EXPLORAR, () -> new Intent(activity, ExplorarActivity.class));
        destinations.put(BottomNavView.Tab.FAVORITES, () -> new Intent(activity, FavoritosActivity.class));
        destinations.put(BottomNavView.Tab.RESERVE, () -> new Intent(activity, ConfirmarReservaActivity.class));
    }

    public void setFinishOnNavigate(boolean finishOnNavigate) {
        this.finishOnNavigate = finishOnNavigate;
    }

    @NonNull
    @Override
    public LiveData<BottomNavView.Tab> getCurrentTab() {
        return currentTab;
    }

    @Override
    public void navigateTo(@NonNull BottomNavView.Tab tab) {
        BottomNavView.Tab selected = currentTab.getValue();
        if (selected == tab) {
            return;
        }
        currentTab.setValue(tab);
        Supplier<Intent> supplier = destinations.get(tab);
        if (supplier == null) {
            return;
        }
        Intent intent = supplier.get();
        activity.startActivity(intent);
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        if (finishOnNavigate) {
            activity.finish();
        }
    }

    @Override
    public void setCurrentTab(@NonNull BottomNavView.Tab tab) {
        currentTab.setValue(tab);
    }
}

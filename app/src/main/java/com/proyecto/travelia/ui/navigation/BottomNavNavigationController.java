package com.proyecto.travelia.ui.navigation;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.proyecto.travelia.ui.BottomNavView;

/**
 * Contrato mínimo para que {@link com.proyecto.travelia.ui.BottomNavView} se conecte a una
 * fuente de navegación compartida (NavController propio o Jetpack Navigation).
 */
public interface BottomNavNavigationController {

    /** LiveData expuesto para resaltar automáticamente la pestaña activa. */
    @NonNull
    LiveData<BottomNavView.Tab> getCurrentTab();

    /** Navega a la pestaña solicitada. */
    void navigateTo(@NonNull BottomNavView.Tab tab);

    /**
     * Permite informar al controlador sobre el tab actual (por ejemplo al recrear una Activity).
     */
    void setCurrentTab(@NonNull BottomNavView.Tab tab);
}

package com.proyecto.travelia.ui.navigation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.proyecto.travelia.data.FavoritesRepository;
import com.proyecto.travelia.data.ReservationsRepository;
import com.proyecto.travelia.ui.BottomNavView;

/**
 * Helper para inicializar el componente BottomNav de manera consistente.
 */
public final class BottomNavComponent {

    private BottomNavComponent() { }

    @Nullable
    public static BottomNavNavigationController bind(@NonNull AppCompatActivity activity,
                                                     @Nullable BottomNavView bottomNavView,
                                                     @NonNull BottomNavView.Tab currentTab,
                                                     @Nullable FavoritesRepository favoritesRepository,
                                                     @Nullable ReservationsRepository reservationsRepository) {
        if (bottomNavView == null) {
            return null;
        }
        ActivityBottomNavController controller = new ActivityBottomNavController(activity);
        controller.setFinishOnNavigate(bottomNavView.shouldFinishOnNavigate());
        controller.setCurrentTab(currentTab);
        bottomNavView.bindToNavController(activity, controller);
        bottomNavView.bindBadgeCounters(activity, favoritesRepository, reservationsRepository);
        return controller;
    }
}

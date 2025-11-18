package com.proyecto.travelia.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LifecycleOwner;

import com.proyecto.travelia.R;
import com.proyecto.travelia.data.FavoritesRepository;
import com.proyecto.travelia.data.ReservationsRepository;
import com.proyecto.travelia.ui.navigation.BottomNavNavigationController;

import java.util.EnumMap;
import java.util.Map;

public class BottomNavView extends CardView {

    public enum Tab { HOME, EXPLORAR, ADD, FAVORITES, RESERVE }

    public interface OnTabSelectedListener {
        void onTabSelected(Tab tab);
    }

    private final Map<Tab, View> tabViews = new EnumMap<>(Tab.class);
    private final Map<Tab, TextView> badgeViews = new EnumMap<>(Tab.class);
    private View navAdd;
    private boolean finishOnNavigate = true;
    private Tab currentTab = Tab.HOME;
    private OnTabSelectedListener tabSelectedListener;
    private OnClickListener addClickListener;

    public BottomNavView(Context context) { super(context); init(context, null); }
    public BottomNavView(Context context, @Nullable AttributeSet attrs) { super(context, attrs); init(context, attrs); }
    public BottomNavView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); init(context, attrs); }

    private void init(Context ctx, @Nullable AttributeSet attrs) {
        LayoutInflater.from(ctx).inflate(R.layout.view_bottom_nav, this, true);

        View navHome = findViewById(R.id.nav_home);
        View navExplorar = findViewById(R.id.nav_explorar);
        navAdd = findViewById(R.id.nav_add);
        View navFavorites = findViewById(R.id.nav_favorites);
        View navReserve = findViewById(R.id.nav_reserve);

        tabViews.put(Tab.HOME, navHome);
        tabViews.put(Tab.EXPLORAR, navExplorar);
        tabViews.put(Tab.ADD, navAdd);
        tabViews.put(Tab.FAVORITES, navFavorites);
        tabViews.put(Tab.RESERVE, navReserve);

        badgeViews.put(Tab.HOME, findViewById(R.id.badge_home));
        badgeViews.put(Tab.EXPLORAR, findViewById(R.id.badge_explorar));
        badgeViews.put(Tab.ADD, findViewById(R.id.badge_add));
        badgeViews.put(Tab.FAVORITES, findViewById(R.id.badge_favorites));
        badgeViews.put(Tab.RESERVE, findViewById(R.id.badge_reserve));

        // Leer attrs
        if (attrs != null) {
            final int[] set = { R.attr.currentTab, R.attr.finishOnNavigate };
            final android.content.res.TypedArray a =
                    ctx.obtainStyledAttributes(attrs, R.styleable.BottomNavView);
            int tabIndex = a.getInt(R.styleable.BottomNavView_currentTab, 0);
            finishOnNavigate = a.getBoolean(R.styleable.BottomNavView_finishOnNavigate, true);
            a.recycle();
            setCurrentTab(Tab.values()[tabIndex]);
        }

        // Inset bottom (IME + system bars)
        ViewCompat.setOnApplyWindowInsetsListener(this, (v, insets) -> {
            Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            if (lp != null) {
                lp.bottomMargin = dp(10) + sys.bottom;
                v.setLayoutParams(lp);
            }
            return insets;
        });
        setupTabClicks();
    }

    private void setupTabClicks() {
        findViewById(R.id.nav_home).setOnClickListener(v -> dispatchTabSelected(Tab.HOME));
        findViewById(R.id.nav_explorar).setOnClickListener(v -> dispatchTabSelected(Tab.EXPLORAR));
        navAdd.setOnClickListener(v -> {
            if (addClickListener != null) {
                addClickListener.onClick(v);
            } else {
                dispatchTabSelected(Tab.ADD);
            }
        });
        findViewById(R.id.nav_favorites).setOnClickListener(v -> dispatchTabSelected(Tab.FAVORITES));
        findViewById(R.id.nav_reserve).setOnClickListener(v -> dispatchTabSelected(Tab.RESERVE));
    }

    private void dispatchTabSelected(Tab tab) {
        if (tabSelectedListener != null) {
            tabSelectedListener.onTabSelected(tab);
        }
    }

    public void highlight(Tab tab) {
        float active = 1f, inactive = 0.6f;
        for (Map.Entry<Tab, View> entry : tabViews.entrySet()) {
            View view = entry.getValue();
            if (view == null) continue;
            view.setAlpha(entry.getKey() == tab ? active : inactive);
        }
    }

    public void setCurrentTab(Tab tab) {
        currentTab = tab;
        highlight(tab);
    }

    public Tab getCurrentTab() {
        return currentTab;
    }

    public void setFinishOnNavigate(boolean finish) { this.finishOnNavigate = finish; }

    public boolean shouldFinishOnNavigate() { return finishOnNavigate; }

    // Permite sobrescribir la acción del botón Add si luego quieres algo especial
    public void setOnAddClickListener(@Nullable OnClickListener l) {
        this.addClickListener = l;
    }

    public void setOnTabSelectedListener(@Nullable OnTabSelectedListener listener) {
        this.tabSelectedListener = listener;
    }

    public void bindToNavController(@NonNull LifecycleOwner owner,
                                     @NonNull BottomNavNavigationController controller) {
        setOnTabSelectedListener(controller::navigateTo);
        controller.getCurrentTab().observe(owner, this::setCurrentTab);
    }

    public void bindBadgeCounters(@NonNull LifecycleOwner owner,
                                  @Nullable FavoritesRepository favoritesRepository,
                                  @Nullable ReservationsRepository reservationsRepository) {
        if (favoritesRepository != null) {
            favoritesRepository.observeAll().observe(owner, list ->
                    setBadgeCount(Tab.FAVORITES, list == null ? 0 : list.size()));
        }
        if (reservationsRepository != null) {
            reservationsRepository.observeAll().observe(owner, list ->
                    setBadgeCount(Tab.RESERVE, list == null ? 0 : list.size()));
        }
    }

    public void setBadgeCount(Tab tab, int count) {
        TextView badge = badgeViews.get(tab);
        if (badge == null) return;
        if (count <= 0) {
            badge.setVisibility(View.GONE);
        } else {
            badge.setVisibility(View.VISIBLE);
            badge.setText(count > 99 ? "99+" : String.valueOf(count));
        }
    }

    private int dp(int value) {
        float d = getResources().getDisplayMetrics().density;
        return Math.round(value * d);
    }
}

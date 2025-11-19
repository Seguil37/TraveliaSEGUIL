package com.proyecto.travelia.ui;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.proyecto.travelia.ConfirmarReservaActivity;
import com.proyecto.travelia.ExplorarActivity;
import com.proyecto.travelia.InicioActivity;
import com.proyecto.travelia.PerfilActivity;
import com.proyecto.travelia.R;
import com.proyecto.travelia.favoritos.FavoritosActivity;

public class BottomNavView extends CardView {

    public enum Tab { HOME, EXPLORAR, FAVORITES, RESERVE, PROFILE }

    private View navHome, navExplorar, navFavorites, navReserve, navProfile;
    private View iconHome, iconExplorar, iconFav, iconReserve, iconProfile;
    private View textHome, textExplorar, textFav, textReserve, textProfile;
    private View badgeFavorites, badgeReserve;
    private boolean finishOnNavigate = true;

    public BottomNavView(Context context) { super(context); init(context, null); }
    public BottomNavView(Context context, @Nullable AttributeSet attrs) { super(context, attrs); init(context, attrs); }
    public BottomNavView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); init(context, attrs); }

    private void init(Context ctx, @Nullable AttributeSet attrs) {
        LayoutInflater.from(ctx).inflate(R.layout.view_bottom_nav, this, true);

        navHome      = findViewById(R.id.nav_home);
        navExplorar  = findViewById(R.id.nav_explorar);
        navFavorites = findViewById(R.id.nav_favorites);
        navReserve   = findViewById(R.id.nav_reserve);
        navProfile   = findViewById(R.id.nav_profile);

        iconHome = findViewById(R.id.icon_home);
        iconExplorar = findViewById(R.id.icon_explorar);
        iconFav = findViewById(R.id.icon_fav);
        iconReserve = findViewById(R.id.icon_reserve);
        iconProfile = findViewById(R.id.icon_profile);

        textHome = findViewById(R.id.text_home);
        textExplorar = findViewById(R.id.text_explorar);
        textFav = findViewById(R.id.text_fav);
        textReserve = findViewById(R.id.text_reserve);
        textProfile = findViewById(R.id.text_profile);

        badgeFavorites = findViewById(R.id.badge_favorites);
        badgeReserve = findViewById(R.id.badge_reserve);

        // Leer attrs
        if (attrs != null) {
            final int[] set = { R.attr.currentTab, R.attr.finishOnNavigate };
            final android.content.res.TypedArray a =
                    ctx.obtainStyledAttributes(attrs, R.styleable.BottomNavView);
            int tabIndex = a.getInt(R.styleable.BottomNavView_currentTab, 0);
            finishOnNavigate = a.getBoolean(R.styleable.BottomNavView_finishOnNavigate, true);
            a.recycle();
            Tab[] values = Tab.values();
            if (tabIndex >= 0 && tabIndex < values.length) {
                highlight(values[tabIndex]);
            }
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

        wireDefaultNavigation(ctx);
    }

    private void wireDefaultNavigation(Context ctx) {
        navHome.setOnClickListener(v -> navigate(ctx, InicioActivity.class));
        navExplorar.setOnClickListener(v -> navigate(ctx, ExplorarActivity.class));
        navFavorites.setOnClickListener(v -> navigate(ctx, com.proyecto.travelia.favoritos.FavoritosActivity.class));
        navReserve.setOnClickListener(v -> navigate(ctx, ConfirmarReservaActivity.class));
        navProfile.setOnClickListener(v -> navigate(ctx, PerfilActivity.class));
    }

    private void navigate(Context ctx, Class<?> target) {
        if (ctx.getClass() == target) return; // ya estás en esa Activity
        ctx.startActivity(new Intent(ctx, target));
        if (finishOnNavigate && ctx instanceof android.app.Activity) {
            ((android.app.Activity) ctx).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            ((android.app.Activity) ctx).finish();
        }
    }

    public void highlight(Tab tab) {
        setSelected(navHome, iconHome, textHome, tab == Tab.HOME);
        setSelected(navExplorar, iconExplorar, textExplorar, tab == Tab.EXPLORAR);
        setSelected(navFavorites, iconFav, textFav, tab == Tab.FAVORITES);
        setSelected(navReserve, iconReserve, textReserve, tab == Tab.RESERVE);
        setSelected(navProfile, iconProfile, textProfile, tab == Tab.PROFILE);
    }

    public void setFinishOnNavigate(boolean finish) { this.finishOnNavigate = finish; }

    public void setFavoritesBadge(int count) {
        updateBadge(badgeFavorites, count);
    }

    public void setReserveBadge(int count) {
        updateBadge(badgeReserve, count);
    }

    private void updateBadge(View badgeView, int count) {
        if (!(badgeView instanceof android.widget.TextView)) return;
        android.widget.TextView tv = (android.widget.TextView) badgeView;
        if (count <= 0) {
            tv.setVisibility(View.GONE);
        } else {
            tv.setVisibility(View.VISIBLE);
            tv.setText(String.valueOf(Math.min(count, 99)));
        }
    }

    private void setSelected(View container, View icon, View text, boolean selected) {
        container.setSelected(selected);
        if (icon != null) icon.setSelected(selected);
        if (text != null) text.setSelected(selected);
    }

    private int dp(int value) {
        float d = getResources().getDisplayMetrics().density;
        return Math.round(value * d);
    }
}

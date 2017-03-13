package com.vendomatica.vendroid;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.app.OnNavigationBlockedListener;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;
import com.heinrichreimersoftware.materialintro.slide.Slide;

public class MainIntroActivity extends IntroActivity {

    public static final String EXTRA_FULLSCREEN = "com.vendomatica.vendo.demo.EXTRA_FULLSCREEN";
    public static final String EXTRA_SCROLLABLE = "com.vendomatica.vendo.demo.EXTRA_SCROLLABLE";
    public static final String EXTRA_CUSTOM_FRAGMENTS = "com.vendomatica.vendo.demo.EXTRA_CUSTOM_FRAGMENTS";
    public static final String EXTRA_PERMISSIONS = "com.vendomatica.vendo.demo.EXTRA_PERMISSIONS";
    public static final String EXTRA_SHOW_BACK = "com.vendomatica.vendo.demo.EXTRA_SHOW_BACK";
    public static final String EXTRA_SHOW_NEXT = "com.vendomatica.vendo.demo.EXTRA_SHOW_NEXT";
    public static final String EXTRA_SKIP_ENABLED = "com.vendomatica.vendo.demo.EXTRA_SKIP_ENABLED";
    public static final String EXTRA_FINISH_ENABLED = "com.vendomatica.vendo.demo.EXTRA_FINISH_ENABLED";
    public static final String EXTRA_GET_STARTED_ENABLED = "com.vendomatica.vendo.demo.EXTRA_GET_STARTED_ENABLED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();

        boolean fullscreen = intent.getBooleanExtra(EXTRA_FULLSCREEN, true);
        boolean scrollable = intent.getBooleanExtra(EXTRA_SCROLLABLE, true);
        boolean customFragments = intent.getBooleanExtra(EXTRA_CUSTOM_FRAGMENTS, true);
        boolean permissions = intent.getBooleanExtra(EXTRA_PERMISSIONS, true);
        boolean showBack = intent.getBooleanExtra(EXTRA_SHOW_BACK, true);
        boolean showNext = intent.getBooleanExtra(EXTRA_SHOW_NEXT, true);
        boolean skipEnabled = intent.getBooleanExtra(EXTRA_SKIP_ENABLED, true);
        boolean finishEnabled = intent.getBooleanExtra(EXTRA_FINISH_ENABLED, false);
        boolean getStartedEnabled = intent.getBooleanExtra(EXTRA_GET_STARTED_ENABLED, false);

        setFullscreen(fullscreen);

        super.onCreate(savedInstanceState);

        setButtonBackFunction(skipEnabled ? BUTTON_BACK_FUNCTION_SKIP : BUTTON_BACK_FUNCTION_BACK);
        setButtonNextFunction(finishEnabled ? BUTTON_NEXT_FUNCTION_NEXT_FINISH : BUTTON_NEXT_FUNCTION_NEXT);
        setButtonBackVisible(showBack);
        setButtonNextVisible(showNext);
        setButtonCtaVisible(getStartedEnabled);
        setButtonCtaTintMode(BUTTON_CTA_TINT_MODE_TEXT);

        addSlide(new SimpleSlide.Builder()
                .title(R.string.title_intro)
                .description(R.string.description_intro)
                .image(R.drawable.art_material_metaphor)
                .background(R.color.color_vendomatica)
                .backgroundDark(R.color.color_vendomatica_dark)
                .scrollable(scrollable)
                .build());

//        addSlide(new SimpleSlide.Builder()
//                .title(R.string.title_material_bold)
//                .description(R.string.description_material_bold)
//                .image(R.drawable.art_material_bold)
//                .background(R.color.color_material_bold)
//                .backgroundDark(R.color.color_dark_material_bold)
//                .scrollable(scrollable)
//                .buttonCtaLabel("Hello")
//                .buttonCtaClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Toast toast = Toast.makeText(MainIntroActivity.this, R.string.toast_button_cta, Toast.LENGTH_SHORT);
//                        toast.setGravity(Gravity.CENTER, 0, 0);
//                        toast.show();
//
//                        nextSlide();
//                    }
//                })
//                .build());
//
//        addSlide(new SimpleSlide.Builder()
//                .title(R.string.title_material_motion)
//                .description(R.string.description_material_motion)
//                .image(R.drawable.art_material_motion)
//                .background(R.color.color_material_motion)
//                .backgroundDark(R.color.color_dark_material_motion)
//                .scrollable(scrollable)
//                .build());
//
//        addSlide(new SimpleSlide.Builder()
//                .title(R.string.title_material_shadow)
//                .description(R.string.description_material_shadow)
//                .image(R.drawable.art_material_shadow)
//                .background(R.color.color_material_shadow)
//                .backgroundDark(R.color.color_dark_material_shadow)
//                .scrollable(scrollable)
//                .build());

        final Slide permissionsSlide;
        if (permissions) {
            permissionsSlide = new SimpleSlide.Builder()
                    .title(R.string.title_permissions)
                    .description(R.string.description_permissions)
                    .background(R.color.color_permissions)
                    .backgroundDark(R.color.color_dark_permissions)
                    .scrollable(scrollable)
                    .permissions(new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.BLUETOOTH,Manifest.permission.BLUETOOTH_ADMIN,
                            Manifest.permission.INTERNET,Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION})
                    .build();
            addSlide(permissionsSlide);
        } else {
            permissionsSlide = null;
        }

        final Slide loginSlide;
        if (customFragments) {
            loginSlide = new FragmentSlide.Builder()
                    .background(R.color.color_vendomatica)
                    .backgroundDark(R.color.color_vendomatica_dark)
                    .fragment(LoginFragment.newInstance())
                    .build();
            addSlide(loginSlide);

//            addSlide(new FragmentSlide.Builder()
//                    .background(R.color.color_vendomatica)
//                    .backgroundDark(R.color.color_vendomatica_dark)
//                    .fragment(R.layout.fragment_custom, R.style.AppThemeDark)
//                    .build());
        } else {
            loginSlide = null;
        }

        //Feel free to add a navigation policy to define when users can go forward/backward
        /*
        setNavigationPolicy(new NavigationPolicy() {
            @Override
            public boolean canGoForward(int position) {
                return true;
            }

            @Override
            public boolean canGoBackward(int position) {
                return true;
            }
        });
        */

        addOnNavigationBlockedListener(new OnNavigationBlockedListener() {
            @Override
            public void onNavigationBlocked(int position, int direction) {
                View contentView = findViewById(android.R.id.content);
                if (contentView != null) {
                    Slide slide = getSlide(position);

                    if (slide == permissionsSlide) {
                        Snackbar.make(contentView, R.string.label_grant_permissions, Snackbar.LENGTH_LONG).show();
                    } else if (slide == loginSlide) {
                        Snackbar.make(contentView, R.string.label_fill_out_form, Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        });

        //Feel free to add and remove page change listeners
        /*
        addOnPageChangeListener(new ViewPager.OnPageChangeListener(){
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        */
    }

}

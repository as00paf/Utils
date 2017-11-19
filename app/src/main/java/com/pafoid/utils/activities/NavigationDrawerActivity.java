package com.pafoid.utils.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pafoid.utils.R;
import com.pafoid.utils.transform.CircleTransform;
import com.pafoid.utils.utils.AppConstants;
import com.pafoid.utils.utils.ResourceUtils;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Abstract Activity that contains a NavigationDrawer
 *
 * Extend this Activity and use it in your project paired with {@link com.pafoid.utils.adapters.DefaultStatePagerAdapter}
 * Create the nav_drawer.xml menu file to define your Navigation Drawer menu
 */
public abstract class NavigationDrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static String TAG = "NavDrawerActivity";

    //View
    protected Toolbar toolbar;
    protected DrawerLayout drawerLayout;
    protected ActionBarDrawerToggle drawerToggle;
    protected NavigationView navigationView;
    protected View content;
    protected ArrayList<Fragment> fragments;

    //Options
    protected boolean useAccount = false;

    //Data
    protected ArrayList<NavigationDrawerSection> sections;
    protected int lastSectionIndex = 0;
    private boolean isMenuDirty = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);

        initToolbar();
        setupDrawerLayout();
        sections = initSections();
        if(useAccount) initAccount();
        onNavigationItemSelected(navigationView.getMenu().getItem(0));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isMenuDirty) navigationView.getMenu().getItem(lastSectionIndex).setChecked(true);
    }

    protected void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
        }
    }

    protected void setupDrawerLayout() {
        content = findViewById(R.id.content);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawerToggle = new ActionBarDrawerToggle(
                this,  drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(drawerToggle);

        drawerToggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getMenu().getItem(0).setChecked(true);
        navigationView.getMenu().getItem(navigationView.getMenu().size() - 1).setChecked(false);

        updateNavigationDrawerCounters();
        showNavigationDrawerIfFirstTime();
    }

    private void showNavigationDrawerIfFirstTime() {
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {

                SharedPreferences preferences = getSharedPreferences("com.nemator.needle", Context.MODE_PRIVATE);
                boolean isFirstStart = preferences.getBoolean(AppConstants.FIRST_LAUNCH, true);

                if(isFirstStart) {
                    drawerLayout.openDrawer(findViewById(R.id.navigation_drawer_container));
                    SharedPreferences.Editor e = preferences.edit();
                    e.putBoolean(AppConstants.FIRST_LAUNCH, false);
                    e.commit();
                }
            }
        });

        t.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return false;
    }

    public void closeDrawers(){
        drawerLayout.closeDrawers();
    }

    //Navigation
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item){
        int itemId = item.getItemId();
        NavigationDrawerSection section = getSectionById(itemId);
        int index = sections.indexOf(section);
        if(section == null) throw new Error("This section id is not associated");
        Fragment newFragment = null;
        int containerViewId = content.getId();
        Boolean add = containerViewId == 0;

        switch (section.getType()){
            case NavigationDrawerSection.Type.ACTIVITY:
                Intent intent = new Intent(this, section.getViewType());
                startActivity(intent);
                isMenuDirty = true;
                return true;
            case NavigationDrawerSection.Type.FRAGMENT:
                newFragment = instantiateFragment(section);
                toolbar.setTitle(section.getTitle());
                lastSectionIndex = index;
                break;
            case NavigationDrawerSection.Type.ACTION:
                if(section.getDelegate() != null){
                    section.getDelegate().onSectionSelected(section.getMenuItemId());
                }else{
                    throw new Error("Section with title " + section.getTitle() + " does not have a delegate");
                }
                break;
        }

        if(newFragment != null){
            if(add){
                getSupportFragmentManager().beginTransaction()
                        .add(containerViewId, newFragment)
                        .commit();
            }else{
                getSupportFragmentManager().beginTransaction()
                        //.setCustomAnimations(enterAnimation, exitAnimation)
                        .replace(containerViewId, newFragment)
                        .commit();

                drawerLayout.closeDrawers();
            }

            return true;
        }

        return false;
    }

    private Fragment instantiateFragment(NavigationDrawerSection section){
        if(fragments != null && fragments.size() > 0){
            for (Fragment fragment : fragments) {
                if(fragment.getClass() == section.getViewType()) return fragment;
            }
        }else{
            fragments = new ArrayList<>();
        }

        try {
            Method method = section.getViewType().getMethod("newInstance");
            return (Fragment) method.invoke(section.getViewType().getConstructor().newInstance());
        } catch (Exception e) {
            Log.e(TAG, "Could not instantiate Fragment of type " + section.getViewType() + "\n" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private NavigationDrawerSection getSectionById(int id){
        for (NavigationDrawerSection section : sections) {
            if(id == section.getMenuItemId()){
                return section;
            }
        }

        return null;
    }

    protected void initAccount() {
        View headerView = navigationView.getHeaderView(0);

        //Profile Image
        ImageView avatarImageView = (ImageView) headerView.findViewById(R.id.avatar);
        String pictureURL = getAvatarURL();
        if(!TextUtils.isEmpty(pictureURL)){
            Picasso.with(getApplicationContext()).load(pictureURL)
                    .transform(new CircleTransform(2, Color.WHITE))
                    .into(avatarImageView);
        }else {
            Log.e(TAG, "Can't fetch avatar picture for user " + getUserName());
        }

        avatarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAvatarClicked();
            }
        });

        //Cover Image
        String coverUrl = getCoverImageURL();
        if(!TextUtils.isEmpty(coverUrl)){
            ImageView cover = (ImageView) headerView.findViewById(R.id.cover);

            Picasso.with(this)
                    .load(coverUrl)
                    .resize(0, (int) ResourceUtils.convertDpToPixel(172, this))
                    .into(cover);
        } else {
            Log.e(TAG, "Can't fetch cover for login user " + getUserName());
        }

        //Username
        TextView username = (TextView) headerView.findViewById(R.id.username);
        username.setText(getUserName());

        //Logged in with ...
        TextView accountType = (TextView) headerView.findViewById(R.id.account_type);
        String type = getAccountType();

        accountType.setText(type);
    }

    //Abstract Methods
    protected abstract ArrayList<NavigationDrawerSection> initSections();

    protected abstract void updateNavigationDrawerCounters();

    protected abstract String getAvatarURL();

    protected abstract String getCoverImageURL();

    protected abstract String getUserName();

    protected abstract void onAvatarClicked();

    protected abstract String getAccountType();

    //Objects
    /**
     * Helper class used to represent a section of the Navigation Drawer
     */
    public static class NavigationDrawerSection{

        public static String TAG = "NavDrawerSection";

        @IdRes
        private int menuItemId = -1;

        private String title;

        private int type;

        private Class viewType;

        private Delegate delegate;

        public NavigationDrawerSection(int menuItemId, String title, int type, Class viewType) {
            this.menuItemId = menuItemId;
            this.title = title;
            this.type = type;
            this.viewType = viewType;
        }

        public NavigationDrawerSection(int menuItemId, String title, int type, Delegate delegate) {
            this.menuItemId = menuItemId;
            this.title = title;
            this.type = type;
            this.delegate  = delegate;
        }

        //Getters/Setters
        public int getMenuItemId() {
            return menuItemId;
        }

        public void setMenuItemId(int menuItemId) {
            this.menuItemId = menuItemId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public Class getViewType() {
            return viewType;
        }

        public void setViewType(Class viewType) {
            this.viewType = viewType;
        }

        public Delegate getDelegate() {
            return delegate;
        }

        public void setDelegate(Delegate delegate) {
            this.delegate = delegate;
        }

        public static class Type{
            public static final int ACTIVITY = 0;
            public static final int FRAGMENT = 1;
            public static final int ACTION = 2;
        }

        public interface Delegate{
            void onSectionSelected(int id);
        }
    }
}

package com.example.yang.flashtable;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;


public class FragmentController extends StoreMainActivity {
    public static final int FRAG_COUNT = 5;
    public static final int HOME = 0;
    public static final int RECENT = 1;
    public static final int APPOINT = 2;
    public static final int MANAGE = 3;
    public static final int CONFIRM = 4;
    public static Fragment[] fragment;

    public static StoreRecentFragment storeRecentFragment;
    public static StoreHomeFragment storeHomeFragment;

    private static final int DEAD = 0;
    private static final int ALIVE = 1;
    private static final int SHOW = 2;
    private int[] frag_stat;
    private int[] viewContainer;

    private FragmentManager fragmentManager;

    public FragmentController(FragmentManager fm){
        fragment = new Fragment[FRAG_COUNT];
        frag_stat = new int[FRAG_COUNT];
        viewContainer = new int[FRAG_COUNT];
        for(int i=0;i<FRAG_COUNT;i++) viewContainer[i]= R.id.fragment_space;
        viewContainer[CONFIRM] = R.id.fragment_full;
        for(int i=0;i<FRAG_COUNT;i++) frag_stat[i]=DEAD;
        fragmentManager  = fm;
        storeRecentFragment = new StoreRecentFragment();
        storeHomeFragment = new StoreHomeFragment();
        for(int i=0;i<FRAG_COUNT;i++)
            initFragment(i);
    }
    private void setActive(int mode){
        for(int i=0;i<FRAG_COUNT;i++){
            if(i!=mode && frag_stat[i]==SHOW){
                fragmentManager.beginTransaction().hide(fragment[i]).commit();
                frag_stat[i]=ALIVE;
            }
        }
        if(frag_stat[mode]==DEAD) {
            fragmentManager.beginTransaction().add(viewContainer[mode], fragment[mode]).commit();
        }
        else if(frag_stat[mode]==ALIVE)
            fragmentManager.beginTransaction().show(fragment[mode]).commit();
        frag_stat[mode] = SHOW;
    }
    public void act(int select){
        switch (select){
            case HOME:
                setActive(HOME);
                break;
            case RECENT:
                setActive(RECENT);
                break;
            case CONFIRM:
                if(frag_stat[CONFIRM]!=DEAD)
                    kill(CONFIRM);
                setActive(CONFIRM);
                break;
        }
    }
    private void kill(int select){
        fragmentManager.beginTransaction().remove(fragment[select]).commit();
        initFragment(select);
        frag_stat[select]=DEAD;
    }
    private void initFragment(int select){
        switch (select){
            case HOME:
                fragment[select] = storeHomeFragment;
                break;
            case RECENT:
                fragment[select] = storeRecentFragment;
                break;
            case CONFIRM:
                fragment[select] = new StoreHomeConfirmFragment();
                break;
        }

    }
}

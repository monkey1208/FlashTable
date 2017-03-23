package com.example.yang.flashtable;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;


public class FragmentController extends StoreMainActivity {
    private static final int FRAG_COUNT = 4;
    private static final int HOME = 0;
    private static final int RECENT = 1;
    private static final int APPOINT = 2;
    private static final int MANAGE = 3;
    private Fragment[] fragment;

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
        for(int i=0;i<FRAG_COUNT;i++) frag_stat[i]=DEAD;
        fragmentManager  = fm;
        fragment[HOME] = new HomeFragment();
        fragment[RECENT] = new RecentFragment();
    }
    private void setActive(int mode){
        for(int i=0;i<FRAG_COUNT;i++){
            if(i!=mode && frag_stat[i]==SHOW){
                fragmentManager.beginTransaction().hide(fragment[i]).commit();
                frag_stat[i]=ALIVE;
            }
        }
        if(frag_stat[mode]==DEAD)
            fragmentManager.beginTransaction().add(viewContainer[mode],fragment[mode]).commit();
        else if(frag_stat[mode]==ALIVE)
            fragmentManager.beginTransaction().show(fragment[mode]).commit();
        frag_stat[mode] = SHOW;
    }
    public void act(int current_stat){
        switch (current_stat){
            case HOME:
                setActive(HOME);
                break;
            case  RECENT:
                setActive(RECENT);
                break;
        }
    }
}

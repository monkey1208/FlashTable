package com.example.yang.flashtable;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

public class FragmentController extends StoreMainActivity{
    public static final int FRAG_COUNT = 12;
    public static final int HOME = 0;
    public static final int RECENT = 1;
    public static final int APPOINT = 2;
    public static final int MANAGE = 3;
    public static final int MANAGE_SUCCESS = 4;
    public static final int MANAGE_OPENTIME = 5;
    public static final int MANAGE_BILL = 6;
    public static final int MANAGE_DISCOUNT = 7;
    public static final int MANAGE_STATISTIC = 8;
    public static final int MANAGE_RECORD = 9;
    public static final int MANAGE_COMMENT = 10;
    public static final int CONFIRM = 11;
    public StoreRecentFragment storeRecentFragment;
    public StoreHomeFragment storeHomeFragment;
    public StoreAppointFragment storeAppointFragment;
    public StoreManageFragment storeManageFragment;

    public Fragment[] fragment;

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
        viewContainer[MANAGE_COMMENT] = R.id.fragment_full;
        for(int i=0;i<FRAG_COUNT;i++) frag_stat[i]=DEAD;
        fragmentManager  = fm;
        storeRecentFragment = new StoreRecentFragment();
        storeHomeFragment = new StoreHomeFragment();
        storeAppointFragment = new StoreAppointFragment();
        storeManageFragment = new StoreManageFragment();
        for(int i=0;i<FRAG_COUNT;i++)
            initFragment(i);
    }
    private void setActive(int mode){
        for(int i=0;i<FRAG_COUNT;i++){
            if(i!=mode && frag_stat[i]==SHOW){
                fragmentManager.beginTransaction().hide(fragment[i]).commit();
                if(i == HOME) {
                    storeHomeFragment.stopGIF();
                    Log.e("GIF","stop");
                }
                frag_stat[i]=ALIVE;
            }
        }
        if(frag_stat[mode]==DEAD) {
            fragmentManager.beginTransaction().add(viewContainer[mode], fragment[mode]).commit();
        }
        else if(frag_stat[mode]==ALIVE) {
            fragmentManager.beginTransaction().show(fragment[mode]).commit();
            if(mode == HOME) {
                storeHomeFragment.startGIF();
                Log.e("GIF","start");
            }
        }
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
            case APPOINT:
                setActive(APPOINT);
                break;
            case MANAGE:
                setActive(MANAGE);
                break;
            case MANAGE_SUCCESS:
                if(frag_stat[MANAGE_SUCCESS]!=DEAD)
                    kill(MANAGE_SUCCESS);
                setActive(MANAGE_SUCCESS);
                break;
            case MANAGE_OPENTIME:
                if(frag_stat[MANAGE_OPENTIME]!=DEAD)
                    kill(MANAGE_OPENTIME);
                setActive(MANAGE_OPENTIME);
                break;
            case MANAGE_BILL:
                setActive(MANAGE_BILL);
                break;
            case MANAGE_STATISTIC:
                setActive(MANAGE_STATISTIC);
                break;
            case MANAGE_RECORD:
                if(frag_stat[MANAGE_RECORD]!=DEAD)
                    kill(MANAGE_RECORD);
                setActive(MANAGE_RECORD);
                break;
            case MANAGE_DISCOUNT:
                setActive(MANAGE_DISCOUNT);
                break;
            case MANAGE_COMMENT:
                setActive(MANAGE_COMMENT);
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
            case APPOINT:
                fragment[select] = storeAppointFragment;
                break;
            case MANAGE:
                fragment[select] = storeManageFragment;
                break;
            case MANAGE_SUCCESS:
                fragment[select] = new StoreManageSuccessFragment();
                break;
            case MANAGE_OPENTIME:
                fragment[select] = new StoreManageOpentimeFragment();
                break;
            case MANAGE_BILL:
                fragment[select] = new StoreManageBillFragment();
                break;
            case MANAGE_STATISTIC:
                fragment[select] = new StoreManageStatisticFragment();
                break;
            case MANAGE_RECORD:
                fragment[select] = new StoreManageRecordFragment();
                break;
            case MANAGE_DISCOUNT:
                fragment[select] = new StoreManageDiscountFragment();
                break;
            case MANAGE_COMMENT:
                fragment[select] = new StoreManageCommentFragment();
                break;
            case CONFIRM:
                fragment[select] = new StoreHomeConfirmFragment();
                break;
        }
    }
    public void sendBundle(Bundle bundle,int mode){
        switch (mode){
            case CONFIRM:
                if(frag_stat[CONFIRM]!=DEAD)
                    kill(CONFIRM);
                fragment[CONFIRM].setArguments(bundle);
                setActive(CONFIRM);
                break;

        }
    }
}

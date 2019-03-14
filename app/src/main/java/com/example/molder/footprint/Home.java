package com.example.molder.footprint;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.example.molder.footprint.CheckInShare.CheckInShareFragment;

import com.example.molder.footprint.Friends.Friends;
import com.example.molder.footprint.Personal.PersonalHome;
import com.example.molder.footprint.Schedule.ScheduleMainFragment;
import com.github.ikidou.fragmentBackHandler.BackHandlerHelper;


public class Home extends AppCompatActivity {


    private BottomNavigationView homeBottomNavigation;
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        homeBottomNavigation = findViewById(R.id.homeBottomNavigation);
        homeNavigationItemSelectedListener();
        fragment = new HomeFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content,fragment);
        ft.commit();
        initContent();
    }

    @Override
    public void onBackPressed() {
        if (!BackHandlerHelper.handleBackPress(this)) {
            super.onBackPressed();
            this.finish();
        }
    }


    //主功能表換頁
    private void homeNavigationItemSelectedListener() {
        homeBottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.homeStrokeBtnNavigation:
                        fragment = new ScheduleMainFragment();
                        break;
                    case R.id.homeTakePictureBtnNavigation:
                        fragment = new CheckInShareFragment();
                        break;
                    case R.id.homeFriendsBtnNavigation:
                        fragment = new Friends();
                        break;
                    case R.id.homePersonalBtnNavigation:
                        fragment = new PersonalHome();
                        break;
                    default:
                        fragment = new HomeFragment();
                        break;
                }
                item.setChecked(true);
                changeFragment(fragment);
                return false;
            }
        });
    }

    private void initContent() {
        //設定預選的選項
        homeBottomNavigation.setSelectedItemId(R.id.homeBtnNavigation);
    }

    private void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment);
//        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (keyCode == KeyEvent.KEYCODE_BACK && count == 0) {
            new AlertDialogFragment().show(getSupportFragmentManager(), "exit");
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public static class AlertDialogFragment
            extends DialogFragment implements DialogInterface.OnClickListener {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.text_Exit)
                    .setMessage(R.string.msg_WantToExit)
                    .setPositiveButton(R.string.msg_ok, this)
                    .setNegativeButton(R.string.text_No, this)
                    .create();
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    if (getActivity() != null) {
                        getActivity().finish();

                    }
                    break;
                default:
                    dialog.cancel();
                    break;
            }
        }
    }

}


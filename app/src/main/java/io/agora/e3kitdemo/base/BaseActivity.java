package io.agora.e3kitdemo.base;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;

import io.agora.e3kitdemo.enums.Status;
import io.agora.e3kitdemo.utils.OnResourceParseCallback;
import io.agora.e3kitdemo.net.Resource;


/**
 * As a basic activity, place some public methods
 */
public class BaseActivity extends AppCompatActivity {
    public BaseActivity mContext;
    private AlertDialog logoutDialog;
    //Dialog generation time, used to determine the display time of the dialog
    private long dialogCreateTime;
    // Used for the dialog delay to disappear
    private Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        clearFragmentsBeforeCreate();
    }

    @Override
    public void onBackPressed() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                super.onBackPressed();
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * hide keyboard
     */
    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (null != this.getCurrentFocus()) {
            /**
             * Click on the blank position to hide the soft keyboard
             */
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }

        return super.onTouchEvent(event);
    }


    /**
     * Parse Resource<T>
     *
     * @param response
     * @param callback
     * @param <T>
     */
    public <T> void parseResource(Resource<T> response, @NonNull OnResourceParseCallback<T> callback) {
        if (response == null) {
            return;
        }
        if (response.status == Status.SUCCESS) {
            callback.onHideLoading();
            callback.onSuccess(response.data);
        } else if (response.status == Status.ERROR) {
            callback.onHideLoading();

            callback.onError(response.errorCode, response.getMessage());
        } else if (response.status == Status.LOADING) {
            callback.onLoading(response.data);
        }
    }

    /**
     * Deal with the fragment overlay problem caused by Activity reconstruction
     */
    public void clearFragmentsBeforeCreate() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments.size() == 0) {
            return;
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        for (Fragment fragment : fragments) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.commitNow();
    }
}

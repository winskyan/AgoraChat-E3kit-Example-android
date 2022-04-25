package io.agora.e3kitdemo.login;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import io.agora.e3kitdemo.model.SingleSourceLiveData;
import io.agora.e3kitdemo.net.Resource;

public class LoginViewModel extends AndroidViewModel {
    private ClientRepository repository;
    private SingleSourceLiveData<Resource<Boolean>> loginObservable;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        repository = new ClientRepository();
        loginObservable = new SingleSourceLiveData<>();
    }

    public LiveData<Resource<Boolean>> getLoginObservable() {
        return loginObservable;
    }

    public void login(String username, String nickname) {
        loginObservable.setSource(repository.loginByAppServer(username, nickname));
    }

    public void logout(boolean unbindDeviceToken) {
        loginObservable.setSource(repository.logout(unbindDeviceToken));
    }
}

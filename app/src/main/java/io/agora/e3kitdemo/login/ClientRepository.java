package io.agora.e3kitdemo.login;

import static io.agora.cloud.HttpClientManager.Method_POST;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.agora.CallBack;
import io.agora.Error;
import io.agora.chat.ChatClient;
import io.agora.cloud.HttpClientManager;
import io.agora.cloud.HttpResponse;
import io.agora.e3kitdemo.BuildConfig;
import io.agora.e3kitdemo.base.BaseEMRepository;
import io.agora.e3kitdemo.utils.NetworkOnlyResource;
import io.agora.e3kitdemo.utils.ResultCallBack;
import io.agora.e3kitdemo.model.LoginBean;
import io.agora.e3kitdemo.net.Resource;

/**
 * Handle ChatClient related logic
 */
public class ClientRepository extends BaseEMRepository {

    private static final String TAG = ClientRepository.class.getSimpleName();

    /**
     * Sign out
     *
     * @param unbindDeviceToken
     * @return
     */
    public LiveData<Resource<Boolean>> logout(boolean unbindDeviceToken) {
        return new NetworkOnlyResource<Boolean>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<Boolean>> callBack) {
                ChatClient.getInstance().logout(unbindDeviceToken, new CallBack() {

                    @Override
                    public void onSuccess() {
                        //reset();
                        callBack.onSuccess(createLiveData(true));

                    }

                    @Override
                    public void onProgress(int progress, String status) {
                    }

                    @Override
                    public void onError(int code, String error) {
                        //reset();
                        callBack.onError(code, getErrorMsg(code, error));
                    }
                });
            }
        }.asLiveData();
    }


    public LiveData<Resource<Boolean>> loginByAppServer(String username, String nickname) {
        return new NetworkOnlyResource<Boolean>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<Boolean>> callBack) {
                loginToAppServer(username, nickname, new ResultCallBack<LoginBean>() {
                    @Override
                    public void onSuccess(LoginBean value) {
                        if (value != null && !TextUtils.isEmpty(value.getAccessToken())) {
                            ChatClient.getInstance().loginWithAgoraToken(username, value.getAccessToken(), new CallBack() {
                                @Override
                                public void onSuccess() {
                                    success(nickname, callBack);
                                }

                                @Override
                                public void onError(int code, String error) {
                                    callBack.onError(code, getErrorMsg(code, error));
                                }

                                @Override
                                public void onProgress(int progress, String status) {

                                }
                            });
                        } else {
                            callBack.onError(Error.GENERAL_ERROR, "AccessToken is null!");
                        }

                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        callBack.onError(error, getErrorMsg(error, errorMsg));
                    }
                });
            }
        }.asLiveData();
    }

    private void success(String nickname, @NonNull ResultCallBack<LiveData<Boolean>> callBack) {
        callBack.onSuccess(createLiveData(true));
    }

    private void loginToAppServer(String username, String nickname, ResultCallBack<LoginBean> callBack) {
        runOnIOThread(() -> {
            try {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");

                JSONObject request = new JSONObject();
                request.putOpt("userAccount", username);
                request.putOpt("userNickname", nickname);

                String url = BuildConfig.APP_SERVER_PROTOCOL + "://" + BuildConfig.APP_SERVER_DOMAIN + BuildConfig.APP_SERVER_URL;
                HttpResponse response = HttpClientManager.httpExecute(url, headers, request.toString(), Method_POST);
                int code = response.code;
                String responseInfo = response.content;
                if (code == 200) {
                    if (responseInfo != null && responseInfo.length() > 0) {
                        JSONObject object = new JSONObject(responseInfo);
                        String token = object.getString("accessToken");
                        LoginBean bean = new LoginBean();
                        bean.setAccessToken(token);
                        bean.setUserNickname(nickname);
                        if (callBack != null) {
                            callBack.onSuccess(bean);
                        }
                    } else {
                        callBack.onError(code, responseInfo);
                    }
                } else {
                    callBack.onError(code, responseInfo);
                }
            } catch (Exception e) {
                //e.printStackTrace();
                callBack.onError(Error.NETWORK_ERROR, e.getMessage());
            }
        });
    }
}

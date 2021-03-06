package com.android.server.hidata.wavemapping.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.android.server.hidata.wavemapping.cons.ContextManager;
import com.android.server.hidata.wavemapping.cons.ParamManager;
import com.android.server.hidata.wavemapping.entity.ParameterInfo;
import com.android.server.hidata.wavemapping.entity.UiInfo;
import com.android.server.hidata.wavemapping.util.LogUtil;
import com.android.server.wifipro.PortalDbHelper;

public class UiService {
    public static final String MSG_FWK = "WAVEMAPPING.FWK.MSG";
    public static final String MSG_UI_1 = "WAVEMAPPING.UI.MSG.addWmpRegularPlace";
    public static final String MSG_UI_2 = "WAVEMAPPING.UI.MSG.forceTrainModel";
    public static final String MSG_UI_4 = "WAVEMAPPING.UI.MSG.leaveAction";
    private static final String TAG;
    private static UiInfo uiInfo = new UiInfo();
    private IntentFilter filter;
    private ParameterInfo param = ParamManager.getInstance().getParameterInfo();
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        public String ssid;

        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String action = intent.getAction();
                if (action != null) {
                    Object obj = -1;
                    int hashCode = action.hashCode();
                    if (hashCode != -1421450094) {
                        if (hashCode != -1329847201) {
                            if (hashCode == 129766110 && action.equals(UiService.MSG_UI_2)) {
                                obj = 1;
                            }
                        } else if (action.equals(UiService.MSG_UI_4)) {
                            obj = 2;
                        }
                    } else if (action.equals(UiService.MSG_UI_1)) {
                        obj = null;
                    }
                    switch (obj) {
                        case null:
                            if (intent.getExtras() != null) {
                                this.ssid = intent.getExtras().getString(PortalDbHelper.ITEM_SSID);
                            }
                            UiService.this.addWmpRegularPlace(this.ssid);
                            break;
                        case 1:
                            UiService.this.forceTrainModel();
                            break;
                        case 2:
                            UiService.this.leaveAction();
                            break;
                    }
                }
            }
        }
    };
    private Handler wmStateMachineHandler;

    static {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("WMapping.");
        stringBuilder.append(UiService.class.getSimpleName());
        TAG = stringBuilder.toString();
    }

    public ParameterInfo getParam() {
        return this.param;
    }

    public void setParam(ParameterInfo param) {
        this.param = param;
    }

    public UiService(Handler smHandler) {
        this.wmStateMachineHandler = smHandler;
        this.filter = new IntentFilter();
        this.filter.addAction(MSG_UI_1);
        this.filter.addAction(MSG_UI_2);
        this.filter.addAction(MSG_UI_4);
        ContextManager.getInstance().getContext().registerReceiver(this.receiver, this.filter);
    }

    public boolean addWmpRegularPlace(String place) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("addWmpRegularPlace :");
        stringBuilder.append(place);
        LogUtil.i(stringBuilder.toString());
        Message msg = Message.obtain(this.wmStateMachineHandler, 20);
        Bundle bundle = new Bundle();
        bundle.putCharSequence("LOCATION", place);
        msg.setData(bundle);
        this.wmStateMachineHandler.sendMessage(msg);
        return true;
    }

    public boolean forceTrainModel() {
        this.wmStateMachineHandler.sendEmptyMessage(60);
        return true;
    }

    public boolean leaveAction() {
        this.wmStateMachineHandler.sendEmptyMessage(55);
        return true;
    }

    public boolean setParameter(ParameterInfo param) {
        setParam(param);
        return true;
    }

    public static UiInfo getUiInfo() {
        return uiInfo;
    }

    public static void sendMsgToUi() {
        try {
            if (LogUtil.getDebug_flag()) {
                sendBrocast(MSG_FWK, uiInfo.toJsonStr());
            }
        } catch (Exception e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("LocatingState,e");
            stringBuilder.append(e.getMessage());
            LogUtil.e(stringBuilder.toString());
        }
    }

    public static void sendBrocast(String action, String uiInfo) {
        Intent intent = null;
        Object obj = (action.hashCode() == -1061096524 && action.equals(MSG_FWK)) ? null : -1;
        if (obj == null) {
            intent = new Intent(MSG_FWK);
            Bundle bundle3 = new Bundle();
            bundle3.putString("uiInfo", uiInfo);
            intent.putExtras(bundle3);
        }
        LogUtil.i(uiInfo);
        ContextManager.getInstance().getContext().sendBroadcast(intent);
    }
}

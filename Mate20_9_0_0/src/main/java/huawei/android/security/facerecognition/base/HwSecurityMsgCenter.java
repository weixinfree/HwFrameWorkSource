package huawei.android.security.facerecognition.base;

import huawei.android.security.facerecognition.FaceRecognizeEvent;
import huawei.android.security.facerecognition.base.HwSecurityTaskBase.EventListener;
import huawei.android.security.facerecognition.utils.LogUtil;
import java.util.HashMap;
import java.util.Map.Entry;

public class HwSecurityMsgCenter {
    private static final String TAG = HwSecurityMsgCenter.class.getSimpleName();
    private static HwSecurityMsgCenter gInstance = null;
    private static Object mInstanceLock = new Object();
    private HashMap<Integer, HashMap<HwSecurityTaskBase, EventRegInfo>> mEvMaps = new HashMap();
    private Object mLock = new Object();

    private static class EventRegInfo {
        private boolean mEnable;
        private EventListener mListener;

        private EventRegInfo(EventListener listener, boolean enable) {
            this.mListener = listener;
            this.mEnable = enable;
        }
    }

    protected HashMap<HwSecurityTaskBase, EventRegInfo> getEventMap(int evId, boolean createWhileEmpty) {
        if (this.mEvMaps == null) {
            LogUtil.e(TAG, "mEvMaps is empty!!!");
            return null;
        } else if (this.mEvMaps.containsKey(Integer.valueOf(evId))) {
            return (HashMap) this.mEvMaps.get(Integer.valueOf(evId));
        } else {
            if (!createWhileEmpty) {
                return null;
            }
            this.mEvMaps.put(Integer.valueOf(evId), new HashMap());
            return (HashMap) this.mEvMaps.get(Integer.valueOf(evId));
        }
    }

    /* JADX WARNING: Missing block: B:26:0x003d, code skipped:
            return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean registerEvent(int evId, HwSecurityTaskBase task, EventListener evListener) {
        synchronized (this.mLock) {
            if (task == null || evListener == null) {
            } else {
                HashMap<HwSecurityTaskBase, EventRegInfo> maps = getEventMap(evId, true);
                if (maps == null) {
                    return false;
                } else if (maps.containsKey(task)) {
                    return false;
                } else {
                    HwSecurityTaskBase parent = task.getParent();
                    if (parent != null && maps.containsKey(parent)) {
                        EventRegInfo parentInfo = (EventRegInfo) maps.get(parent);
                        if (parentInfo != null) {
                            parentInfo.mEnable = false;
                        }
                    }
                    maps.put(task, new EventRegInfo(evListener, true));
                    return true;
                }
            }
        }
    }

    public void unregisterEvent(int evId, HwSecurityTaskBase task) {
        synchronized (this.mLock) {
            HashMap<HwSecurityTaskBase, EventRegInfo> maps = getEventMap(evId, null);
            if (maps == null) {
                return;
            }
            HwSecurityTaskBase parent = task.getParent();
            if (parent != null && maps.containsKey(parent)) {
                EventRegInfo parentInfo = (EventRegInfo) maps.get(parent);
                if (parentInfo != null) {
                    parentInfo.mEnable = true;
                }
            }
            maps.remove(task);
        }
    }

    /* JADX WARNING: Missing block: B:12:0x0052, code skipped:
            r2 = r0.values().iterator();
     */
    /* JADX WARNING: Missing block: B:14:0x005e, code skipped:
            if (r2.hasNext() == false) goto L_0x0080;
     */
    /* JADX WARNING: Missing block: B:15:0x0060, code skipped:
            r3 = (huawei.android.security.facerecognition.base.HwSecurityMsgCenter.EventRegInfo) r2.next();
     */
    /* JADX WARNING: Missing block: B:16:0x0066, code skipped:
            if (r3 == null) goto L_0x007f;
     */
    /* JADX WARNING: Missing block: B:18:0x006c, code skipped:
            if (huawei.android.security.facerecognition.base.HwSecurityMsgCenter.EventRegInfo.access$000(r3) == false) goto L_0x007f;
     */
    /* JADX WARNING: Missing block: B:20:0x0072, code skipped:
            if (huawei.android.security.facerecognition.base.HwSecurityMsgCenter.EventRegInfo.access$200(r3) == null) goto L_0x007f;
     */
    /* JADX WARNING: Missing block: B:22:0x007c, code skipped:
            if (huawei.android.security.facerecognition.base.HwSecurityMsgCenter.EventRegInfo.access$200(r3).onEvent(r9) == false) goto L_0x007f;
     */
    /* JADX WARNING: Missing block: B:24:0x0080, code skipped:
            return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void processEvent(FaceRecognizeEvent ev) {
        HashMap<HwSecurityTaskBase, EventRegInfo> copyMaps = new HashMap();
        synchronized (this.mLock) {
            int evId = ev.getType();
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("processEvent: ");
            stringBuilder.append(ev);
            LogUtil.d(str, stringBuilder.toString());
            HashMap<HwSecurityTaskBase, EventRegInfo> maps = getEventMap(evId, false);
            if (maps == null) {
                return;
            }
            for (Entry<HwSecurityTaskBase, EventRegInfo> entry : maps.entrySet()) {
                copyMaps.put((HwSecurityTaskBase) entry.getKey(), (EventRegInfo) entry.getValue());
            }
        }
    }

    public static boolean staticRegisterEvent(int evId, HwSecurityTaskBase task, EventListener evListener) {
        HwSecurityMsgCenter gMsgCenter = getInstance();
        if (gMsgCenter != null) {
            return gMsgCenter.registerEvent(evId, task, evListener);
        }
        return false;
    }

    public static boolean staticUnregisterEvent(int evId, HwSecurityTaskBase task) {
        HwSecurityMsgCenter gMsgCenter = getInstance();
        if (gMsgCenter == null) {
            return false;
        }
        gMsgCenter.unregisterEvent(evId, task);
        return true;
    }

    public static void createInstance() {
        synchronized (mInstanceLock) {
            if (gInstance == null) {
                gInstance = new HwSecurityMsgCenter();
            }
        }
    }

    public static HwSecurityMsgCenter getInstance() {
        HwSecurityMsgCenter hwSecurityMsgCenter;
        synchronized (mInstanceLock) {
            hwSecurityMsgCenter = gInstance;
        }
        return hwSecurityMsgCenter;
    }

    public static void destroyInstance() {
        synchronized (mInstanceLock) {
            if (gInstance != null) {
                gInstance = null;
            }
        }
    }
}

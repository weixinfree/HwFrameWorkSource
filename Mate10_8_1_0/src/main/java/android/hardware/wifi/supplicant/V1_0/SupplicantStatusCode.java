package android.hardware.wifi.supplicant.V1_0;

public final class SupplicantStatusCode {
    public static final int FAILURE_ARGS_INVALID = 2;
    public static final int FAILURE_IFACE_DISABLED = 6;
    public static final int FAILURE_IFACE_EXISTS = 5;
    public static final int FAILURE_IFACE_INVALID = 3;
    public static final int FAILURE_IFACE_NOT_DISCONNECTED = 7;
    public static final int FAILURE_IFACE_UNKNOWN = 4;
    public static final int FAILURE_NETWORK_INVALID = 8;
    public static final int FAILURE_NETWORK_UNKNOWN = 9;
    public static final int FAILURE_UNKNOWN = 1;
    public static final int SUCCESS = 0;

    public static final java.lang.String dumpBitfield(int r1) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.hardware.wifi.supplicant.V1_0.SupplicantStatusCode.dumpBitfield(int):java.lang.String
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:116)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:249)
	at jadx.core.ProcessClass.process(ProcessClass.java:31)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:296)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:199)
Caused by: jadx.core.utils.exceptions.DecodeException: Unknown instruction: not-int
	at jadx.core.dex.instructions.InsnDecoder.decode(InsnDecoder.java:568)
	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:56)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:102)
	... 7 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: android.hardware.wifi.supplicant.V1_0.SupplicantStatusCode.dumpBitfield(int):java.lang.String");
    }

    public static final String toString(int o) {
        if (o == 0) {
            return "SUCCESS";
        }
        if (o == 1) {
            return "FAILURE_UNKNOWN";
        }
        if (o == 2) {
            return "FAILURE_ARGS_INVALID";
        }
        if (o == 3) {
            return "FAILURE_IFACE_INVALID";
        }
        if (o == 4) {
            return "FAILURE_IFACE_UNKNOWN";
        }
        if (o == 5) {
            return "FAILURE_IFACE_EXISTS";
        }
        if (o == 6) {
            return "FAILURE_IFACE_DISABLED";
        }
        if (o == 7) {
            return "FAILURE_IFACE_NOT_DISCONNECTED";
        }
        if (o == 8) {
            return "FAILURE_NETWORK_INVALID";
        }
        if (o == 9) {
            return "FAILURE_NETWORK_UNKNOWN";
        }
        return "0x" + Integer.toHexString(o);
    }
}

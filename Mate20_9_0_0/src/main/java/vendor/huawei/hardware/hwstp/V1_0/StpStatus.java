package vendor.huawei.hardware.hwstp.V1_0;

import java.util.ArrayList;

public final class StpStatus {
    public static final byte STP_RISK = (byte) 1;
    public static final byte STP_SAFE = (byte) 0;

    public static final String toString(byte o) {
        if (o == (byte) 0) {
            return "STP_SAFE";
        }
        if (o == (byte) 1) {
            return "STP_RISK";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("0x");
        stringBuilder.append(Integer.toHexString(Byte.toUnsignedInt(o)));
        return stringBuilder.toString();
    }

    public static final String dumpBitfield(byte o) {
        ArrayList<String> list = new ArrayList();
        byte flipped = (byte) 0;
        list.add("STP_SAFE");
        if ((o & 1) == 1) {
            list.add("STP_RISK");
            flipped = (byte) (0 | 1);
        }
        if (o != flipped) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("0x");
            stringBuilder.append(Integer.toHexString(Byte.toUnsignedInt((byte) ((~flipped) & o))));
            list.add(stringBuilder.toString());
        }
        return String.join(" | ", list);
    }
}

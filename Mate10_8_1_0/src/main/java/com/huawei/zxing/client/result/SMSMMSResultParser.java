package com.huawei.zxing.client.result;

import com.huawei.zxing.Result;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class SMSMMSResultParser extends ResultParser {
    public SMSParsedResult parse(Result result) {
        boolean z;
        String rawText = ResultParser.getMassagedText(result);
        if (rawText.startsWith("sms:") || rawText.startsWith("SMS:") || rawText.startsWith("mms:")) {
            z = true;
        } else {
            z = rawText.startsWith("MMS:");
        }
        if (!z) {
            return null;
        }
        String smsURIWithoutQuery;
        Map<String, String> nameValuePairs = ResultParser.parseNameValuePairs(rawText);
        String str = null;
        String str2 = null;
        boolean querySyntax = false;
        if (!(nameValuePairs == null || (nameValuePairs.isEmpty() ^ 1) == 0)) {
            str = (String) nameValuePairs.get("subject");
            str2 = (String) nameValuePairs.get("body");
            querySyntax = true;
        }
        int queryStart = rawText.indexOf(63, 4);
        if (queryStart < 0 || (querySyntax ^ 1) != 0) {
            smsURIWithoutQuery = rawText.substring(4);
        } else {
            smsURIWithoutQuery = rawText.substring(4, queryStart);
        }
        int lastComma = -1;
        List<String> numbers = new ArrayList(1);
        List<String> vias = new ArrayList(1);
        while (true) {
            int comma = smsURIWithoutQuery.indexOf(44, lastComma + 1);
            if (comma > lastComma) {
                addNumberVia(numbers, vias, smsURIWithoutQuery.substring(lastComma + 1, comma));
                lastComma = comma;
            } else {
                addNumberVia(numbers, vias, smsURIWithoutQuery.substring(lastComma + 1));
                return new SMSParsedResult((String[]) numbers.toArray(new String[numbers.size()]), (String[]) vias.toArray(new String[vias.size()]), str, str2);
            }
        }
    }

    private static void addNumberVia(Collection<String> numbers, Collection<String> vias, String numberPart) {
        int numberEnd = numberPart.indexOf(59);
        if (numberEnd < 0) {
            numbers.add(numberPart);
            vias.add(null);
            return;
        }
        Object substring;
        numbers.add(numberPart.substring(0, numberEnd));
        String maybeVia = numberPart.substring(numberEnd + 1);
        if (maybeVia.startsWith("via=")) {
            substring = maybeVia.substring(4);
        } else {
            substring = null;
        }
        vias.add(substring);
    }
}

package gov.nist.javax.sip.parser.ims;

import gov.nist.javax.sip.header.SIPHeader;
import gov.nist.javax.sip.header.ims.SecurityClient;
import gov.nist.javax.sip.header.ims.SecurityClientList;
import gov.nist.javax.sip.parser.Lexer;
import gov.nist.javax.sip.parser.TokenTypes;
import java.text.ParseException;

public class SecurityClientParser extends SecurityAgreeParser {
    public SecurityClientParser(String security) {
        super(security);
    }

    protected SecurityClientParser(Lexer lexer) {
        super(lexer);
    }

    public SIPHeader parse() throws ParseException {
        dbg_enter("SecuriryClient parse");
        SecurityClientList secClientList;
        try {
            headerName(TokenTypes.SECURITY_CLIENT);
            secClientList = (SecurityClientList) super.parse(new SecurityClient());
            return secClientList;
        } finally {
            secClientList = "SecuriryClient parse";
            dbg_leave(secClientList);
        }
    }
}

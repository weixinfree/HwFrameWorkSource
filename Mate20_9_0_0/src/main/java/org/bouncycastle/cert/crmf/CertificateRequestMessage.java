package org.bouncycastle.cert.crmf;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.crmf.AttributeTypeAndValue;
import org.bouncycastle.asn1.crmf.CRMFObjectIdentifiers;
import org.bouncycastle.asn1.crmf.CertReqMsg;
import org.bouncycastle.asn1.crmf.CertTemplate;
import org.bouncycastle.asn1.crmf.Controls;
import org.bouncycastle.asn1.crmf.PKIArchiveOptions;
import org.bouncycastle.asn1.crmf.POPOSigningKey;
import org.bouncycastle.asn1.crmf.ProofOfPossession;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Encodable;

public class CertificateRequestMessage implements Encodable {
    public static final int popKeyAgreement = 3;
    public static final int popKeyEncipherment = 2;
    public static final int popRaVerified = 0;
    public static final int popSigningKey = 1;
    private final CertReqMsg certReqMsg;
    private final Controls controls;

    public CertificateRequestMessage(CertReqMsg certReqMsg) {
        this.certReqMsg = certReqMsg;
        this.controls = certReqMsg.getCertReq().getControls();
    }

    public CertificateRequestMessage(byte[] bArr) throws IOException {
        this(parseBytes(bArr));
    }

    private AttributeTypeAndValue findControl(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        if (this.controls == null) {
            return null;
        }
        AttributeTypeAndValue[] toAttributeTypeAndValueArray = this.controls.toAttributeTypeAndValueArray();
        for (int i = 0; i != toAttributeTypeAndValueArray.length; i++) {
            if (toAttributeTypeAndValueArray[i].getType().equals(aSN1ObjectIdentifier)) {
                return toAttributeTypeAndValueArray[i];
            }
        }
        return null;
    }

    private static CertReqMsg parseBytes(byte[] bArr) throws IOException {
        StringBuilder stringBuilder;
        try {
            return CertReqMsg.getInstance(ASN1Primitive.fromByteArray(bArr));
        } catch (ClassCastException e) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("malformed data: ");
            stringBuilder.append(e.getMessage());
            throw new CertIOException(stringBuilder.toString(), e);
        } catch (IllegalArgumentException e2) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("malformed data: ");
            stringBuilder.append(e2.getMessage());
            throw new CertIOException(stringBuilder.toString(), e2);
        }
    }

    private boolean verifySignature(ContentVerifierProvider contentVerifierProvider, POPOSigningKey pOPOSigningKey) throws CRMFException {
        try {
            ContentVerifier contentVerifier = contentVerifierProvider.get(pOPOSigningKey.getAlgorithmIdentifier());
            CRMFUtil.derEncodeToStream(pOPOSigningKey.getPoposkInput() != null ? pOPOSigningKey.getPoposkInput() : this.certReqMsg.getCertReq(), contentVerifier.getOutputStream());
            return contentVerifier.verify(pOPOSigningKey.getSignature().getOctets());
        } catch (OperatorCreationException e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("unable to create verifier: ");
            stringBuilder.append(e.getMessage());
            throw new CRMFException(stringBuilder.toString(), e);
        }
    }

    public CertTemplate getCertTemplate() {
        return this.certReqMsg.getCertReq().getCertTemplate();
    }

    public Control getControl(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        AttributeTypeAndValue findControl = findControl(aSN1ObjectIdentifier);
        if (findControl != null) {
            if (findControl.getType().equals(CRMFObjectIdentifiers.id_regCtrl_pkiArchiveOptions)) {
                return new PKIArchiveControl(PKIArchiveOptions.getInstance(findControl.getValue()));
            }
            if (findControl.getType().equals(CRMFObjectIdentifiers.id_regCtrl_regToken)) {
                return new RegTokenControl(DERUTF8String.getInstance(findControl.getValue()));
            }
            if (findControl.getType().equals(CRMFObjectIdentifiers.id_regCtrl_authenticator)) {
                return new AuthenticatorControl(DERUTF8String.getInstance(findControl.getValue()));
            }
        }
        return null;
    }

    public byte[] getEncoded() throws IOException {
        return this.certReqMsg.getEncoded();
    }

    public int getProofOfPossessionType() {
        return this.certReqMsg.getPopo().getType();
    }

    public boolean hasControl(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return findControl(aSN1ObjectIdentifier) != null;
    }

    public boolean hasControls() {
        return this.controls != null;
    }

    public boolean hasProofOfPossession() {
        return this.certReqMsg.getPopo() != null;
    }

    public boolean hasSigningKeyProofOfPossessionWithPKMAC() {
        ProofOfPossession popo = this.certReqMsg.getPopo();
        return popo.getType() == 1 && POPOSigningKey.getInstance(popo.getObject()).getPoposkInput().getPublicKeyMAC() != null;
    }

    public boolean isValidSigningKeyPOP(ContentVerifierProvider contentVerifierProvider) throws CRMFException, IllegalStateException {
        ProofOfPossession popo = this.certReqMsg.getPopo();
        if (popo.getType() == 1) {
            POPOSigningKey instance = POPOSigningKey.getInstance(popo.getObject());
            if (instance.getPoposkInput() == null || instance.getPoposkInput().getPublicKeyMAC() == null) {
                return verifySignature(contentVerifierProvider, instance);
            }
            throw new IllegalStateException("verification requires password check");
        }
        throw new IllegalStateException("not Signing Key type of proof of possession");
    }

    public boolean isValidSigningKeyPOP(ContentVerifierProvider contentVerifierProvider, PKMACBuilder pKMACBuilder, char[] cArr) throws CRMFException, IllegalStateException {
        ProofOfPossession popo = this.certReqMsg.getPopo();
        if (popo.getType() == 1) {
            POPOSigningKey instance = POPOSigningKey.getInstance(popo.getObject());
            if (instance.getPoposkInput() == null || instance.getPoposkInput().getSender() != null) {
                throw new IllegalStateException("no PKMAC present in proof of possession");
            }
            return new PKMACValueVerifier(pKMACBuilder).isValid(instance.getPoposkInput().getPublicKeyMAC(), cArr, getCertTemplate().getPublicKey()) ? verifySignature(contentVerifierProvider, instance) : false;
        } else {
            throw new IllegalStateException("not Signing Key type of proof of possession");
        }
    }

    public CertReqMsg toASN1Structure() {
        return this.certReqMsg;
    }
}

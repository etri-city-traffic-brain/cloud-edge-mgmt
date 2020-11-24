package com.innogrid.uniq.coreopenstack.model;

import lombok.Data;
import org.openstack4j.model.compute.Keypair;

import java.io.Serializable;

/**
 * @author wss
 * @date 2019.3.25
 * @brief 오픈스택 KeyPair 모델
 */
@Data
public class KeyPairInfo implements Serializable {

    private static final long serialVersionUID = 8585354799376127134L;
    private String name;
    private String fingerprint;
    private String publicKey;
    private String privateKey;

    public KeyPairInfo() {

    }

    public KeyPairInfo(Keypair info) {
        this.name = info.getName();
        this.fingerprint = info.getFingerprint();
        this.publicKey = info.getPublicKey();
        this.privateKey = info.getPrivateKey();
    }
}

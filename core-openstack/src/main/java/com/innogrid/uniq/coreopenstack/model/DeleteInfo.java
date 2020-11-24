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
public class DeleteInfo implements Serializable {

    private static final long serialVersionUID = 8585354799376127134L;
    private String id;
    private String name;

    public DeleteInfo() {
    }
//    public DeleteInfo(VolumeInfo info) {
//        this.id = info.getId();
//        this.name = info.getName();
//    }
//    public DeleteInfo(NetworkInfo info) {
//        this.id = info.getId();
//        this.name = info.getName();
//    }

}

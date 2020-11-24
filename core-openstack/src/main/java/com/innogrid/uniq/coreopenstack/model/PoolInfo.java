package com.innogrid.uniq.coreopenstack.model;

import lombok.Data;
import org.openstack4j.model.network.Pool;

import java.io.Serializable;

/**
 * @author wss
 * @date 2019.4.12
 * @brief 오픈스택 Subnet 모델
 */
@Data
public class PoolInfo implements Serializable {

    private static final long serialVersionUID = 6423963740882789376L;
    private String start;
    private String end;

    public PoolInfo() {

    }

    public PoolInfo(Pool info) {
        this.start = info.getStart();
        this.end = info.getEnd();
    }
}

package cn.cyejing.shuttle.common.model;

import cn.cyejing.shuttle.common.model.ConnectRequest.ConnectType;
import cn.cyejing.shuttle.common.model.ConnectRequest.Version;
import lombok.Data;

/**
 * @author Born
 */
@Data
public class ConnectResponse {

    private Version version = Version.V1;
    private ConnectType type;

    public ConnectResponse(ConnectType type) {
        this.type = type;
    }
}

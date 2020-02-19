package cn.cyejing.lastjump.intel.proto.model;

import cn.cyejing.lastjump.intel.proto.model.ConnectRequest.ConnectType;
import cn.cyejing.lastjump.intel.proto.model.ConnectRequest.Version;
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

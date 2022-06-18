package enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @program: NatEngine
 * @author: cx
 * @create: 2022-06-15 20:25
 * @description: 内网穿透传输过程中出现的错误
 **/

@AllArgsConstructor
@Getter
public enum NatError {


    UNKNOWN_PROTOCOL("不识别的协议包"),
    SERVICE_NOT_FOUND("找不到对应的服务"),
    FAILED_TO_CONNECT_TO_SERVICE_REGISTRY("连接注册中心失败"),
    REGISTER_SERVICE_FAILED("注册服务失败");

    private final String message;
}

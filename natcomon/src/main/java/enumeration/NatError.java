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

    UNKNOWN_PROTOCOL("不识别的协议包");

    private final String message;
}

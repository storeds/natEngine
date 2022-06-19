package exception;

import enumeration.NatError;

/**
 * @program: NatEngine
 * @author: cx
 * @create: 2022-06-15 20:38
 * @description: nat存在的运行时异常
 **/
public class NatException extends RuntimeException {

    /**
     * 传递异常类型和详情
     * @param naterror
     * @param detial
     */
    public NatException(NatError naterror, String detial) {
        super(naterror.getMessage() + ":" + detial);
    }

    /**
     * 传递异常信息和其他异常
     * @param message
     * @param cause
     */
    public NatException(NatError message, Throwable cause) {
        super(String.valueOf(message), cause);
    }

    /**
     * 传递自定义异常
     * @param naterror
     */
    public NatException(NatError naterror) {
        super(naterror.getMessage());
    }

}

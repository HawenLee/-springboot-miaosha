package com.hawen.miaosha.exception;


import com.hawen.miaosha.result.CodeMsg;

public class MiaoshaException extends RuntimeException {
    private final CodeMsg codeMsg;

    public MiaoshaException(CodeMsg codeMsg) {
        super(codeMsg.getMsg());
        this.codeMsg = codeMsg;
    }

    public CodeMsg getCodeMsg() {
        return codeMsg;
    }
}

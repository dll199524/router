package com.example.myrouter_api.result;

public class RouterResult {
    static final int SUCCESS_CODE = 0X000001;
    static final int FAILURE_CODE = 0X000002;
    String msg;
    int code;
    private Object object;

    public RouterResult(Builder builder) {
        this.msg = builder.msg;
        this.code = builder.code;
        this.object = builder.object;
    }

    public String getMsg() {
        return msg;
    }

    public int getCode() {
        return code;
    }

    public Object getObject() {
        return object;
    }

    boolean isSuccess() {return  code == SUCCESS_CODE;}

    static class Builder {
        String msg;
        int code;
        private Object object;
        public Builder mag(String msg) {
            this.msg = msg;
            return this;
        }
        public Builder success() {
            this.code = SUCCESS_CODE;
            return this;
        }
        public Builder error() {
            this.code = FAILURE_CODE;
            return this;
        }
        public Builder object(Object object) {
            this.object = object;
            return this;
        }

        public RouterResult build() {return new RouterResult(this);}
    }
}

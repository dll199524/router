package com.example.myrouter_api.result;

public interface ActionCallBack {

    void onInterrupt();
    void onResult(RouterResult result);

    ActionCallBack DEFAULT_ACTION_CALLBACK = new ActionCallBack() {
        @Override
        public void onInterrupt() {

        }

        @Override
        public void onResult(RouterResult result) {

        }
    };

}

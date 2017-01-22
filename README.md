#RxBus_

采用RxAndroid、APT的思路,根据EventBus的源码仿造一个RxBus。


###注册-注销-接受事件
```java
    @Subscribe
    @BindRxBus
    public void onEvent(Event event) {
       Toast.makeText(this, "on Event", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate() {
        super.onStart();
        RxBus.getInstance().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unregister(this);
    }
```

###发送消息
```java

    RxBusDao.getInstance().post(new Event());

```









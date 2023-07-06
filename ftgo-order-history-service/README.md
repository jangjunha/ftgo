# Order History Service

### Run Web Server

```shell
$ java \
    -cp ftgo.ftgo-order-history-service.main \
    me.jangjunha.ftgo.order_history_service.OrderHistoryServiceMainKt 
```

### Initialize Database 

```shell
$ java \
    -cp ftgo.ftgo-order-history-service.main \
    -Dspring.profiles.active=cli \
    me.jangjunha.ftgo.order_history_service.OrderHistoryServiceMainKt
    db create
```

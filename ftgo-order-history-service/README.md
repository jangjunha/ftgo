# Order History Service

[![Can I Deploy main to production](https://pact.ftgo.jangjunha.me/pacticipants/ftgo-order-history-service/branches/main/latest-version/can-i-deploy/to-environment/production/badge)](https://pact.ftgo.jangjunha.me/hal-browser/browser.html#https://pact.ftgo.jangjunha.me/pacticipants/ftgo-order-history-service/branches/main/latest-version/can-i-deploy/to-environment/production)

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

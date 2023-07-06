package me.jangjunha.ftgo.order_history_service.dynamodb

import software.amazon.awssdk.enhanced.dynamodb.Expression
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

data class SourceEvent(
    val aggregateType: String,
    val aggregateId: String,
    val eventId: String,
) {
    val ignoreIfDuplicateExpression: Expression
        get() = Expression.builder()
            .expression("attribute_not_exists(#duplicateDetection) OR #duplicateDetection < :eventId")
            .putExpressionName("#duplicateDetection", "events.$aggregateType$aggregateId")
            .putExpressionValue(":eventId", AttributeValue.fromS(eventId))
            .build()
}

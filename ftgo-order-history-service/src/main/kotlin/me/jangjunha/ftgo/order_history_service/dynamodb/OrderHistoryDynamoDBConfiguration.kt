package me.jangjunha.ftgo.order_history_service.dynamodb

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.net.URI

@Configuration
class OrderHistoryDynamoDBConfiguration {

    @Value("\${aws.dynamodb.endpoint.url:#{null}}")
    private val endpointURL: String? = null

    @Bean
    fun dynamoDBClient(): DynamoDbClient {
        var builder = DynamoDbClient.builder()
        if (endpointURL != null) {
            builder = builder.endpointOverride(URI.create(endpointURL))
        }
        return builder.build()
    }

    @Bean
    fun dynamoDB(
        dynamoDbClient: DynamoDbClient,
    ): DynamoDbEnhancedClient {
        return DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build()
    }
}

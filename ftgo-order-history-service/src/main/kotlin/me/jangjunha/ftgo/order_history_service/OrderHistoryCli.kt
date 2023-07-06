package me.jangjunha.ftgo.order_history_service

import me.jangjunha.ftgo.order_history_service.domain.Order
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.shell.command.annotation.Command
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.ProjectionType
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter

@Command(command = ["db"])
class OrderHistoryCli
@Autowired constructor(
    private val dynamoDBClient: DynamoDbClient,
    private val orderTable: DynamoDbTable<Order>,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Command(command = ["create"])
    fun createTable() {
        logger.info("Creating table ${orderTable.tableName()}...")
        orderTable.createTable { builder ->
            builder
                .provisionedThroughput { thrp ->
                    thrp.readCapacityUnits(3)
                        .writeCapacityUnits(3)
                        .build()
                }
                .globalSecondaryIndices({ gsib ->
                    gsib.indexName(Order.consumerCreationDateIndexName)
                        .projection { pb -> pb.projectionType(ProjectionType.ALL) }
                        .provisionedThroughput { gsithrp ->
                            gsithrp
                                .readCapacityUnits(3)
                                .writeCapacityUnits(3)
                        }
                })
        }
        val waiter = DynamoDbWaiter.builder().client(dynamoDBClient).build()
        val response = waiter.waitUntilTableExists {
            it.tableName(orderTable.tableName()).build()
        }.matched()
        val description = response.response().orElseThrow {
            logger.error("${orderTable.tableName()} table was not created.")
            RuntimeException("${orderTable.tableName()} table was not created.")
        }
        logger.info("${description.table().tableName()} was created.")
    }
}

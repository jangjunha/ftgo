package me.jangjunha.ftgo.apigateway.proxies

import com.google.protobuf.Empty
import io.grpc.CallCredentials
import io.grpc.ManagedChannelBuilder
import io.grpc.stub.StreamObserver
import me.jangjunha.ftgo.apigateway.Destinations
import me.jangjunha.ftgo.common.protobuf.TimestampUtils
import me.jangjunha.ftgo.kitchen_service.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Service
class KitchenService
@Autowired constructor(
    destinations: Destinations,
) {
    private val stub: KitchenServiceGrpc.KitchenServiceStub = KitchenServiceGrpc.newStub(
        ManagedChannelBuilder.forTarget(destinations.kitchenServiceUrl).usePlaintext().build()
    )

    suspend fun findTicketById(id: UUID, credentials: CallCredentials): Ticket = suspendCoroutine { cont ->
        val payload = getTicketPayload {
            ticketId = id.toString()
        }
        stub.withCallCredentials(credentials)
            .getTicket(payload, object : StreamObserver<Ticket> {
                override fun onNext(value: Ticket?) {
                    if (value == null) {
                        cont.resumeWithException(TicketNotFoundException())
                        return
                    }
                    cont.resume(value)
                }

                override fun onError(t: Throwable?) {
                    cont.resumeWithException(t ?: RuntimeException())
                }

                override fun onCompleted() {}
            })
    }

    suspend fun acceptTicket(id: UUID, readyBy: OffsetDateTime, credentials: CallCredentials): Unit =
        suspendCoroutine { cont ->
            val payload = acceptTicketPayload {
                this.ticketId = id.toString()
                this.readyBy = TimestampUtils.toTimestamp(readyBy)
            }
            stub.withCallCredentials(credentials)
                .acceptTicket(payload, object : StreamObserver<Empty> {
                    override fun onNext(value: Empty?) {
                        cont.resume(Unit)
                    }

                    override fun onError(t: Throwable?) {
                        cont.resumeWithException(t ?: RuntimeException())
                    }

                    override fun onCompleted() {}
                })
        }
}

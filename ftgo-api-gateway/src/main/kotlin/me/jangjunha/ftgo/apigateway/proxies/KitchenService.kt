package me.jangjunha.ftgo.apigateway.proxies

import com.google.protobuf.Empty
import io.grpc.ManagedChannelBuilder
import io.grpc.stub.StreamObserver
import me.jangjunha.ftgo.apigateway.Destinations
import me.jangjunha.ftgo.kitchen_service.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
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

    suspend fun findTicketById(id: UUID): Ticket = suspendCoroutine { cont ->
        val payload = getTicketPayload {
            ticketId = id.toString()
        }
        stub.getTicket(payload, object : StreamObserver<Ticket> {
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

    suspend fun acceptTicket(id: UUID): Unit = suspendCoroutine { cont ->
        val payload = acceptTicketPayload {
            ticketId = id.toString()
        }
        stub.acceptTicket(payload, object : StreamObserver<Empty> {
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

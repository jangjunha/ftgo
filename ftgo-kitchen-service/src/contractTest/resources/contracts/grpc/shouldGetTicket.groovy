package contracts.grpc

import org.springframework.cloud.contract.spec.Contract
import org.springframework.cloud.contract.verifier.http.ContractVerifierHttpMetaData

Contract.make {
    request {
        method 'POST'
        url '/me.jangjunha.ftgo.kitchen_service.KitchenService/GetTicket'
        body(fileAsBytes("GetTicketPayload_b9f2606f-2cdf-458c-95c7-b3fd48750d11.bin"))
        headers {
            contentType("application/grpc")
            header("te", "trailers")
        }
    }
    response {
        status 200
        body(fileAsBytes("Ticket_AWAITING_ACCEPTANCE.bin"))
        headers {
            contentType("application/grpc")
            header("grpc-encoding", "identity")
            header("grpc-accept-encoding", "gzip")
        }
    }
    metadata([
            "verifierHttp": [
                    "protocol": ContractVerifierHttpMetaData.Protocol.H2_PRIOR_KNOWLEDGE.toString()
            ]
    ])
}

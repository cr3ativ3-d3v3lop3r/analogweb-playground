package com.cooper

import analogweb._
import circe._
import io.circe._
import generic.semiauto._
import org.analogweb.core.response.HttpStatus
import org.analogweb.scala.Route
import scala.language.postfixOps

object Http {
  final case class IncomingRequest(transaction: String)
  final case class OutboundResponse(status: Int, message: String)

  implicit val incomingRequestDecoder: Decoder[IncomingRequest]   = deriveDecoder[IncomingRequest]
  implicit val outboundResponseEncoder: Encoder[OutboundResponse] = deriveEncoder[OutboundResponse]
}

object Server {
  import Http._

  val endPoints: Seq[Route] =
    get("/healthcheck")(_ => Ok) ++
    post("/process-transaction") { implicit request =>

      val process: IncomingRequest => HttpStatus = incomingRequest => {
        val outboundResponse: OutboundResponse =
          OutboundResponse(
            status  = Ok.getStatusCode,
            message = s"Incoming request has been parsed successfully for transaction ${incomingRequest.transaction}..."
          )

        Ok(asJson(outboundResponse))
      }

      json.as[IncomingRequest].map(process)
        .getOrElse(BadRequest)
    }

  def main(args: Array[String]): Unit = {
    val port = 9000
    val host = "localhost"

    http(host, port)(endPoints).run()
  }
}
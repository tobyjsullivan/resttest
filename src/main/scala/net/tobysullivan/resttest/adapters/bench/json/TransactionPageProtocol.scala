package net.tobysullivan.resttest.adapters.bench.json

import net.tobysullivan.resttest.adapters.bench.models.TransactionPage
import net.tobysullivan.resttest.models.Transaction
import org.joda.time.format.ISODateTimeFormat
import spray.json._

import scala.util.{Failure, Success, Try}

/**
  * This is a protocol for use with spray-json which defines the JSON formats of the Bench API.
  */
object TransactionPageProtocol extends DefaultJsonProtocol {
  private val dateFormat = ISODateTimeFormat.date()

  implicit object TransactionFormat extends RootJsonFormat[Transaction] {
    override def read(json: JsValue): Transaction =
      json.asJsObject.getFields("Date", "Amount") match {
        case Seq(JsString(dateString), JsString(amountString)) => tryParseTransaction(dateString, amountString) match {
          case Success(txn) => txn
          case Failure(e) => deserializationError("Unexepected transaction data.", e)
        }
        case _ => deserializationError("Expected Transaction")
      }

    private def tryParseTransaction(dateString: String, amountString: String): Try[Transaction] = Try {
      Transaction(
        dateFormat.parseLocalDate(dateString),
        java.lang.Double.parseDouble(amountString)
      )
    }

    // We don't need a writer so not implemented
    override def write(obj: Transaction): JsValue = ???
  }

  implicit object TransactionPageFormat extends RootJsonFormat[TransactionPage] {
    override def read(json: JsValue): TransactionPage =
      json.asJsObject.getFields("transactions") match {
        case Seq(JsArray(txnObjs)) =>
          val txns = txnObjs.map(_.convertTo[Transaction])
          TransactionPage(txns)
        case _ => deserializationError("Expected a list of transactions")
      }

    // We don't need a writer so not implemented
    override def write(obj: TransactionPage): JsValue = ???
  }
}

package net.tobysullivan.resttest.adapters.bench.json

import net.tobysullivan.resttest.adapters.bench.models.TransactionPage
import net.tobysullivan.resttest.models.Transaction
import org.joda.time.format.ISODateTimeFormat
import spray.json._

import scala.util.{Try}

/**
  * This is a protocol for use with spray-json which defines the JSON formats of the Bench API.
  */
object TransactionPageProtocol extends DefaultJsonProtocol {
  private val dateFormat = ISODateTimeFormat.date()

  /**
    * This TransactionFormat object contains the logic for parsing individual Transactions from the Bench API.
    */
  implicit object TransactionFormat extends RootJsonFormat[Transaction] {
    override def read(json: JsValue): Transaction =
      // Extract only the relevant fields needed for the Transaction object
      json.asJsObject.getFields("Date", "Amount") match {
        case Seq(JsString(dateString), JsString(amountString)) =>
          tryParseTransactionElements(dateString, amountString).recoverWith[Transaction]{
            case e => deserializationError("Unexepected transaction data.", e)
          }.get
        case _ => deserializationError("Expected a transaction.")
      }

    private def tryParseTransactionElements(dateString: String, amountString: String): Try[Transaction] = Try {
      Transaction(
        dateFormat.parseLocalDate(dateString),
        java.lang.Double.parseDouble(amountString)
      )
    }

    // We don't need a writer so not implemented
    override def write(obj: Transaction): JsValue = ???
  }

  /**
    * This TransactionPageFormat object contains the logic for parsing a complete page of transactions from
    * the Bench API.
    */
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

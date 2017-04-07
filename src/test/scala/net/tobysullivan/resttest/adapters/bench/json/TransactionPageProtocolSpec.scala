package net.tobysullivan.resttest.adapters.bench.json

import TransactionPageProtocol._
import net.tobysullivan.resttest.adapters.bench.models.TransactionPage
import net.tobysullivan.resttest.models.Transaction
import org.joda.time.LocalDate
import spray.json._
import org.scalatest.{FunSpec, Matchers}

class TransactionPageProtocolSpec extends FunSpec with Matchers {
  describe("TransactionPageProtocol") {
    describe("TransactionFormat") {
      describe(".read") {
        describe("with a properly formatted JSON value") {
          val json =
            """
              |   {
              |			"Date": "2013-12-21",
              |			"Ledger": "Travel Expense, Nonlocal",
              |			"Amount": "-8.1",
              |			"Company": "BLACK TOP CABS VANCOUVER BC"
              |		}
            """.stripMargin

          it ("should return the expected Transaction") {
            val transcation = json.parseJson.convertTo[Transaction]

            transcation should be (Transaction(new LocalDate(2013, 12, 21), -8.1))
          }
        }

        describe("with a missing amount field") {
          val json =
            """
              |   {
              |			"Date": "2013-12-21",
              |			"Ledger": "Travel Expense, Nonlocal",
              |			"Company": "BLACK TOP CABS VANCOUVER BC"
              |		}
            """.stripMargin

          it("should throw a deserialization error") {
            assertThrows[DeserializationException] {
              json.parseJson.convertTo[Transaction]
            }
          }
        }

        describe("with a missing date field") {
          val json =
            """
              |   {
              |			"Ledger": "Travel Expense, Nonlocal",
              |  		"Amount": "-8.1",
              |			"Company": "BLACK TOP CABS VANCOUVER BC"
              |		}
            """.stripMargin

          it("should throw a deserialization error") {
            assertThrows[DeserializationException] {
              json.parseJson.convertTo[Transaction]
            }
          }
        }
      }
    }

    describe("TransactionPageFormat") {
      describe(".read") {
        describe("with a properly formatted json payload") {
          val json =
            """
              |{
              |	"totalCount": 38,
              |	"page": 1,
              |	"transactions": [
              |		{
              |			"Date": "2013-12-22",
              |			"Ledger": "Phone & Internet Expense",
              |			"Amount": "-110.71",
              |			"Company": "SHAW CABLESYSTEMS CALGARY AB"
              |		},
              |		{
              |			"Date": "2013-12-21",
              |			"Ledger": "Travel Expense, Nonlocal",
              |			"Amount": "-8.1",
              |			"Company": "BLACK TOP CABS VANCOUVER BC"
              |		}
              |	]
              |}
            """.stripMargin

          it("Should return the expected TransactionPage instance") {
            val page = json.parseJson.convertTo[TransactionPage]

            page.transactions.length should be (2)
          }
        }
      }
    }
  }
}

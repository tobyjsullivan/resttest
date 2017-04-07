package net.tobysullivan.resttest.adapters.bench

import net.tobysullivan.resttest.adapters.http.JsonFetcher
import net.tobysullivan.resttest.models.Transaction
import org.joda.time.LocalDate
import org.scalatest.{FunSpec, Matchers}
import spray.json._

import scala.util.{Success, Try}

class BenchAdapterSpec extends FunSpec with Matchers {
  class TestFetcher(responder: (String) => Try[Option[JsValue]]) extends JsonFetcher {
    def fetchSync(url: String): Try[Option[JsValue]] = responder(url)
  }

  private val testPage1 =
    """
      |{
      |	"totalCount": 7,
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
      |		},
      |		{
      |			"Date": "2013-12-21",
      |			"Ledger": "Business Meals & Entertainment Expense",
      |			"Amount": "-9.88",
      |			"Company": "GUILT & CO. VANCOUVER BC"
      |		}
      |	]
      |}
    """.stripMargin

  private val testPage2 =
    """
      |{
      |	"totalCount": 7,
      |	"page": 2,
      |	"transactions": [
      |		{
      |			"Date": "2013-12-19",
      |			"Ledger": "Travel Expense, Nonlocal",
      |			"Amount": "-200",
      |			"Company": "YELLOW CAB COMPANY LTD VANCOUVER"
      |		},
      |		{
      |			"Date": "2013-12-18",
      |			"Ledger": "Business Meals & Entertainment Expense",
      |			"Amount": "-8.94",
      |			"Company": "NESTERS MARKET #x0064 VANCOUVER BC"
      |		},
      |		{
      |			"Date": "2013-12-18",
      |			"Ledger": "Travel Expense, Nonlocal",
      |			"Amount": "-9.55",
      |			"Company": "VANCOUVER TAXI VANCOUVER BC"
      |		}
      |	]
      |}
    """.stripMargin

  private val testPage3 =
    """
      |{
      |	"totalCount": 7,
      |	"page": 3,
      |	"transactions": [
      |		{
      |			"Date": "2013-12-17",
      |			"Ledger": "",
      |			"Amount": "907.85",
      |			"Company": "PAYMENT RECEIVED - THANK YOU"
      |		}
      |	]
      |}
    """.stripMargin


  describe("BenchAdapter") {
    describe("when there are three pages of results") {
      val fetcher = new TestFetcher({
        case url if url.endsWith("/transactions/1.json") => Success(Some(testPage1.parseJson))
        case url if url.endsWith("/transactions/2.json") => Success(Some(testPage2.parseJson))
        case url if url.endsWith("/transactions/3.json") => Success(Some(testPage3.parseJson))
        case _ => Success(None)
      })
      val bench = new BenchAdapter(fetcher)

      describe(".allTransactions") {
        val txns = bench.allTransactions().force

        txns.length should be (7)

        txns should contain (Transaction(new LocalDate(2013, 12, 21), -8.1))
        txns should contain (Transaction(new LocalDate(2013, 12, 19), -200.0))
        txns should contain (Transaction(new LocalDate(2013, 12, 18), -9.55))
        txns should contain (Transaction(new LocalDate(2013, 12, 17), 907.85))
      }
    }
  }
}

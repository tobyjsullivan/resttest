package net.tobysullivan.resttest.adapters.bench

import net.tobysullivan.resttest.adapters.bench.models.TransactionPage
import net.tobysullivan.resttest.adapters.http.JsonFetcher
import net.tobysullivan.resttest.models.Transaction

import com.typesafe.config.ConfigFactory

import scala.util.{Failure, Success}

class BenchAdapter(fetcher: JsonFetcher) {
  private val conf = ConfigFactory.load()

  private val API_BASE_URL = conf.getString("bench.api.baseUrl")
  private val STARTING_PAGE = 1

  def allTransactions(): Stream[Transaction] = pages.flatMap(_.toStream)

  private def fetchTransactionPage(pageNum: Int): Option[Seq[Transaction]] = {
    import net.tobysullivan.resttest.adapters.bench.json.TransactionPageProtocol._

    // Fetch the transaction page from the API and parse JSON. The fetcher returns None if the API produces a 404.
    fetcher.fetchSync(s"$API_BASE_URL/transactions/$pageNum.json") match {
      case Success(Some(json)) =>
        val page = json.convertTo[TransactionPage]
        Some(page.transactions)
      case Success(None) => None
      case Failure(e) =>
        // Error behaviour is currently undefined so rethrow.
        throw e
    }
  }

  private lazy val pages: Stream[Seq[Transaction]] = {
    def loop(pageNum: Int): Stream[Seq[Transaction]] = fetchTransactionPage(pageNum) match {
        case Some(txns) => txns #:: loop(pageNum + 1)
        case None => Stream.empty
      }

    loop(STARTING_PAGE)
  }
}

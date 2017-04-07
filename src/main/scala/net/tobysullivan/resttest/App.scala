package net.tobysullivan.resttest

import net.tobysullivan.resttest.adapters.bench.BenchAdapter
import net.tobysullivan.resttest.adapters.http.{JsonFetcher, JsonFetcherImpl}
import net.tobysullivan.resttest.cli.DisplayFormat
import net.tobysullivan.resttest.models.Statement

import scala.util.{Failure, Success, Try}

object App {
  def main(args: Array[String]) {
    // Initialize the adapters
    val fetcher: JsonFetcher = JsonFetcherImpl
    val bench = new BenchAdapter(fetcher)

    // Get a Statement object from the Bench adapter
    Try {
      Statement(bench.allTransactions())
    } match {
      case Success(stmt) =>
        // Format the statement and print to screen
        println(DisplayFormat.format(stmt))
      case Failure(e) =>
        println("Uh oh! There was an unexpected error. Maybe a configuration issue?")
        println("ERROR " + e.toString)
        e.printStackTrace()
        System.exit(1)
    }
  }
}

package net.tobysullivan.resttest

import net.tobysullivan.resttest.adapters.bench.BenchAdapter
import net.tobysullivan.resttest.adapters.http.{JsonFetcher, JsonFetcherImpl}
import net.tobysullivan.resttest.cli.DisplayFormat
import net.tobysullivan.resttest.models.Statement

object App {
  def main(args: Array[String]) {
    // Initialize the adapters
    val fetcher: JsonFetcher = JsonFetcherImpl
    val bench = new BenchAdapter(fetcher)

    // Get a Statement object from the Bench adapter
    val stmt: Statement = Statement(bench.allTransactions())

    // Format the statement and print to screen
    println(DisplayFormat.format(stmt))
  }
}

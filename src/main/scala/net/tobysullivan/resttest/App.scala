package net.tobysullivan.resttest

import net.tobysullivan.resttest.adapters.bench.BenchAdapter
import net.tobysullivan.resttest.adapters.http.JsonFetcher
import net.tobysullivan.resttest.cli.DisplayFormat
import net.tobysullivan.resttest.models.Statement

object App {
  def main(args: Array[String]) {
    // Initialize the adapters
    val fetcher: JsonFetcher = ???
    val bench = new BenchAdapter(fetcher)

    val stmt: Statement = Statement(bench.allTransactions().toSet)

    println(DisplayFormat.format(stmt))
  }
}

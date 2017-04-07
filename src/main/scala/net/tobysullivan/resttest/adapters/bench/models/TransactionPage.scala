package net.tobysullivan.resttest.adapters.bench.models

import net.tobysullivan.resttest.models.Transaction

/**
  * An instance of TransactionPage represents a single page of the Bench API.
  * @param transactions The list of transactions included on the page
  */
case class TransactionPage(transactions: Seq[Transaction])

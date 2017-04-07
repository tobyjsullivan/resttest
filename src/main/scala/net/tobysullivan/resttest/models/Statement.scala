package net.tobysullivan.resttest.models

import org.joda.time.LocalDate

/**
  * Represents a complete statement of transactions. Operates on a Stream of Transactions so that
  * aggregation operations on very large volumes should only consume O(1) memory. Well, technically
  * O(M) memory where M is # of different dates.
  * @param transactions A stream of transactions to include in the statement.
  */
case class Statement(transactions: Stream[Transaction]) {
  // We could use `.sum` but opted for the foldLeft to make it clear that we are progressively consuming the stream
  lazy val totalBalance: Double = transactions.map(_.amount).foldLeft(0.0){ (sum, amount) => sum + amount}

  lazy val balancesByDate: Map[LocalDate, Double] = transactionsByDate.mapValues(_.foldLeft(0.0) { (sum, txn) => sum + txn.amount })

  private lazy val transactionsByDate: Map[LocalDate, Set[Transaction]] =
    transactions.foldLeft(Map[LocalDate, Set[Transaction]]()){ (m, txn) =>
      val currentSet: Set[Transaction] = m.getOrElse(txn.date, Set[Transaction]())
      m + (txn.date -> (currentSet + txn))
    }
}

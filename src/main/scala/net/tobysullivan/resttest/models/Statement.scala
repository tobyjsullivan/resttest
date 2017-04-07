package net.tobysullivan.resttest.models

import org.joda.time.LocalDate

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

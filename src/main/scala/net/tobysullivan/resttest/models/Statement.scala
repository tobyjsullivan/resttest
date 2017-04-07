package net.tobysullivan.resttest.models

import org.joda.time.LocalDate

/**
  * Represents a complete statement of transactions. Operates on a Stream of Transactions so that
  * aggregation operations on very large volumes should only consume O(1) memory. Well, technically
  * O(M) memory where M is # of different dates present.
  * @param transactions A stream of transactions to include in the statement.
  */
case class Statement(transactions: Stream[Transaction]) {
  // We could use `.sum` but opted for the foldLeft to make it clear that we are progressively consuming the stream
  lazy val totalBalance: Double = accruedDailyBalances match {
    case head :: _ => head._2
    case Nil => 0.0
  }

  // Returns an ordered set (most recent date first) of mappings from each date to the accrued balance
  lazy val accruedDailyBalances: Seq[(LocalDate, Double)] = {
    // Get a sorted list of all dates represented in the statement
    val dates = datesCovered()

    // Build up a new list of mappings from each date to the ACCRUED total on that date
    val (_, accruedBalances) = dates.foldLeft(0.0, Seq[(LocalDate, Double)]()) {
      case ((accruedBalance, balances), date) =>
        val updatedBalance = accruedBalance + totalsByDate.getOrElse(date, 0.0)
        (updatedBalance, balances :+ (date -> updatedBalance))
    }

    // Reverse the list so that most recent date is at head of list
    accruedBalances.reverse
  }

  // This iterates through the stream and calculates totals for each date without having to hold all transactions in memory
  private lazy val totalsByDate: Map[LocalDate, Double] =
    transactions.foldLeft(Map[LocalDate, Double]()) { (m, txn) =>
      val currentTotal: Double = m.getOrElse(txn.date, 0.0)
      m + (txn.date -> (currentTotal + txn.amount))
    }

  private def datesCovered(): Seq[LocalDate] = totalsByDate.keys.toSeq.sortWith((d1, d2) => d1.isBefore(d2))
}

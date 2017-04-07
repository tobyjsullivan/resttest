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
    // Sort the list of daily balances by date
    val sortedMappings = balancesByDate.toSeq.sortWith((kv1, kv2) => kv1._1.isBefore(kv2._1))

    // Build up a new list of mappings from each date to the ACCRUED total on that date
    val (_, accruedBalances) = sortedMappings.foldLeft(Tuple2(0.0, Seq[(LocalDate, Double)]())) {
      case ((accruedBalance, priorBalances), (currentDate, currentBalance)) =>
        val newBalance = accruedBalance + currentBalance

        (newBalance, priorBalances :+ (currentDate -> newBalance))
    }

    // Reverse the list so that most recent date is at head of list
    accruedBalances.reverse
  }

  private lazy val balancesByDate: Map[LocalDate, Double] = transactionsByDate.mapValues(_.foldLeft(0.0) { (sum, txn) => sum + txn.amount })

  private lazy val transactionsByDate: Map[LocalDate, Set[Transaction]] =
    transactions.foldLeft(Map[LocalDate, Set[Transaction]]()){ (m, txn) =>
      val currentSet: Set[Transaction] = m.getOrElse(txn.date, Set[Transaction]())
      m + (txn.date -> (currentSet + txn))
    }
}

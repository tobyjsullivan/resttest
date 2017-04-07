package net.tobysullivan.resttest.models

import org.joda.time.LocalDate

case class Statement(txns: Set[Transaction]) {
  lazy val transactionsByDate: Map[LocalDate, Set[Transaction]] = txns.groupBy(_.date)

  lazy val totalBalance: Double = txns.map(_.amount).sum

  lazy val balancesByDate: Map[LocalDate, Double] = transactionsByDate.mapValues(_.foldLeft(0.0) { (sum, txn) => sum + txn.amount })
}

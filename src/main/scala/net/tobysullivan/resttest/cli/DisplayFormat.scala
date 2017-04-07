package net.tobysullivan.resttest.cli

import net.tobysullivan.resttest.models.Statement
import org.joda.time.LocalDate
import org.joda.time.format.ISODateTimeFormat

object DisplayFormat {
  private val dateFormat = ISODateTimeFormat.date()

  def format(statement: Statement): String = s"""
                                               |Total Balance: ${moneyFmt(statement.totalBalance)}
                                               |
                                               |Daily Balances
                                               |${dailyBalances(statement.balancesByDate)}
                                               |""".stripMargin

  private def moneyFmt(value: Double): String = "$%.2f".format(value)

  private def dailyBalances(balances: Map[LocalDate, Double]): String = {
    val sortedBalances = balances.toSeq.sortWith((kv1, kv2)  => kv1._1.isAfter(kv2._1))

    sortedBalances.map {
      case (date, amount) => s"${dateFormat.print(date)}: ${moneyFmt(amount)}"
    }.mkString("\n")
  }
}

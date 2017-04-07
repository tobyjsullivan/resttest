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
                                               |${dailyBalances(statement.accruedDailyBalances)}
                                               |""".stripMargin

  private def moneyFmt(value: Double): String = "$%.2f".format(value)

  private def dailyBalances(balances: Seq[(LocalDate, Double)]): String = balances match {
    case Nil =>
      "-- No transactions found. --"
    case _ =>
      balances.map {
        case (date, amount) => s"${dateFormat.print(date)}: ${moneyFmt(amount)}"
      }.mkString("\n")
  }
}

package net.tobysullivan.resttest.cli

import net.tobysullivan.resttest.models.Statement

object DisplayFormat {
  def format(statement: Statement): String = """
                                               |Total Balance: $0.00
                                               |
                                               |Daily Balances
                                               |-- No transaction history. --
                                               |""".stripMargin
}

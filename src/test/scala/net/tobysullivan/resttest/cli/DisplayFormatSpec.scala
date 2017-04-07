package net.tobysullivan.resttest.cli

import net.tobysullivan.resttest.models.{Statement, Transaction}
import org.joda.time.LocalDate
import org.scalatest.{FunSpec, Matchers}

class DisplayFormatSpec extends FunSpec with Matchers {
  describe("DisplayFormat") {
    describe(".format") {
      describe("with a statement of multiple transactions") {
        val statement = Statement(
          Transaction(new LocalDate(2013, 12, 25), 20.76) #::
          Transaction(new LocalDate(2013, 12, 21), -5.68) #::
          Transaction(new LocalDate(2013, 12, 21), -7.96) #::
          Transaction(new LocalDate(2013, 12, 22), -37.65) #::
          Stream.empty
        )
        it("should output the expected format") {
          val out = DisplayFormat.format(statement)

          out.trim should be (
            """
              |Total Balance: $-30.53
              |
              |Daily Balances
              |2013-12-25: $20.76
              |2013-12-22: $-37.65
              |2013-12-21: $-13.64
            """.stripMargin.trim)
        }
      }

      describe("with an empty statement") {
        val statement = Statement(Stream.empty)

        it("should display a zero balance and a notice") {
          val out = DisplayFormat.format(statement)

          out.trim should be (
            """
              |Total Balance: $0.00
              |
              |Daily Balances
              |-- No transactions found. --
            """.stripMargin.trim)
        }
      }
    }
  }
}

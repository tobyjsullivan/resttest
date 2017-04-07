package net.tobysullivan.resttest.models

import org.joda.time.LocalDate
import org.scalatest.{FunSpec, Matchers}

class StatementSpec extends FunSpec with Matchers {
  val testTxn1 = Transaction(
    new LocalDate(2013, 12, 22),
    -110.71
  )

  val testTxn2 = Transaction(
    new LocalDate(2013, 12, 21),
    -8.1
  )

  val testTxn3 = Transaction(
    new LocalDate(2013, 12, 21),
    -9.88
  )

  val testTxn4 = Transaction(
    new LocalDate(2013, 12, 25),
    25.89
  )


  describe("Statement") {
    describe("with multiple transactions") {
      val statement = Statement(
        testTxn1 #:: testTxn2 #:: testTxn3 #:: testTxn4 #:: Stream.empty
      )

      describe(".totalBalance") {
        it("should be the sum of all transaction") {
          statement.totalBalance should be (-102.80)
        }
      }

      describe(".accruedDailyBalances") {
        it("should return a map of each date to the accrued balance up to and including that day's transactions") {
          statement.accruedDailyBalances should be (Seq(
            new LocalDate(2013, 12, 25) -> -102.80,
            new LocalDate(2013, 12, 22) -> -128.69,
            new LocalDate(2013, 12, 21) -> -17.98
          ))
        }
      }
    }
  }
}

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


  describe("Statement") {
    describe("with multiple transactions") {
      val statement = Statement(
        Set(testTxn1, testTxn2, testTxn3)
      )
      describe(".transactionsByDate") {
        it("should return a map with all dates and transactions") {
          val result = statement.transactionsByDate

          result should be (Map(
            new LocalDate(2013, 12, 22) -> Set(testTxn1),
            new LocalDate(2013, 12, 21) -> Set(testTxn2, testTxn3)
          ))
        }
      }

      describe(".totalBalance") {
        it("should be the sum of all transaction") {
          statement.totalBalance should be (-128.69)
        }
      }

      describe(".balancesByDate") {
        it("should return a map of each date to the total balance for that day") {
          statement.balancesByDate should be (Map(
            new LocalDate(2013, 12, 22) -> -110.71,
            new LocalDate(2013, 12, 21) -> -17.98
          ))
        }
      }
    }
  }
}

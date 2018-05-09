import java.time.LocalDate

import org.scalatest.FunSuite
import utils.Utils

class UtilsSpec extends FunSuite{
    test("A timeparser should parse and get date correctly") {
      val sample ="2013-04-04 03:24:24 +0000"
      val date = Utils.getDatefromString(sample)
      assert(date == LocalDate.of(2013, 4, 4))
    }
}

import java.time.LocalDate

import org.scalatest.FunSuite
import utils.Utils

class UtilsSpec extends FunSuite{
  test("twitter parser should parse date from response correctly"){
    val date = "Wed Aug 27 13:08:45 +0000 2008"
    val parseddate = Utils.parseDate(date)
    val expectedDate = LocalDate.of(2008, 8, 27).toString
    assert(parseddate == expectedDate)
  }
}

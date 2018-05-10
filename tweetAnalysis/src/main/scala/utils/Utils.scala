package utils

import java.text.SimpleDateFormat
import java.time.LocalDate

object Utils {
  def getDatefromString(timeStr : String)={
    val formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
    val date = formatter.parse(timeStr)
    println(s"(((((((( date to be parsed : ${timeStr} with year : ${date.getYear+1900}, month: ${date.getMonth}, day: ${date.getDay}")
    LocalDate.of(date.getYear+1900, date.getMonth+1, date.getDate).toString
  }
}

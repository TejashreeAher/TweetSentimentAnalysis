package com.twitter

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Locale

object Utils {
  def parseDate(date: String) = {
    val twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
    // Important note. Only ENGLISH Locale works.
    val sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
    sf.setLenient(true);
    val parsedDate = sf.parse(date);
    LocalDate
      .of(parsedDate.getYear + 1900,
          parsedDate.getMonth + 1,
          parsedDate.getDate)
      .toString
  }
}

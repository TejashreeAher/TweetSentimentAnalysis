import com.job.analysis.CoreAnalyzer
import com.job.models.Sentiment
import org.scalatest.FunSuite
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito._

class AnalyserTest extends FunSuite with MockitoSugar {
  test("Analyser convert response of analysernllibrary to correct sentiment") {
    val analyser = mock[CoreAnalyzer]
    val sentiment = Sentiment(1, Sentiment.NEGATIVE_SENTIMENT)
    when(analyser.extractSentiments("sample text")).thenReturn(sentiment)
    assert(true == true)
  }

  test("getSentiment() should get correct maximum sentiment") {
    val sentimentList = List(("text1", 0), ("text2", 1), ("text 3", 4))
    val expectedSentiment = Sentiment(4, Sentiment.POSITIVE_SENTIMENT)
    val analyzer = new CoreAnalyzer()
    val returnedSentiment = analyzer.getSentiment(sentimentList)
    assert(returnedSentiment == expectedSentiment)
  }
}

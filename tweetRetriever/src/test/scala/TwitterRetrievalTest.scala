import java.util

import com.twitter.mapper.{Tweet, TweetMapper}
import org.apache.flink.api.common.functions.util.ListCollector
import org.scalatest.FunSuite

class TwitterRetrievalTest extends FunSuite{
    test("twitter parser should be able to parse received tweet propery"){
      val tweetRcvd =
      """{"created_at":"Sun May 13 13:13:41 +0000 2018","id":995653308240089088,"id_str":"995653308240089088","text":"RT @Israel: We have a winner! \ud83c\udfc6\ud83c\uddee\ud83c\uddf1\ud83c\udfb5\n\nSO PROUD of our @NettaBarzilai, the winner of tonight's #Eurovision \n\nCongratulations from Israel to AL\u2026","source":"\u003ca href=\"http://twitter.com/download/android\" rel=\"nofollow\"\u003eTwitter for Android\u003c/a\u003e","truncated":false,"in_reply_to_status_id":null,"in_reply_to_status_id_str":null,"in_reply_to_user_id":null,"in_reply_to_user_id_str":null,"in_reply_to_screen_name":null,"user":{"id":51290826,"id_str":"51290826","name":"Willo Carde\u00f1a","screen_name":"britno_","location":"Yucat\u00e1n, M\u00e9xico","url":null,"description":"Yucateco, so\u00f1ador, amo la m\u00fasica, el dise\u00f1o y dar lo mejor por mi gente. #NoALaViolencia\n90tero, #BArmy y #Eurofan !!","translator_type":"none","protected":false,"verified":false,"followers_count":694,"friends_count":1161,"listed_count":4,"favourites_count":2150,"statuses_count":30851,"created_at":"Sat Jun 27 00:36:21 +0000 2009","utc_offset":-21600,"time_zone":"Mountain Time (US & Canada)","geo_enabled":true,"lang":"es","contributors_enabled":false,"is_translator":false,"profile_background_color":"000000","profile_background_image_url":"http://pbs.twimg.com/profile_background_images/639267995055865857/VmDrAdup.jpg","profile_background_image_url_https":"https://pbs.twimg.com/profile_background_images/639267995055865857/VmDrAdup.jpg","profile_background_tile":true,"profile_link_color":"FAB81E","profile_sidebar_border_color":"000000","profile_sidebar_fill_color":"000000","profile_text_color":"000000","profile_use_background_image":true,"profile_image_url":"http://pbs.twimg.com/profile_images/981599753078296576/ws3hCpiG_normal.jpg","profile_image_url_https":"https://pbs.twimg.com/profile_images/981599753078296576/ws3hCpiG_normal.jpg","profile_banner_url":"https://pbs.twimg.com/profile_banners/51290826/1526090006","default_profile":false,"default_profile_image":false,"following":null,"follow_request_sent":null,"notifications":null},"geo":null,"coordinates":null,"place":null,"contributors":null,"retweeted_status":{"created_at":"Sat May 12 22:53:52 +0000 2018","id":995436928500723712,"id_str":"995436928500723712","text":"We have a winner! \ud83c\udfc6\ud83c\uddee\ud83c\uddf1\ud83c\udfb5\n\nSO PROUD of our @NettaBarzilai, the winner of tonight's #Eurovision \n\nCongratulations from\u2026 https://t.co/4nrOhVhz2T","display_text_range":[0,140],"source":"\u003ca href=\"http://twitter.com/download/android\" rel=\"nofollow\"\u003eTwitter for Android\u003c/a\u003e","truncated":true,"in_reply_to_status_id":null,"in_reply_to_status_id_str":null,"in_reply_to_user_id":null,"in_reply_to_user_id_str":null,"in_reply_to_screen_name":null,"user":{"id":52660746,"id_str":"52660746","name":"Israel \u05d9\u05e9\u05e8\u05d0\u05dc","screen_name":"Israel","location":"Jerusalem, Israel","url":"http://www.israel.org","description":"The State of Israel's official twitter channel, maintained by @IsraelMFA's Digital Diplomacy Team. Follow us also on Telegram: https://t.me/IsraelMFA","translator_type":"regular","protected":false,"verified":true,"followers_count":497826,"friends_count":5362,"listed_count":4133,"favourites_count":892,"statuses_count":25873,"created_at":"Wed Jul 01 07:15:30 +0000 2009","utc_offset":10800,"time_zone":"Jerusalem","geo_enabled":true,"lang":"en","contributors_enabled":false,"is_translator":false,"profile_background_color":"0C2A66","profile_background_image_url":"http://pbs.twimg.com/profile_background_images/211860828/126.jpg","profile_background_image_url_https":"https://pbs.twimg.com/profile_background_images/211860828/126.jpg","profile_background_tile":false,"profile_link_color":"0E0E70","profile_sidebar_border_color":"100F33","profile_sidebar_fill_color":"CCD4E8","profile_text_color":"000000","profile_use_background_image":true,"profile_image_url":"http://pbs.twimg.com/profile_images/460810701984919552/1TtTPTnj_normal.png","profile_image_url_https":"https://pbs.twimg.com/profile_images/460810701984919552/1TtTPTnj_normal.png","profile_banner_url":"https://pbs.twimg.com/profile_banners/52660746/1428833215","default_profile":false,"default_profile_image":false,"following":null,"follow_request_sent":null,"notifications":null},"geo":null,"coordinates":null,"place":null,"contributors":null,"is_quote_status":false,"extended_tweet":{"full_text":"We have a winner! \ud83c\udfc6\ud83c\uddee\ud83c\uddf1\ud83c\udfb5\n\nSO PROUD of our @NettaBarzilai, the winner of tonight's #Eurovision \n\nCongratulations from Israel to ALL talented performers!\n\nSee you all next year in #Eurovision2019... in Israel! https://t.co/EItHfXIxfQ","display_text_range":[0,205],"entities":{"hashtags":[{"text":"Eurovision","indices":[80,91]},{"text":"Eurovision2019","indices":[176,191]}],"urls":[],"user_mentions":[{"screen_name":"NettaBarzilai","name":"Netta Barzilai","id":972800740862382082,"id_str":"972800740862382082","indices":[40,54]}],"symbols":[],"media":[{"id":995436917641613313,"id_str":"995436917641613313","indices":[206,229],"media_url":"http://pbs.twimg.com/media/DdCAmkBWkAEn3m-.jpg","media_url_https":"https://pbs.twimg.com/media/DdCAmkBWkAEn3m-.jpg","url":"https://t.co/EItHfXIxfQ","display_url":"pic.twitter.com/EItHfXIxfQ","expanded_url":"https://twitter.com/Israel/status/995436928500723712/photo/1","type":"photo","sizes":{"thumb":{"w":150,"h":150,"resize":"crop"},"small":{"w":680,"h":292,"resize":"fit"},"large":{"w":1440,"h":619,"resize":"fit"},"medium":{"w":1200,"h":516,"resize":"fit"}}}]},"extended_entities":{"media":[{"id":995436917641613313,"id_str":"995436917641613313","indices":[206,229],"media_url":"http://pbs.twimg.com/media/DdCAmkBWkAEn3m-.jpg","media_url_https":"https://pbs.twimg.com/media/DdCAmkBWkAEn3m-.jpg","url":"https://t.co/EItHfXIxfQ","display_url":"pic.twitter.com/EItHfXIxfQ","expanded_url":"https://twitter.com/Israel/status/995436928500723712/photo/1","type":"photo","sizes":{"thumb":{"w":150,"h":150,"resize":"crop"},"small":{"w":680,"h":292,"resize":"fit"},"large":{"w":1440,"h":619,"resize":"fit"},"medium":{"w":1200,"h":516,"resize":"fit"}}}]}},"quote_count":63,"reply_count":125,"retweet_count":642,"favorite_count":2045,"entities":{"hashtags":[{"text":"Eurovision","indices":[80,91]}],"urls":[{"url":"https://t.co/4nrOhVhz2T","expanded_url":"https://twitter.com/i/web/status/995436928500723712","display_url":"twitter.com/i/web/status/9\u2026","indices":[116,139]}],"user_mentions":[{"screen_name":"NettaBarzilai","name":"Netta Barzilai","id":972800740862382082,"id_str":"972800740862382082","indices":[40,54]}],"symbols":[]},"favorited":false,"retweeted":false,"possibly_sensitive":false,"filter_level":"low","lang":"en"},"is_quote_status":false,"quote_count":0,"reply_count":0,"retweet_count":0,"favorite_count":0,"entities":{"hashtags":[{"text":"Eurovision","indices":[92,103]}],"urls":[],"user_mentions":[{"screen_name":"Israel","name":"Israel \u05d9\u05e9\u05e8\u05d0\u05dc","id":52660746,"id_str":"52660746","indices":[3,10]},{"screen_name":"NettaBarzilai","name":"Netta Barzilai","id":972800740862382082,"id_str":"972800740862382082","indices":[52,66]}],"symbols":[]},"favorited":false,"retweeted":false,"filter_level":"low","lang":"en","timestamp_ms":"1526217221685"}
        """
      val tweetMapper = new TweetMapper()
      val list = new util.ArrayList[Tweet]()
      val out  = new ListCollector[Tweet](list)
      tweetMapper.flatMap(tweetRcvd, out)
      assert(list.size() == 1)
    }
}
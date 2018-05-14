export SPARK_HOME=/Users/tejashree.aher/Documents/Sofware/spark-2.3.0-bin-hadoop2.7
echo $1
$SPARK_HOME/bin/spark-submit --class "com.job.TweetAnalysisJob" --master local[4] tweetAnalysis/target/scala-2.11/tweet-sentiment-analysis-assembly-0.0.1.jar -d $1 --keyword $2
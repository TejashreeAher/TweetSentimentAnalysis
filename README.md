# TweetSentimentAnalysis
A Spark job to analyse tweets in a batch mode

Set correct spark home using :
export SPARK_HOME=/your/spark/home
This is needed in the `run-job.sh` script

create table text_table(id int, dt string,name string) stored as textfile location '/user/yashu/text_table';

There are 2 jobs :
1. tweetRetriever : This is a flink job that listens to Twitter stream continuously and writes tweets to files grouped by date (execution date and not tweets date).
But as te tweets are streamed, this date also co-incides with the tweets date.

The input to this job are:
    1. twitter.properties file that has twitter credentials
    2. the date from which we need to fetch historic tweets (Bootstrap process, more on it below). The format is "YYYY-MM-DD".
    Absence of date means there is not need to bootstrap

2. tweetAnalysis : This is a spark job that runs in a batch mode once a day. The job aggregates gets tweets for the last day (stored by the flink job)
and processes it and stores in json files


Bootstrap process :
As the streaming job cannot go back in time to fetch historic tweets, it is necessary to have some bootstrap mechanism which will get older
tweets from Twitter for the job to process. This is particularly needed when the job runs for the very first time and when there is some downtime.


TO DO :
Take snapshot of the bootstrap date so that these tweets are not fetched again while bootstrapping

Building projects :
1. tweetRetriever :

2. tweetAnalysis:
    export SBT_OPTS="-Xmx2G -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=2G -Xss2M  -Duser.timezone=GMT"
    cd TweetSentimentAnalysis
    sbt tweetAnalysis/compile test
    sbt tweetAnalysis/assembly
    export SPARK_HOME=/your/spark/home
    ./tweetAnalysis/src/main/scripts/run-job.sh "2018-05-14" "#holidaycheck"
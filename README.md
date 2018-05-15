# TweetSentimentAnalysis
A Spark job to analyse tweets in a batch mode

## There are 2 jobs :
### 1. tweetRetriever : 
This is a flink job that listens to Twitter stream continuously and writes tweets to files grouped by date (execution date and not tweets date).

But as te tweets are streamed, this date also co-incides with the tweets date.

The input to this job are:

    1. `twitter.properties` file that has twitter credentials
    
    2. the `date` from which we need to fetch historic tweets (Bootstrap process, more on it below). The format is "YYYY-MM-DD".
    Absence of date means there is not need to bootstrap

### 2. tweetAnalysis : 
This is a spark job that runs in a batch mode once a day. The job aggregates gets tweets for the last day (stored by the flink job) and processes it and stores in json files

Note that the tweetAnalysis job runs for the previous day as tweetRetriever is already fetching tweets for the current day


### Bootstrap process :
As the streaming job cannot go back in time to fetch historic tweets, it is necessary to have some bootstrap mechanism which will get older tweets from Twitter for the job to process. This is particularly needed when the job runs for the very first time and when there is some downtime.


### Building projects :
#### 1. tweetRetriever :
    $ sbt clean compile test
    $ export SBT_OPTS="-Xmx2G -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=2G -Xss2M  -Duser.timezone=GMT"
    $ sbt tweetRetriever/assembly

    Running job :
    1. Start flink cluster :
        $ cd /Users/tejashree.aher/Documents/Sofware/flink-1.3.3/
        $ ./bin/start-local.sh
        (JobManager’s web frontend at http://localhost:8081)
        (Logs : $ tail log/flink-*-jobmanager-*.log)

    2. Submit the job to the cluster :
        $ cd /Users/tejashree.aher/Documents/Sofware/flink-1.3.3/
        $ ./bin/flink run /Users/tejashree.aher/TweetSentimentAnalysis/tweetRetriever/target/scala-2.11/tweet-retriever-assembly-0.0.1.jar -d "2018-01-01" -w "#holidaycheck" -f "twitter.properties"
        (Logs : tail -f log/flink-*-taskmanager-*.out)
    3. Get list of running jobs :
        $ ./bin/flink list
    4.  Stop a running flink job :
        $ ./bin/flink stop ${jobID}
    5. To stop the cluster : $ ./bin/stop-local.sh

#### 2. tweetAnalysis:
    $ export SBT_OPTS="-Xmx2G -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=2G -Xss2M  -Duser.timezone=GMT"
    $ cd TweetSentimentAnalysis
    $ sbt tweetAnalysis/compile test
    $ sbt tweetAnalysis/assembly
    $ export SPARK_HOME=/your/spark/home
    $ ./tweetAnalysis/src/main/scripts/run-job.sh "2018-05-15" "#holidaycheck"

Flink job creates partitions strating with "_" which are invisible for spark. So current workaround is to rename those files using following commands :

    $ cd /tmp/streaming/tweets/${keyword}/${date}/
    $ for f in _part*; do mv "$f" "${f#_}"; done

### TROUBLESHOOTING :

### TO DO :
1.Take snapshot of the bootstrap date so that these tweets are not fetched again while bootstrapping. But as the tweets occur only once, the new result will be the absolute result.

2. Proper metrics for monitoring

3. More test cases




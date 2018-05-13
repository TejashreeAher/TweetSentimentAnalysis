# TweetSentimentAnalysis
A Spark job to analyse tweets in a batch mode

Set correct spark home using :
export SPARK_HOME=/your/spark/home
This is needed in the `run-job.sh` script

create table text_table(id int, dt string,name string) stored as textfile location '/user/yashu/text_table';
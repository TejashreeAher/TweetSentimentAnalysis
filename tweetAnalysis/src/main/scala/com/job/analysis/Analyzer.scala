package com.job.analysis

import com.job.models.Sentiment

trait Analyzer {
    def getSentiment(): Sentiment

    def getSentimentFromValue(value: Int): Sentiment
}

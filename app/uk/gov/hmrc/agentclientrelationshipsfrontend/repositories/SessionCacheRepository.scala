
package uk.gov.hmrc.agentclientrelationshipsfrontend.repositories

import uk.gov.hmrc.mongo.{MongoComponent, TimestampSupport}
import uk.gov.hmrc.mongo.cache.CacheIdType.SimpleCacheId
import uk.gov.hmrc.mongo.cache.MongoCacheRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

@Singleton
class SessionCacheRepository @Inject()(mongo: MongoComponent, timestampSupport: TimestampSupport)(implicit ec: ExecutionContext)
  extends MongoCacheRepository(
    mongoComponent = mongo,
    collectionName = "sessions",
    ttl = 15.minutes,
    timestampSupport = timestampSupport,
    cacheIdType = SimpleCacheId
  )

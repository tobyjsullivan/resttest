package net.tobysullivan.resttest.adapters.http

import scalaj.http._
import spray.json._
import com.typesafe.config.ConfigFactory

import scala.util.Try

object JsonFetcherImpl extends JsonFetcher {
  private val conf = ConfigFactory.load()

  private val connTimeout: Int = conf.getInt("http.timeout.connect")
  private val readTimeout: Int = conf.getInt("http.timeout.read")

  override def fetchSync(url: String): Try[Option[JsValue]] = Try {
    Http(url)
      .option(HttpOptions.method("GET"))
      .option(HttpOptions.followRedirects(true))
      .option(HttpOptions.connTimeout(connTimeout))
      .option(HttpOptions.readTimeout(readTimeout))
      .asString match {
      case resp @ HttpResponse(body, _, _) if resp.is2xx => Some(body.parseJson)
      case HttpResponse(_, 404, _) => None
      case HttpResponse(_, code, _) => throw new Exception(s"Unexpected response code: $code")
    }
  }

}

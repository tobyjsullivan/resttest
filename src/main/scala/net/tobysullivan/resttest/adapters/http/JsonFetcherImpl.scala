package net.tobysullivan.resttest.adapters.http

import scalaj.http._
import spray.json._

import scala.util.Try

object JsonFetcherImpl extends JsonFetcher {
  override def fetchSync(url: String): Try[Option[JsValue]] = Try {
    Http(url)
      .option(HttpOptions.method("GET"))
      .option(HttpOptions.followRedirects(true))
      .asString match {
      case resp @ HttpResponse(body, _, _) if resp.is2xx => Some(body.parseJson)
      case HttpResponse(_, 404, _) => None
      case HttpResponse(_, code, _) => throw new Exception(s"Unexpected response code: $code")
    }
  }

}

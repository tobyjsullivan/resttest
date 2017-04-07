package net.tobysullivan.resttest.adapters.http

import spray.json.JsValue

import scala.util.Try

/**
  * JsonFetcher represents an adapter which requests JSON resourcs from an API
  */
trait JsonFetcher {
  /**
    * fetchSync will request a JSON payload from the given url using a GET request.
    * @param url The URL to query
    * @return Success(Some(_)) if json data is found with a 2XX response. Success(None) for any 404 response. Failure(_) for any other response.
    */
  def fetchSync(url: String): Try[Option[JsValue]]
}

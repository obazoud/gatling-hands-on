package computerdatabase

import com.excilys.ebi.gatling.core.Predef._
import com.excilys.ebi.gatling.http.Predef._
import akka.util.duration._
import bootstrap._

class Step09 extends Simulation {

	def apply = {

		val httpConf = httpConfig
			.baseURL("http://localhost:9000")
			.acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
			.doNotTrackHeader("1")
			.acceptLanguageHeader("en-US,en;q=0.5")
			.acceptEncodingHeader("gzip, deflate")
			.userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")
			.hostHeader("localhost:9000")

		// notice headers
		val formHeaders = Map("Content-Type" -> "application/x-www-form-urlencoded")

		// clean up
		val scn = scenario("Create")
			.exec(http("Index")
				.get("/"))
			.pause(4)
			.exec(http("Display form")
				.get("/computers/new"))
			.pause(4)
			.exec(http("Post form")
				.post("/computers")
				.headers(formHeaders)
				.param("name", "Beautiful Computer")
				.param("introduced", "2012-05-30")
				.param("discontinued", "")
				.param("company", "1"))

		List(scn.configure.users(1).protocolConfig(httpConf))
	}
}
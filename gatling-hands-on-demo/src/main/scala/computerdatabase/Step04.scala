package computerdatabase

import com.excilys.ebi.gatling.core.Predef._
import com.excilys.ebi.gatling.http.Predef._
import akka.util.duration._
import bootstrap._

class Step04 extends Simulation {

	def apply = {

		val httpConf = httpConfig
			.baseURL("http://localhost:9000")
			.acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
			.doNotTrackHeader("1")
			.acceptLanguageHeader("en-US,en;q=0.5")
			.acceptEncodingHeader("gzip, deflate")
			.userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")
			.hostHeader("localhost:9000")

		// init from Recorder: browse from index (page 0) to page 4
		val scn = scenario("Scenario Name")
			.exec(http("request_1")
				.get("/"))
			.pause(4)
			.exec(http("request_2")
				.get("/computers")
				.queryParam("p", "1"))
			.pause(4)
			.exec(http("request_3")
				.get("/computers")
				.queryParam("p", "2"))
			.pause(4)
			.exec(http("request_4")
				.get("/computers")
				.queryParam("p", "3"))
			.pause(4)
			.exec(http("request_5")
				.get("/computers")
				.queryParam("p", "4"))

		List(scn.configure.users(1).protocolConfig(httpConf))
	}
}
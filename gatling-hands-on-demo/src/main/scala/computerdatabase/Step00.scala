package computerdatabase

import com.excilys.ebi.gatling.core.Predef._
import com.excilys.ebi.gatling.http.Predef._
import akka.util.duration._
import bootstrap._

class Step00 extends Simulation {

	def apply = {

		val httpConf = httpConfig
			.baseURL("http://localhost:9000")
			.acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
			.doNotTrackHeader("1")
			.acceptLanguageHeader("en-US,en;q=0.5")
			.acceptEncodingHeader("gzip, deflate")
			.userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")
			.hostHeader("localhost:9000")

		// init from Recorder: search Macbook Pro
		val scn = scenario("Scenario Name")
			.exec(http("request_1")
				.get("/"))
			.pause(4)
			.exec(http("request_2")
				.get("/computers")
				.queryParam("""f""", """Macbook Pro"""))
			.pause(4)
			.exec(http("request_3")
				.get("/computers/6"))

		List(scn.configure.users(1).protocolConfig(httpConf))
	}

}
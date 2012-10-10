package play

import akka.util.duration._
import com.excilys.ebi.gatling.core.Predef._
import com.excilys.ebi.gatling.http.Predef._
import bootstrap._

class SimulationPlay extends Simulation {

	def apply = {
		val httpConf = httpConfig
			.baseURL("http://10.0.2.2:9000")
			.acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
			.acceptEncodingHeader("gzip, deflate")
			.acceptLanguageHeader("fr,fr-fr;q=0.8,en-us;q=0.5,en;q=0.3")
			.hostHeader("localhost:9000")
			.userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.7; rv:11.0) Gecko/20100101 Firefox/11.0")

		val scn = scenario("Play2 Computer Database Sample")
			.repeat(10) {
				exec(http("Home")
					.get("/")
				)
				.pause(2 seconds)
				.exec(http("Search Apple")
					.get("/computers")
					.queryParam("f", "Apple")
					.check(
						//md5.is("a16e96ddc8ac09be4a5d0a7ff245b248"),
						regex("13 computers found")
					)
				)
				.exec(http("Bootstrap CSS")
					.get("/assets/stylesheets/bootstrap.min.css")
				)
				.pause(2 seconds)
				.exec(http("Go to next page")
					.get("/computers")
					.queryParam("p", "1")
					.queryParam("f", "Apple")
				)
				.pause(6 seconds)
				.exec(http("Select Apple Network Server")
					.get("/computers/403")
				)
				.pause(6 seconds)
				.exec(http("Cancel")
					.get("/computers")
				)
			}

		List(scn.configure.users(10).protocolConfig(httpConf))
	}
}

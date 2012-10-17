package computerdatabase

import com.excilys.ebi.gatling.core.Predef._
import com.excilys.ebi.gatling.http.Predef._
import akka.util.duration._
import bootstrap._

class Step02 extends Simulation {

	def apply = {

		val httpConf = httpConfig
			.baseURL("http://localhost:9000")
			.acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
			.doNotTrackHeader("1")
			.acceptLanguageHeader("en-US,en;q=0.5")
			.acceptEncodingHeader("gzip, deflate")
			.userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")
			.hostHeader("localhost:9000")

		val scn = scenario("Search computer")
			.exec(http("Index")
				.get("/"))
			.pause(4)
			.exec(http("Search")
				.get("/computers")
				.queryParam("f", "MacBook Pro")
				// add checks
				.check(status.is(200),
					regex("""<a href="([^"]+)">MacBook Pro</a>""").find.saveAs("computerURL")))
			.pause(4)
			// add EL
			.exec(http("Select")
				.get("${computerURL}")
				.check(css("#name", "value").is("MacBook Pro"))) // add check

		List(scn.configure.users(1).protocolConfig(httpConf))
	}
}
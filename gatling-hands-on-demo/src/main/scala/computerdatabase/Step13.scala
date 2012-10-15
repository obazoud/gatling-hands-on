package computerdatabase

import com.excilys.ebi.gatling.core.Predef._
import com.excilys.ebi.gatling.http.Predef._
import akka.util.duration._
import bootstrap._

class Step13 extends Simulation {

	// plug steps 3, 7 and 12 altogether
	def apply = {

		val httpConf = httpConfig
			.baseURL("http://localhost:9000")
			.acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
			.doNotTrackHeader("1")
			.acceptLanguageHeader("en-US,en;q=0.5")
			.acceptEncodingHeader("gzip, deflate")
			.userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")
			.hostHeader("localhost:9000")

		// define chains for each process
		val search = {
			val searchFeeder = csv("search.csv").circular

			exec(http("Index")
				.get("/"))
				.pause(4)
				// add feeder
				.feed(searchFeeder)
				.exec(http("Search")
					.get("/computers")
					.queryParam("f", "${searchCriterion}")
					.check(status.is(200),
						regex("""<a href="([^"]+)">${searchComputerName}</a>""").find.saveAs("computerURL")))
				.pause(4)
				.exec(http("Select")
					.get("${computerURL}")
					.check(css("#name", "value").is("${searchComputerName}")))
		}

		val browse = {
			def gotoPage(index: String) = exec(http("Next page")
				.get("/computers")
				.queryParam("p", index))

			randomSwitch(
				80 -> repeat(5, "index") {
					gotoPage("${index}")
						.pause(4)
				},
				20 -> gotoPage("5"))
		}

		val create = {
			val formHeaders = Map("Content-Type" -> "application/x-www-form-urlencoded")

			val createFeeder = csv("computers.csv").circular

			val random = new java.util.Random

			exec(http("Index")
				.get("/"))
				.pause(4)
				.exec(http("Display form")
					.get("/computers/new"))
				.pause(4)
				.feed(createFeeder)
				.tryMax(5) {
					exec(http("Post form")
						.post("/computers")
						.headers(formHeaders)
						.param("name", "${name}")
						.param("introduced", "${introduced}")
						.param("discontinued", "${discontinued}")
						.param("company", "${company}")
						.check(status.is((session: Session) => 200 + random.nextInt(2))))
				}
		}

		// define 2 populations
		val users = scenario("Users")
			.repeat(3) { // loop 3 times
				exec(browse, search)
			}
		val admins = scenario("Admins")
			.during(60) { // loop for 60 seconds
				exec(browse, create)
			}

		// mix the two populations
		List(users.configure.users(1000).ramp(10).protocolConfig(httpConf),
			admins.configure.users(100).ramp(10).protocolConfig(httpConf))
	}
}
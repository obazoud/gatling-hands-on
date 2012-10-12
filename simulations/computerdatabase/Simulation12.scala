package computerdatabase

import com.excilys.ebi.gatling.core.Predef._
import com.excilys.ebi.gatling.http.Predef._
import akka.util.duration._
import bootstrap._
import scala.util.Random

class Simulation12 extends Simulation {

	def apply = {

		val baseURL = "http://localhost:9000"

		val httpConf = httpConfig
			.baseURL(baseURL)
			.acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
			.acceptCharsetHeader("ISO-8859-1,utf-8;q=0.7,*;q=0.3")
			.acceptLanguageHeader("fr-FR,fr;q=0.8,en-US;q=0.6,en;q=0.4,es;q=0.2,it;q=0.2")
			.acceptEncodingHeader("gzip,deflate,sdch")
			.doNotTrackHeader("1")
			.userAgentHeader("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.4 (KHTML, like Gecko) Chrome/22.0.1229.92 Safari/537.4")
			.hostHeader("localhost:9000")

		val formHeaders = Map(
			"Content-Type" -> "application/x-www-form-urlencoded")

		val computerFeeder = csv("computers.csv").circular

		// We declare a request in a val to use it at different places
		val nextPageChain = exec(http("Next page")
			.get("${nextURL}")
			.check(
				css("#pagination .next a", "href").saveAs("nextURL"),
				currentLocation.saveAs("currentURL")
			)
		)

		val scn = scenario("Gatling simulation")
			.exec(http("Index page")
				.get("/")
				.check(
					css(".computers tbody a").count.is(10),
					responseTimeInMillis.lessThan(Random.nextInt(50))
				)
			)
			.pause(11 milliseconds)
			.repeat(10) {
				exec(http("Add computer page")
					.get("/computers/new")
				)
				.pause(2)
				.feed(computerFeeder)
				.exec(http("Post new computer")
					.post("/computers")
					.headers(formHeaders)
					.param("name", "${name}")
					.param("introduced", "${introduced}")
					.param("discontinued", "${discontinued}")
					.param("company", "${company}")
					.check(
						status.in(Seq(200, 303)),
						currentLocation.is(baseURL + "/computers")
					)
				)
			}
			.pause(2 seconds)
			.exec(http("Find my computer")
				.get("/computers")
				.queryParam("f", "My awesome computer")
				.check(
					status.is(200),
					regex("""<a href="([^"]+)">My awesome computer</a>""").find.saveAs("computerURL")
				)
			)
			.pause(1 second, 3 seconds)
			.exec(http("My computer page")
				.get("${computerURL}")
				.check(
					css("#name", "value").is("My awesome computer")
				)
			)
			.pauseExp(200 milliseconds)
			.exec(http("Index page")
				.get("/")
				.check(
					css("#pagination .next a", "href").saveAs("nextURL"),
					currentLocation.saveAs("currentURL")
				)
			)
			.pause(11 milliseconds)
			.randomSwitch( // A user will 80% of the time iterate to the 5th page and 20% of the time to the 50th page
				80 -> asLongAs((s: Session) => {
					!s.getTypedAttribute[String]("currentURL").endsWith("p=5")
				}) (nextPageChain), // We use the chain that we have declared before
				20 -> asLongAs((s: Session) => {
					!s.getTypedAttribute[String]("currentURL").endsWith("p=50")
				}) (nextPageChain) // We use the chain that we have declared before
			)
			.roundRobinSwitch( // A user will go to the 10th page or the 20th page
				pause(712 milliseconds)
				.exec(http("Go to page 10")
					.get("/computers")
					.queryParam("p", "10")
				),
				pause(2)
				.exec(http("Go to page 20")
					.get("/computers")
					.queryParam("p", "20")
				)
			)
			.pause(3)
			.exec(http("Random page")
				.get("/computers/157")
			)
			.pause(1)
			.exec(http("Index page")
				.get("/")
			)
			.pause(3)

		List(scn.configure.users(100).ramp(20).protocolConfig(httpConf))
	}
}

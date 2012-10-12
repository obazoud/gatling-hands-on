package computerdatabase

import com.excilys.ebi.gatling.core.Predef._
import com.excilys.ebi.gatling.http.Predef._
import akka.util.duration._
import bootstrap._
import scala.util.Random

class Simulation13 extends Simulation {

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
			.randomSwitch(
				80 -> asLongAs((s: Session) => {
					!s.getTypedAttribute[String]("currentURL").endsWith("p=5")
				}) (nextPageChain),
				20 -> asLongAs((s: Session) => {
					!s.getTypedAttribute[String]("currentURL").endsWith("p=50")
				}) (nextPageChain)
			)
			.roundRobinSwitch(
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
			.tryMax(50) { // If the requests failed, we will retry with 50 tries at max
				exec((s: Session) => { // We create a custom exec that generate a random value and store it into the session
					val id = Random.nextInt(10000)
					println("Trying id = " + id) // You can print what you want like the session
					s.setAttribute("computerId", id) // Store the value in session and return a new session (a session is immutable !)
				})
				.pause(3)
				.exec(http("Random page")
					.get("/computers/${computerId}") // Like always, we use EL to access session attributes
				)
			}
			.exitBlockOnFail { // The block will stop execution as soon as there is a failure
				repeat(10) {
					pause(1)
					.exec(http("Index page")
						.get("/")
						.check(
						  status.is(200 + Random.nextInt(5))
						)
					)
				}
			}
			.pause(3)

		List(scn.configure.users(100).ramp(20).protocolConfig(httpConf))
	}
}

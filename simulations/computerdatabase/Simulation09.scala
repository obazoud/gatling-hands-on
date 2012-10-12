package computerdatabase

import com.excilys.ebi.gatling.core.Predef._
import com.excilys.ebi.gatling.http.Predef._
import akka.util.duration._
import scala.util.Random
import bootstrap._

class Simulation09 extends Simulation {

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

		val scn = scenario("Gatling simulation")
			.exec(http("Index page")
				.get("/")
				.check( // Add checks
					css(".computers tbody a").count.is(10), // Check that there are 10 links in the table
					responseTimeInMillis.lessThan(Random.nextInt(50)) // Check that the response time is less than a random value between 0 and 50ms
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
					.check( // Add checks
						status.in(Seq(200, 303)), // Check that HTTP status code is 200 or 303
						currentLocation.is(baseURL + "/computers") // Check that the current URL is /computers
					)
				)
			}
			.pause(2 seconds)
			.exec(http("Find my computer")
				.get("/computers")
				.queryParam("f", "My awesome computer")
				.check( // Add checks
					status.is(200), // Check that HTTP status code is 200
					regex("""<a href="([^"]+)">My awesome computer</a>""").exists // Check that there is a link to My awesome computer in the page
				)
			)
			.pause(1 second, 3 seconds)
			.exec(http("My computer page")
				.get("/computers/1000")
				.check( // Add checks
					css("#name", "value").is("My awesome computer") // Check that the value attribute of #name is My awesome computer
				)
			)
			.pauseExp(200 milliseconds)
			.exec(http("Index page")
				.get("/")
			)
			.pause(11 milliseconds)
			.exec(http("Next page 1")
				.get("/computers")
				.queryParam("p", "1")
			)
			.pause(1)
			.exec(http("Next page 2")
				.get("/computers")
				.queryParam("p", "2")
			)
			.pause(568 milliseconds)
			.exec(http("Next page 3")
				.get("/computers")
				.queryParam("p", "3")
			)
			.pause(480 milliseconds)
			.exec(http("Next page 4")
				.get("/computers")
				.queryParam("p", "4")
			)
			.pause(503 milliseconds)
			.exec(http("Next page 5")
				.get("/computers")
				.queryParam("p", "5")
			)
			.pause(712 milliseconds)
			.exec(http("Go to page 10")
				.get("/computers")
				.queryParam("p", "10")
			)
			.pause(2)
			.exec(http("Go to page 20")
				.get("/computers")
				.queryParam("p", "20")
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

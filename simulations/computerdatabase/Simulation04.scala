package computerdatabase

import com.excilys.ebi.gatling.core.Predef._
import com.excilys.ebi.gatling.http.Predef._
import akka.util.duration._
import bootstrap._

class Simulation04 extends Simulation {

	def apply = {

		val httpConf = httpConfig
			.baseURL("http://localhost:9000")
			.acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
			.acceptCharsetHeader("ISO-8859-1,utf-8;q=0.7,*;q=0.3")
			.acceptLanguageHeader("fr-FR,fr;q=0.8,en-US;q=0.6,en;q=0.4,es;q=0.2,it;q=0.2")
			.acceptEncodingHeader("gzip,deflate,sdch")
			.doNotTrackHeader("1")
			.userAgentHeader("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.4 (KHTML, like Gecko) Chrome/22.0.1229.92 Safari/537.4")
			.hostHeader("localhost:9000")

		val formHeaders = Map(
			"Content-Type" -> "application/x-www-form-urlencoded")

		val scn = scenario("Gatling simulation")
			.exec(http("Index page")
				.get("/")
			)
			.pause(11 milliseconds)
			.during(10 seconds) { // This block will be repeated for 10 seconds by each user
				exec(http("Add computer page")
					.get("/computers/new")
				)
				.pause(2)
				.exec(http("Post new computer")
					.post("/computers")
					.headers(formHeaders)
					.param("name", "My awesome computer")
					.param("introduced", "2012-10-08")
					.param("discontinued", "2013-01-03")
					.param("company", "37")
				)
			}
			.pause(2 seconds)
			.exec(http("Find my computer")
				.get("/computers")
				.queryParam("f", "My awesome computer")
			)
			.pause(1 second, 3 seconds)
			.exec(http("My computer page")
				.get("/computers/1000")
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

		List(scn.configure.users(1).protocolConfig(httpConf))
	}
}

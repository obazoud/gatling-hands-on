package computerdatabase

import scala.util.Random
import com.excilys.ebi.gatling.core.Predef._
import com.excilys.ebi.gatling.http.Predef._
import bootstrap._

class Simulation12 extends Simulation {

	def apply = {

		val baseURL = "http://localhost:9000"

		val computerFeeder = csv("computers.csv").random

		val httpConf = httpConfig
			.baseURL(baseURL)
			.acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
			.acceptCharsetHeader("ISO-8859-1,utf-8;q=0.7,*;q=0.3")
			.acceptLanguageHeader("fr-FR,fr;q=0.8,en-US;q=0.6,en;q=0.4,es;q=0.2,it;q=0.2")
			.acceptEncodingHeader("gzip,deflate,sdch")
			.doNotTrackHeader("1")
			.userAgentHeader("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.4 (KHTML, like Gecko) Chrome/22.0.1229.92 Safari/537.4")
			.hostHeader("localhost:9000")

		val formHeaders = Map("Content-Type" -> """application/x-www-form-urlencoded""")

		val nextPageChain = exec(http("Next page")
			.get("${nextURL}")
			.check(
				css("#pagination .next a", "href").saveAs("nextURL"),
				currentLocation.saveAs("currentURL")))

		val scn = scenario("Gatling simulation")
			.exec(http("Index page")
				.get("/computers")
				.check(
					css(".computers tbody a").count.is(10),
					responseTimeInMillis.lessThan(Random.nextInt(50))))

			.exitBlockOnFail {
				repeat(10) {
					exec(http("Add computer page")
						.get("/computers/new"))
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
								currentLocation.is(baseURL + "/computers")))
				}
			}

			.exec(http("Find my computer")
				.get("/computers")
				.queryParam("f", "My awesome computer")
				.check(
					status.is(200),
					regex("""<a href="([^"]+)">My awesome computer</a>""").find.saveAs("computerURL")))

			.exec(http("My computer page")
				.get("${computerURL}")
				.check(
					css("#name", "value").is("My awesome computer")))

			.exec(http("Index page")
				.get("/computers")
				.check(
					css("#pagination .next a", "href").saveAs("nextURL"),
					currentLocation.saveAs("currentURL")))

			.randomSwitch(
				80 -> asLongAs((s: Session) => {
					!s.getTypedAttribute[String]("currentURL").endsWith("p=5")
				})(nextPageChain),

				20 -> asLongAs((s: Session) => {
					!s.getTypedAttribute[String]("currentURL").endsWith("p=50")
				})(nextPageChain))

			.roundRobinSwitch(
				exec(http("Go to page 10")
					.get("/computers")
					.queryParam("p", "10")),

				exec(http("Go to page 20")
					.get("/computers")
					.queryParam("p", "20")))

			.tryMax(50) {
				exec((s: Session) => {
					val id = Random.nextInt(10000)
					println("Trying id = " + id)
					s.setAttribute("computerId", id)
				})

					.exec(http("Random page")
						.get("/computers/${computerId}"))
			}

		List(scn.configure.users(1).protocolConfig(httpConf))
	}
}
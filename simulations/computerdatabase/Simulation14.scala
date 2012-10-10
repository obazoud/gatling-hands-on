package computerdatabase

import scala.util.Random
import akka.util.duration._
import com.excilys.ebi.gatling.core.Predef._
import com.excilys.ebi.gatling.http.Predef._
import bootstrap._

class Simulation14 extends Simulation {

	def apply = {

		val baseURL = "http://localhost:9000"

		val computerFeeder = csv("computers.csv").random

		val httpConf = httpConfig
			.baseURL(baseURL)

		val formHeader = Map(
			"Content-Type" -> "application/x-www-form-urlencoded")

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

			.pause(2 seconds)

			.exitBlockOnFail {
				repeat(10) {
					exec(http("Add computer page")
						.get("/computers/new"))
						.feed(computerFeeder)
						.exec(http("Post new computer")
							.post("/computers")
							.headers(formHeader)
							.param("name", "${name}")
							.param("introduced", "${introduced}")
							.param("discontinued", "${discontinued}")
							.param("company", "${company}")
							.check(
								status.in(Seq(200, 303)),
								currentLocation.is(baseURL + "/computers")))
				}
			}

			.pause(100 milliseconds, 4 seconds)

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

			.pauseExp(1 second)

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

		List(scn.configure.users(100).ramp(20).protocolConfig(httpConf))
	}
}
package computerdatabase

import com.excilys.ebi.gatling.core.Predef._
import com.excilys.ebi.gatling.http.Predef._
import bootstrap._

class Simulation05 extends Simulation {

	val computerFeeder = csv("computers.csv").random

	def apply = {

		val httpConf = httpConfig
			.baseURL("http://localhost:9000")

		val formHeader = Map(
			"Content-Type" -> "application/x-www-form-urlencoded"
		)


		val scn = scenario("Gatling simulation")
			.exec(http("Index page")
				.get("/computers")
			)

			.repeat(10) {
				exec(http("Add computer page")
					.get("/computers/new")
				)
				.feed(computerFeeder)
				.exec(http("Post new computer")
					.post("/computers")
					.headers(formHeader)
					.param("name", "${name} ${userId}")
					.param("introduced", "${introduced}")
					.param("discontinued", "${discontinued}")
					.param("company", "${company}")
				)
			}

		List(scn.configure.users(1).protocolConfig(httpConf))
	}
}
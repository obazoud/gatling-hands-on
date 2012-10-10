package computerdatabase

import com.excilys.ebi.gatling.core.Predef._
import com.excilys.ebi.gatling.http.Predef._

class Simulation01 extends Simulation {

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

			.exec(http("Add computer page")
				.get("/computers/new")
			)

			.exec(http("Post new computer")
				.post("/computers")
				.headers(formHeader)
				.param("name", "My computer")
				.param("introduced", "2012-10-08")
				.param("discontinued", "2013-01-03")
				.param("company", "37")
			)

		List(scn.configure.users(1).protocolConfig(httpConf))
	}
}
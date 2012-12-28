package computerdatabase
import com.excilys.ebi.gatling.core.Predef._
import com.excilys.ebi.gatling.http.Predef._
import com.excilys.ebi.gatling.jdbc.Predef._
import com.excilys.ebi.gatling.http.Headers.Names._
import akka.util.duration._
import bootstrap._
import assertions._

class Step03 extends Simulation {

	object Search {

		val search = exec(http("Home") // let's give proper names, they are displayed in the reports, and used as keys
			.get("/"))
			.pause(1) // let's set the pauses to 1 sec for demo purpose
			.exec(http("Search")
				.get("/computers")
				.queryParam("""f""", """macbook"""))
			.pause(1)
			.exec(http("Select")
				.get("/computers/6")
				.check(status.is(200))) // Here's a check. By default, Gatling checks is the status is 200X
			.pause(1)
	}

	object Browse {

		val browse = exec(http("request_4")
			.get("/"))
			.pause(1)
			.exec(http("request_5")
				.get("/computers")
				.queryParam("""p""", """1"""))
			.pause(670 milliseconds)
			.exec(http("request_6")
				.get("/computers")
				.queryParam("""p""", """2"""))
			.pause(629 milliseconds)
			.exec(http("request_7")
				.get("/computers")
				.queryParam("""p""", """3"""))
			.pause(734 milliseconds)
			.exec(http("request_8")
				.get("/computers")
				.queryParam("""p""", """4"""))
			.pause(5)
	}

	object Edit {

		val headers_10 = Map("Content-Type" -> """application/x-www-form-urlencoded""")

		val edit = exec(http("request_9")
			.get("/computers/new"))
			.pause(1)
			.exec(http("request_10")
				.post("/computers")
				.headers(headers_10)
				.param("""name""", """Beautiful Computer""")
				.param("""introduced""", """2012-05-30""")
				.param("""discontinued""", """""")
				.param("""company""", """37"""))
	}

	val httpConf = httpConfig
		.baseURL("http://localhost:9000")
		.acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
		.doNotTrackHeader("1")
		.acceptLanguageHeader("en-US,en;q=0.5")
		.acceptEncodingHeader("gzip, deflate")
		.userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")

	val users = scenario("Users").exec(Search.search, Browse.browse)
	val admins = scenario("Admins").exec(Search.search, Browse.browse, Edit.edit)

	setUp(users.users(1000).ramp(20).protocolConfig(httpConf),
		admins.users(10).ramp(20).protocolConfig(httpConf))
}
package computerdatabase
import com.excilys.ebi.gatling.core.Predef._
import com.excilys.ebi.gatling.http.Predef._
import com.excilys.ebi.gatling.jdbc.Predef._
import com.excilys.ebi.gatling.http.Headers.Names._
import akka.util.duration._
import bootstrap._
import assertions._

class Step05 extends Simulation {

	object Search {

		val feeder = csv("search.csv").random

		val search = exec(http("Home")
			.get("/"))
			.pause(1)
			.feed(feeder)
			.exec(http("Search")
				.get("/computers")
				.queryParam("""f""", "${searchCriterion}")
				.check(regex("""<a href="([^"]+)">${searchComputerName}</a>""").saveAs("computerURL")))
			.pause(1)
			.exec(http("Select")
				.get("${computerURL}")
				.check(status.is(200)))
			.pause(1)
	}

	object Browse {

		// Not very DRY, let's refactor

		def gotoPage(page: String) = exec(http("Page " + page)
			.get("/computers")
			.queryParam("""p""", page))
			.pause(1)

		val browse = exec(gotoPage("0"), gotoPage("1"), gotoPage("2"), gotoPage("3"), gotoPage("4"))
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
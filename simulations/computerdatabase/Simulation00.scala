package computerdatabase
import com.excilys.ebi.gatling.core.Predef._
import com.excilys.ebi.gatling.http.Predef._
import com.excilys.ebi.gatling.jdbc.Predef._
import com.excilys.ebi.gatling.http.Headers.Names._
import akka.util.duration._
import bootstrap._

class Simulation00 extends Simulation {

	def apply = {

		val httpConf = httpConfig
			.baseURL("http://localhost:9000")
			.acceptHeader("text/css,*/*;q=0.1")
			.acceptCharsetHeader("ISO-8859-1,utf-8;q=0.7,*;q=0.3")
			.acceptLanguageHeader("fr-FR,fr;q=0.8,en-US;q=0.6,en;q=0.4,es;q=0.2,it;q=0.2")
			.acceptEncodingHeader("gzip,deflate,sdch")
			.userAgentHeader("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.4 (KHTML, like Gecko) Chrome/22.0.1229.79 Safari/537.4")
			.hostHeader("localhost:9000")


		val headers_1 = Map(
			"Accept" -> """text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8""",
			"Cache-Control" -> """max-age=0"""
		)

		val headers_2 = Map(
			"Cache-Control" -> """max-age=0""",
			"If-Modified-Since" -> """Fri, 03 Aug 2012 17:29:45 GMT""",
			"If-None-Match" -> """"4e196cad76bd2c3fe91623ac4255a7ca428fa848""""
		)

		val headers_3 = Map(
			"Cache-Control" -> """max-age=0""",
			"If-Modified-Since" -> """Fri, 03 Aug 2012 17:29:45 GMT""",
			"If-None-Match" -> """"09d8c9e996513a600c7cc5a3f87bdb5344af22db""""
		)

		val headers_4 = Map(
			"Accept" -> """*/*"""
		)

		val headers_5 = Map(
			"Accept" -> """text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"""
		)

		val headers_9 = Map(
			"Accept" -> """text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8""",
			"Cache-Control" -> """max-age=0""",
			"Content-Type" -> """application/x-www-form-urlencoded""",
			"Origin" -> """http://localhost:9000"""
		)


		val scn = scenario("Recorder scenario")
			.exec(http("request_1")
					.get("/computers")
					.headers(headers_1)
			)
			.exec(http("request_2")
					.get("/assets/stylesheets/main.css")
					.headers(headers_2)
					.check(status.is(304))
			)
			.			pause(24 milliseconds)
			.exec(http("request_3")
					.get("/assets/stylesheets/bootstrap.min.css")
					.headers(headers_3)
					.check(status.is(304))
			)
			.exec(http("request_4")
					.get("/favicon.ico")
					.headers(headers_4)
					.check(status.is(404))
			)
			.			pause(31 milliseconds)
			.exec(http("request_5")
					.get("/computers/new")
					.headers(headers_5)
			)
			.			pause(2)
			.exec(http("request_6")
					.get("/assets/stylesheets/bootstrap.min.css")
					.headers(headers_3)
					.check(status.is(304))
			)
			.			pause(25 milliseconds)
			.exec(http("request_7")
					.get("/assets/stylesheets/main.css")
					.headers(headers_2)
					.check(status.is(304))
			)
			.exec(http("request_8")
					.get("/favicon.ico")
					.headers(headers_4)
					.check(status.is(404))
			)
			.			pause(34 milliseconds)
			.exec(http("request_9")
					.post("/computers")
					.headers(headers_9)
						.param("""name""", """My computer""")
						.param("""introduced""", """2012-10-08""")
						.param("""discontinued""", """2013-01-03""")
						.param("""company""", """37""")
			)
			.			pause(24 milliseconds)
			.exec(http("request_10")
					.get("/assets/stylesheets/bootstrap.min.css")
					.headers(headers_3)
					.check(status.is(304))
			)
			.			pause(27 milliseconds)
			.exec(http("request_11")
					.get("/assets/stylesheets/main.css")
					.headers(headers_2)
					.check(status.is(304))
			)
			.exec(http("request_12")
					.get("/favicon.ico")
					.headers(headers_4)
					.check(status.is(404))
			)
			.			pause(41 milliseconds)

		List(scn.configure.users(1).protocolConfig(httpConf))
	}
}
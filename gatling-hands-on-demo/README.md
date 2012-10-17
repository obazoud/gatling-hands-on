gatling-hands-on
================

Code for Gatling Hands On


Simulations
===========

The simulations use the computer database scala sample provided with Play 2 distribution.

**0** Slides

**1** Explain bundle structure:

* `bin` contains the launch scripts for Gatling and the Recorder. Talk briefly about options (force simulation, reports only…)
* `conf` contains the confif files for gatling, akka and logback
* `lib`
* `user-files`
  * `simulations` contains your Simulations scala files. Please respect package folder hierarchy
  * `data` feeder files
  * `request-bodies` templates for request bodies
* `results` `simulation.log` and reports with be generated in a sub directory

**2** Start with `play` then `start -DapplyEvolutions.default=true`.

**3** Record the following browsing: index page, search `Macbook Pro`, click `Macbook Pro` result

Set up Gatling Recorder with the following options:

* `/**/*.css`, `/**/*.js` and `/**/*.ico` filters
* `followRedirect`checked
* `autoReferer`checked
* `computerdatabase` package

Explain Simulation file structure: package, class, def, scenario = workflow, DSL immutability, immutable Session

**4** Minor clean up and properly name requests.

**5** Add checks and EL for capturing the link URL.

Checks are used for making assertions on the request and the response, and also for capturing elements.

**6** Use a feeder for dynamic data. Add more users and a ramp and try it out!

If you always use the same data, your system will get some optimizations that won't be representative of real life behavior.

**7** Record browsing from index (page 0) to page 4

**8** Minor clean up and properly name requests. Factorize with a `repeat` loop, note the loop index usage.

**9** Use a `randomSwitch` for mixing with users that would jump directly to page 5

Let's mix different behaviours. The first way is to use a switch.

**10** Factorize request definition. Add more users and a ramp and try it out!

**11** Record the following browsing: go to create new computer page, fill the form and validate.

Until now, we've been doing only read-only tests. Write tests are teh difficult part, where you have to reset your environment between runs.

**12** Minor clean up and properly name requests. Note the new headers.

**13** Use a feeder for populating the form

**14** Add a check that checks randomly

Let's emulate the request failure.

**15** Add a `tryMax` for retrying when the check fails. Add more users and a ramp and try it out!

**16** Merge 3, 7 and 12. Define variables for each process. Compose these processes to make 2 different populations. Try it out!

Here's an example of how one can factorize scenario parts/process definitions. One can also use objects and imports.

Going further: using a repeat loop in point 8 was a quick win. How could we do that if page links are less obvious? Let's use an `asLongAs` loop, with a check.
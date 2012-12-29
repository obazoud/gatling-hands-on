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

**4** Go through Step00 to Step09. See comments in samples.

**5** Try it out!
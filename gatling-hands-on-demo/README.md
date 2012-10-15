gatling-hands-on
================

Code for Gatling Hands On


Simulations
===========

The simulations use the computer database scala sample provided with Play 2 distribution.

Start with `play` then `start -DapplyEvolutions.default=true`.

Set up Gatling Recorder with the following options:

* `/**/*.css`, `/**/*.js` and `/**/*.ico` filters
* `followRedirect`checked
* `autoReferer`checked
* `computerdatabase` package


0. Record the following browsing: index page, search `Macbook Pro`, click `Macbook Pro` result
1. Minor clean up and properly name requests.
2. Add checks and EL for capturing the link URL
3. Use a feeder for dynamic data. Add more users and a ramp and try it out!
4. Record browsing from index (page 0) to page 4
5. Minor clean up and properly name requests. Factorize with a `repeat` loop, note the loop index usage.
6. Use a `randomSwitch` for mixing with users that would jump directly to page 5
7. Factorize request definition. Add more users and a ramp and try it out!
8. Record the following browsing: go to create new computer page, fill the form and validate.
9. Minor clean up and properly name requests. Note the headers.
10. Use a feeder for populating the form
11. Add a check that checks randomly
12. Add a `tryMax` for retrying when the check fails. Add more users and a ramp and try it out!
13. Merge 3, 7 and 12. Define variables for each process. Compose these processes to make 2 different populations. Try it out!
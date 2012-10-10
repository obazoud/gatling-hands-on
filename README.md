gatling-hands-on
================

Code for Gatling Hands On


Simulations
===========

The simulations use the computer database scala sample provided with Play 2 distribution.

1.	Gatling Recorder output for adding a new computer.
2.	Little clean up and properly name request.
3.	Add during loop : a user repeatly add new computers for 10 seconds.
4.	Replace during loop with repeat : a user add 10 identical new computers.
5.	Use a CSV feeder to provide data like the name or the company when adding a new computer.
6.	Move the feed into the loop and use random strategy for the feeder.
7.	Use exitBlockOnFail to stop the loop when a request fail.
8.	Check the result of requests like the HTTP status, the current URL and the body using CSS selectors and regular expressions.
9.	Save a value into the session and use it in another request.
10.	Use asLongAs to go to the 5th page. The URL to go to the next page is stored in the session.
11.	Use randomSwitch and roundRobinSwitch to simulate a pseudo-random behavior.
12.	Use a custom exec to set a random value in the session and try to access to a computer that may not exist. We use tryMax to set the max number of attempts.
13.	Add pauses : fixed duration, uniform random duration or exponential random duration
14.	Simulate 100 users with a ramp of 20.

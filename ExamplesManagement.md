# Examples of Management Simulation Models #

### Service Queue ###

This is one of the simplest simulation examples.
Customers arrive at random times at a service desk where they have to wait in a queue when the service desk is busy. The time between two customer arrivals is uniformly distributed between 1 and 8 minutes. The times for completing a service vary from 1 to 6 minutes, with probabilities 0.1, 0.2, 0.3, 0.25, 0.1, 0.05, respectively. An arriving customer is represented by a newly generated object, which is destroyed again, when this customer leaves the system. The customer in service is represented by the first/topmost item in the First-In-First-Out queue, while the remaining items of the queue represent the waiting line. The goal is to collect the mean response time statistics (the length of time a customer spends in the system).

[View/Download Basic Model](http://hydrogen.informatik.tu-cottbus.de/aors/examples/Management/SingleQueue_withoutActivities/scenario.xml) (Download with Right-Click and "Save Link As")

[View/Download Extended Model Using an Activity](http://hydrogen.informatik.tu-cottbus.de/aors/examples/Management/SingleQueue_withActivities/scenario.xml) (Download with Right-Click and "Save Link As")


### Drive Thru Restaurant ###

This example is based on the paper [Introduction to Simulation](http://www.informs-sim.org/wsc08papers/005.pdf) by R.G. Ingalls (published in Proceedings of the 2008 Winter Simulation Conference).

As a car enters from the street, the driver, who we will call Fred, decides whether or not to get in line. If Fred decides to leave the restaurant, he leaves as a dissatisfied customer. If Fred decides to get in line, then he waits until the menu board is available. At that time, Fred gives the order to the order taker. After the order is taken, then two things occur simultaneously:
  1. Fred moves forward if there is room, otherwise he has to wait at the menu board until there is room to move forward.
  1. The order is sent electronically back to the kitchen where it is prepared as soon as the cook is available.
  1. As soon as Fred reaches the pickup window, then he pays and picks up his food, if it is ready. If the food is not ready, then Fred has to wait until his order is prepared.

[View/Download Basic Version](http://hydrogen.informatik.tu-cottbus.de/aors/examples/Management/DriveThruRestaurant/scenario.xml) (Download with Right-Click and "Save Link As")

In the basic version, the menu board order taker, the kitchen and the pickup window are modeled as objects.

[View/Download an Agent-Based Version](http://hydrogen.informatik.tu-cottbus.de/aors/examples/Management/DriveThruRestaurant_withAgents/scenario.xml) (Download with Right-Click and "Save Link As")

In the agent-based version, the menu board order taker, the kitchen and the pickup window are modeled as agents that communicate with each other by exchanging messages.

[View/Download an Extended Agent-Based Version Using Activities](http://hydrogen.informatik.tu-cottbus.de/aors/examples/Management/DriveThruRestaurant_withAgentsAndActivities/scenario.xml) (Download with Right-Click and "Save Link As")

In the extended agent-based version, the services of the menu board order taker, the kitchen and the pickup window are modeled as activities.
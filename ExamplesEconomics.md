# Examples of Economics Simulation Models #


## The MIT Beer Game (Supply Chain Dynamics) ##

This model describes the classical [MIT Beer Game](http://web.mit.edu/jsterman/www/SDG/beergame.html), a simulation model of a supply chain with four nodes: the Retailer, the Wholesaler, the Distributor and the Factory. Every intermediate node has one upstream node to order and receive beer from and one downstream node to receive orders from and to delivery beer to. An order takes 7 days and a delivery takes 14 days. At the end of a week every node decides how much beer to order and calculates its costs for current stock and outstanding orders.

[View/Download "The MIT Beer Game"](http://hydrogen.informatik.tu-cottbus.de/aors/examples/Economics/BeerGame/scenario.xml) (Download with Right-Click and "Save Link As")


## The Gold-Food Economy (A Minimal Economy Model) ##

This model was proposed by K. Steiglitz et al. in their paper [A computational market model based on individual action](http://www.cs.princeton.edu/~ken/scott.pdf) (1996). It is an example of an agent-based simulation of a relatively basic economy that does not directly model a real economy, but attempts to explain the macro-economy through simulation of a minimal economy.

It is one of the simplest models wherein zero-intelligence agents produce, consume and trade in an economy with only two goods: gold and food. The model simulates the actions of independent "worker" agents, each with his own inventory and skills (at producing food and gold), interacting through an auction market agent that establishes a commonly accepted transaction price. Workers must consume a unit of food every period, and each worker tries to maintain a minimum inventory of food to guarantee consumption. For the system to survive the production of food must be greater than the total food consumption per period, but this leads to a surplus of food in the economy. The market allows agents to sell their surplus food for gold that can be used to purchase food in the same market later on. Agents more skilled in the production of gold might decide to mine gold and trade it for food at every period.

[View/Download "The Gold-Food Minimal Economy Model"](http://hydrogen.informatik.tu-cottbus.de/aors/examples/Economics/MinimalEconomy_GoldFood/scenario.xml) (Download with Right-Click and "Save Link As")
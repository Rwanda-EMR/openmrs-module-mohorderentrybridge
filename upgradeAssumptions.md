# Here are a number of assumptions considered to bridge the gap between version 1.6.x and 1.11.x

* A new order is created whenever an order is discontinued whose orderReason would be the discontinueReason in 1.6.x
* dateActivated is equivalent to startDate in 1.6.x
* dateStopped is equivalent to stop/end Date in 1.6.x
* There is no disconued property in 1.6.x, isActive should be used instead
* A discontinued order has ACTION DISCONTINUE

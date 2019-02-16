# spring-reactive-mongodb

Example Application for Spring Webflux with Reactive Mongodb 

1. Upon every new insert of the data in the mongodb, reactive mongo publishes an event with the newly inserted data. 
2. This application subscribes for this event through the spring data mongodb repository. 
3. The subscribed event is then sent as the text-stream to the browser. 

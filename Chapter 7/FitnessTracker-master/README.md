# FitnessTracker
FitnessTracker  
Following lists the high level requirements of fitness tracker application:  

1. When the application starts for the first time, it should take the following authorizations from the user.  
  * To read their live fitness data with all the read scopes using Sensor API.  
  * To record their fitness data with all the read scopes using Recording API.  
  * To read their history of fitness data with all the read scopes using History API.  
2. The application should list all the available data sources for live data capture using the Sensors API.  
3. The application should capture the live data from available data sources. It should also allow adding and removing listeners using the Sensors API.  
4. The application should list all the active subscriptions with their data types using the Recording API.  
5. The application should allow adding and removing of subscriptions for a particular data type using the Recording API.  
6. The application should show the history of available fitness data type from selected date range to the user using History API.  
7. The application should show the aggregated history of available fitness data type by individual day using the buckets filter provided by History API.  

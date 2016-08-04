#Philosophy Crawler
Inspired by a wikipedia article on finding philosophy by traversing wikipedia by the first lower case link in the paragraph body.

[Wikipedia article](https://en.wikipedia.org/wiki/Wikipedia:Getting_to_Philosophy)

[Hosted Heroku Instance](https://philosocrawler.herokuapp.com/) <- micro instance, will take a really long time to wake up
upwards of a minute even, so please be patient.

## Features
* Crawls Wikipedia up to a configurable amount of maximum hops.
* Terminates if it detects a loop, times out or finds philosophy page.
* Timeout defaults to 10000 ms as there is some times some lag on wikipedia side.
* Stores baked graph results a blob into the database (well actually its a lob of type text)
* Able to accept random keywords, or a link to a particular page as well.
* Only supports english wikipedia at the moment, so links from other types wont be supported.
* TODO: Error reporting, errors are non fatal but do occur and arent reported yet to the user (such as time outs and loops)

##Configuration
When running the crawler provide the following environment variable, but change it so that it makes sense for the environment.

```
export JDBC_DATABASE_URL=jdbc:postgresql://192.168.56.101:5432/crawler?user=crawler&password=crawler
```

## Building
 To build execute
```
./gradlew build
```

To run execute
```
./gradlew bootRun
```

## Migrations
Are not implemented yet so be careful of hibernates ability to autoddl the heck out of entity tables.
If anything it wont hurt to clear the database once in a while.


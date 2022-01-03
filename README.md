# input-tables

input-tables is a repository for Deephaven input table examples. It was created as the source code for the blog post
["Deephaven data your way"](https://deephaven.io/blog/#todo).

## Build

The following examples can be built via

```shell
./gradlew installDist
```

This will create and package the runnable applications under the `app/build/install/app` directory.

## Running

These examples assume an already running Deephaven server.
See [deephaven-core#run-deephaven](https://github.com/deephaven/deephaven-core#run-deephaven) for more information.

By default, the applications will connect to the Deephaven target `dh+plain://localhost:10000`, but that can be changed
by setting the environment variable `DEEPHAVEN_TARGET` as appropriate.

### audit-log

The audit log is an example input table with a timestamp `Timestamp`, string `Type`, and string `Log` schema.

* [AuditLogCreate.java](app/src/main/java/io/deephaven/examples/audit_log/AuditLogCreate.java)
* [AuditLogAdd.java](app/src/main/java/io/deephaven/examples/audit_log/AuditLogAdd.java)

```shell
./app/build/install/app/bin/audit-log-create
./app/build/install/app/bin/audit-log-add NEW_USER "Added new user 'devin'" PASSWORD_RESET "Password reset for user 'devin'"
```

### city-weather

The city weather is an example input table with a string `City`, timestamp `Timestamp`, and double `Degrees` schema.

* [CityWeatherCreate.java](app/src/main/java/io/deephaven/examples/city_weather/CityWeatherCreate.java)
* [CityWeatherAdd.java](app/src/main/java/io/deephaven/examples/city_weather/CityWeatherAdd.java)

```shell
./app/build/install/app/bin/city-weather-create
./app/build/install/app/bin/city-weather-add "Minneapolis, MN" 35.4 "Seattle, WA" 53.7
```

### random-numbers-generator

The random numbers generator is an example input table with an int `Index`, timestamp `Timestamp`, int `Iteration`, int `Int`, long `Long`, and double `Double` schema.

* [RandomNumbersCreate.java](app/src/main/java/io/deephaven/examples/random_numbers/RandomNumbersCreate.java)
* [RandomNumbersGenerator.java](app/src/main/java/io/deephaven/examples/random_numbers/RandomNumbersGenerator.java)

```shell
./app/build/install/app/bin/random-numbers-create
./app/build/install/app/bin/random-numbers-generator
```

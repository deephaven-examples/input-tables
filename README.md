# input-tables

A collection of remote java client input table examples for table creation and data entry.

## Build

The following examples can be built via

```shell
./gradlew installDist
```

This will package and create the startup scripts under the `app/build/install/app` directory.

## Running

By default, the applications will connect to the Deephaven target `dh+plain://localhost:10000`, but that can be changed
by setting the environment variable `DEEPHAVEN_TARGET`.

### audit-log

* [AuditLogCreate.java](app/src/main/java/io/deephaven/examples/audit_log/AuditLogCreate.java)
* [AuditLogAdd.java](app/src/main/java/io/deephaven/examples/audit_log/AuditLogAdd.java)

```shell
./app/build/install/app/bin/audit-log-create
./app/build/install/app/bin/audit-log-add NEW_USER "Added new user 'devin'" PASSWORD_RESET "Password reset for user 'devin'"
```

### city-weather

* [CityWeatherCreate.java](app/src/main/java/io/deephaven/examples/city_weather/CityWeatherCreate.java)
* [CityWeatherAdd.java](app/src/main/java/io/deephaven/examples/city_weather/CityWeatherAdd.java)

```shell
./app/build/install/app/bin/city-weather-create
./app/build/install/app/bin/city-weather-add "Minneapolis, MN" 35.4 "Seattle, WA" 53.7
```

### random-numbers-generator

* [RandomNumbersCreate.java](app/src/main/java/io/deephaven/examples/random_numbers/RandomNumbersCreate.java)
* [RandomNumbersGenerator.java](app/src/main/java/io/deephaven/examples/random_numbers/RandomNumbersGenerator.java)

```shell
./app/build/install/app/bin/random-numbers-create
./app/build/install/app/bin/random-numbers-generator
```

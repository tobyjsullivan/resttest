# Bench Test

This project is a solution to Bench Accounting's
[assessment project](http://resttest.bench.co).

## Getting Started

Running this project is fairly easy.

1. Install [SBT](http://www.scala-sbt.org/download.html).
2. Clone the project repo

    `git clone https://github.com/tobyjsullivan/resttest.git`

3. In the project directory, run:

    `sbt run`

## Running Tests

You can run the unit tests for this project easily as well.

1. Follow the "Getting started" steps.
2. Run the tests with:

    `sbt test`

## Configuration

You can configure basic App parameters (such as the API base URL) by
modifying `src/main/resources/application.conf`.

## Navigating the Code

The basic packages for the project are as follows:

| Package | Description |
|---|---|
|net.tobysullivan.resttest| The main entry point to the application.|
|adapters| This package contains all code relevant to connecting to external systems. |
|adapters.bench| This package and subpackages contains all code specific to querying the Bench API at http://resttest.bench.co.|
|adapters.http| This package contains HTTP client code that could, presumably, be shared between multiple adapters.|
|cli| This code is specific to the App's CLI presentation mode (currently the only available mode).|
|models| These are models relevant to the entire app.|

## Design Decisions

### Streaming vs Futures

One design decision you'll likely notice immediately is a lack of
Futures, Actors, or other concurrency mechanisms.

There are two contexts where these were considered but discarded.

The first applicable context would be to request the multiple pages of
transactions in parallel. This functionality could be added in
conjunction with the current design (effectively requesting batches of
pages as we build the transaction stream) but this would only add a
linear performance boost. We would want to consider this feature if
it is found that many large statements are taking too long to fetch but
there's no reason to introduce this function prematurely - it'll only
complicate the code.

The second context would be something akin to a desire to avoid blocking
the rest of the app while network requests are being made. However, this
application currently only does one thing - display the aggregate results
of all fetches. These results can only be displayed once all data has
been fetched and reduced and so there is no practical way to provide an
experience which doesn't wait for all fetches and computations to
complete.
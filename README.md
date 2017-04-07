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
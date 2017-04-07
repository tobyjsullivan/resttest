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

### Interpretation of Spec

My reading of the [assessment project](http://resttest.bench.co) spec
lead me to believe the following:

* There is no reason to believe that transactions will be ordered by
date or otherwise.
* The exact page size is undefined and, therefore, should not be assumed
to remain at 10 transactions/page.
* As an alternative to referencing `totalCount`, it is reasonable to
assume that the last page has been reached when the next page returns a 404.

### Halt on HTTP Error

I made the decision to take a fail-fast philosophy and halt the
application on unexpected error responses from the API. Such errors
include 5XX-level server and 4XX-level errors other than 404. In either
of these cases, it is implied that our
code is incorrect (e.g., 400 BadRequest) or the server is experiencing
difficulties. In either case, there is no reasonable way for the
application to continue with the calculations without the missing data as
any results would be suspect.

One potential improvement would be to auto-retry on 5XX-level failures
in hope that the problem would self-correct.

### Streaming

I ultimately opted for a stream-based architecture for this application.
The basic idea is that we fetch and parse pages from the Bench API one
at a time and, as we do, produce a constant Stream of Transaction
objects which can be mapped, filtered, and iterated over. This allows
us to perform our aggregation functions (calculate total
balance) in a manner which consumes O(1) memory.

The alternative solution would be to follow a Map-Reduce pattern. This
approach would generally be faster with concurrency; however, the
trade-off would be
unpredictable memory consumption which could be a significant issue for
either very large statements (as it might lead to swapping and negative
performance) or situations where multiple client statements are being
read on the same machine (although that's not possible with the current
API).

Note: There is no benefit to the current streaming design if each page
has a very large number of transactions. The working assumption is that pages will
always contain a limited number of transactions (a thousand or fewer, say)
 and very large statements will be expressed through a very large number
 of pages.

### Lack of Concurrency

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

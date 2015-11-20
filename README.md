# RetryableAsyncTask

[![Build
Status](https://snap-ci.com/alabeduarte/retryableasynctask/branch/master/build_image)](https://snap-ci.com/alabeduarte/retryableasynctask/branch/master)

Android AsyncTask that retries the job in case of failure.

## Usage

```java
// Params, Progress and Result could be anything, same as a regular AsyncTask
new RetryableAsyncTask<Params, Progress, Result>(myActivity) {
  @Override
  protected void onPreExecute() {
    // write some code here
  }

  @Override
  protected Result doInBackground(Params... params) {
    // execute some expensive task here with your params
    // eg: MyExpensiveTask with method called 'get'

    return MyExpensiveTask.get(params);
  }

  @Override
  protected void onPostExecute(Result result) {
    // write some code here with your result
  }
  
  @Override
  protected void onError(Throwable error, final Params... params) {
    // write your own error handling
  }
}.execute(myParams);
```

## Install

### Gradle

```groovy
repositories {
  // ...
  maven { url "https://jitpack.io" }
}
```

```groovy
compile 'com.github.alabeduarte:retryableasynctask:0.0.3'
```

### Maven

```xml
<repository>
  <id>jitpack.io</id>
  <url>https://jitpack.io</url>
</repository>
```

```xml
<dependency>
  <groupId>com.github.alabeduarte</groupId>
  <artifactId>retryableasynctask</artifactId>
  <version>0.0.3</version>
</dependency>
```

## Contributing

There are many ways to contribute, such as fixing opened issues, creating them or suggesting new ideas.
Either way will be very appreciated.

If there are issues open, I recommend you follow those steps:

* Create a branch retryableasynctask#{issue_number}; eg: retryableasynctask#42
* Please, remember to write unit tests.
* Send a pull request!

## Running the tests

I strongly recommend to use [Android Studio](http://developer.android.com/sdk/index.html) for a
smooth integration, but follow the command below if you wanna run all unit tests by your own.

```sh
$ ./gradlew test
```


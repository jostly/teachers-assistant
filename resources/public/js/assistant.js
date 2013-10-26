$( document ).ready(function() {
  fetchStatus();
  setInterval(fetchStatus, 500);
});

colours = {
  passed: "#57E341",
  failed: "#F73214",
  buildErrorOnFailed: "#C01204",
  buildErrorOnPassed: "#12B322"
}

var currentStage;

function fetchStatus() {
	$.get('build', function( dataArray ) {

    var data = dataArray[0];
    if (data) {
      var oldTests = [];
      for (var i = 1; i < dataArray.length; i++) {
        if (dataArray[i].build === "complete") {
          oldTests = dataArray[i].tests;
          break;
        }
      }

      showElapsedTime( data.age );

      if (data.build === "complete") {
    		clearError();

        var numberOfTests = data.tests.length;
        var failures = 0;
        for (var test = 0; test < numberOfTests; test++) {
          if (data.tests[test].failure) {
            failures += 1;
            addFailedTest( data.tests[test], isNewFailure(data.tests[test], oldTests) )
          }
        }

        if (failures === 0) {
          setStage( colours.passed );
        } else {
          setStage( colours.failed );
        }

        if (numberOfTests > 0) {
          if (failures > 0) {
            setStatus( failures + " of " + numberOfTests + " tests failed" );
          } else {
            setStatus( "All " + numberOfTests + " tests passed!" );
          }
        } else if (!data.error) {
          setStatus( "No tests detected" );
        }

      } else {

        setError( "Build failed" );

      }
    } else {
      setStatus( "Waiting for first build..." );
    }


	});

}

function setStage( color ) {
    $('body').animate( {
        backgroundColor: color
    }, 200);
  currentStage = color;
}

function setStatus( status ) {
    $('#status').html( status );
}

function setError( error ) {
  $('#status').addClass( 'buildError' );
  $('#status').html( error );
  if (currentStage === colours.passed) {
    setStage( colours.buildErrorOnPassed );
  } else if (currentStage === colours.failed) {
    setStage( colours.buildErrorOnFailed );
  }
}

function clearError() {
  $('#status').removeClass( 'buildError' );
  $("#failed div").remove();
}

function isNewFailure( test, lastTests ) {
  for(var i = 0; i < lastTests.length; i++) {
    var oldTest = lastTests[i];
    if (oldTest.name === test.name &&
        oldTest.classname === test.classname &&
        oldTest.failure === test.failure) {
      return false;
    }
  }
  return true;
}

function addFailedTest( test, isNew ) {
  var testDisplay = $( '<div class="entry"/>' );

  var testHeader = $( '<div class="header"/>' );

  var className = $( '<div class="className">' + test.classname + '</div>');
  var testName = $( '<div class="testName">' + test.name + '</div>' );
  var testBody = $( '<div class="body">' + test.failure + '</div>' );

  if (isNew) {
    testDisplay.addClass('new');
    testName.addClass('new');
  }

  testDisplay.append(testHeader);
  testHeader.append(className);
  testHeader.append(testName);
  testDisplay.append(testBody);

  $("#failed").append(testDisplay);

}

function showElapsedTime( age ) {
  function show( v, unit ) {
    if (v > 1) {
      unit = unit + "s";
    }
    $('#time').html( (v > 0) ? (v + " " + unit + " ago") : "Just now");
  }
  var seconds = Math.round(age / 1000);
  if (seconds >= 3600) {
    show( Math.round(seconds/3600), 'hour' );
  } else if (seconds >= 60) {
    show( Math.round(seconds/60), 'minute' );
  } else {
    show( seconds, 'second' );
  }

}

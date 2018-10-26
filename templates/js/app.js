var locations = [
    {
        title: 'Haute-Kotto, Central African Republic',
        location: {
            lat: 8.179276,
            lng: 23.104608
        }
    },
    {
        title: 'Haut-Mbomou, Central African Republic',
        location: {
            lat: 6.677922,
            lng: 25.479455
        }
    },
    {
        title: 'Calimanesti, Romania',
        location: {
            lat: 45.270846,
            lng: 24.312363
        }
    },
    {
        title: 'Santa Terezinha, State of Mato Grosso, 78650-000, Brazil',
        location: {
            lat: -10.362986,
            lng: -50.544638
        }
    },
    {
        title: 'Xikrin do Rio Catete, State of Pará, Brazil',
        location: {
            lat: -6.589042,
            lng: -50.904527
        }
    },
    {
        title: 'Chinatown Homey Space',
        location: {
            lat: 40.7180628,
            lng: -73.9961237
        }
    },
    {
        title: 'United States, Georgia',
        location: {
            lat: 33.005098,
            lng: -84.918453
        }
    }
];

//
var first = false;
var not_found_error = "Not Found";

/*Map Initialization and Functions*/

var map;
var markers = [];
//https://drive.google.com/open?id=1TlDx4i1ccBCwqN60GYn26p4l7CYgDYco
//https://drive.google.com/open?id=1nttmtcbIiaTcnRLbt-FY1lr-lBgOPFr9
//https://drive.google.com/open?id=1nttmtcbIiaTcnRLbt-FY1lr-lBgOPFr9
// reddot https://drive.google.com/open?id=1a419nUBWpYJIDmniVBr4Dkms7C8Ip5hw
var fire_icon = "https://docs.google.com/uc?id=1nttmtcbIiaTcnRLbt-FY1lr-lBgOPFr9";
var red_icon = "https://docs.google.com/uc?id=1a419nUBWpYJIDmniVBr4Dkms7C8Ip5hw";
var largeInfowindow;
var timeout = 1400;

//var fs = require('fs');
//var longSampleRawCsv = fs.readFileSync(__dirname + '/FireData.csv', 'utf8');

function initMap() {
    // Constructor creates a new map - only center and zoom are required.
    map = new google.maps.Map(document.getElementById('map'), {
        center: new google.maps.LatLng(21.310132, -15.426230),
        zoom: 3
    });
    largeInfowindow = new google.maps.InfoWindow();

    for (var i = 0; i < locations.length; i++) {
        var position = locations[i].location;
        var title = locations[i].title;
        var marker = new google.maps.Marker({
            map: map,
            position: position,
            title: title,
            icon: fire_icon,
            animation: google.maps.Animation.BOUNCE,
            id: i
        });

        markers.push(marker);

        marker.addListener('click', marker_actions);
    }

    function marker_actions() {
        populateInfoWindow(this, largeInfowindow);
        if (this.getAnimation() !== null) {
            this.setAnimation(null);
        } else {
            var self = this;
            this.setAnimation(google.maps.Animation.BOUNCE);
            setTimeout(function() {
                self.setAnimation(null);
            }, timeout);
        }
    }
}

/*================================================*/

var stringStartsWith = function(string, startsWith) {
    string = string || "";
    if (startsWith.length > string.length)
        return false;
    return string.substring(0, startsWith.length) === startsWith;
};

var ViewModel = function() {

    self = this;
    self.locs = ko.observableArray([]);
    self.filter = ko.observable("");
    for (var i = 0; i < locations.length; i++) {
        self.locs.push(locations[i].title);
    }

    self.filtered = ko.computed(function() {
        var filter = self.filter().toLowerCase();
        if (!filter) {
            if (first)
                adapt_map_with_list(self.locs());
            return self.locs();
        } else {
            var temp = ko.utils.arrayFilter(self.locs(), function(item) {
                return stringStartsWith(item.toLowerCase(), filter);
            });
            first = true;
            adapt_map_with_list(temp);
            return temp;
        }
    });

    self.menu = function() {
        $(this).toggleClass('active');
        $('aside').animate({
            width: 'toggle'
        }, 200);
    };

    self.listAction = function() {
        var str = this;
        var id = (self.locs()).indexOf(str + "");
        marker_action_listView(markers[id]);
    };
};

ko.applyBindings(new ViewModel());
/*============================================*/

// This function will loop through the markers array and display them all.
function adapt_map_with_list(array_filter) {
    var bounds = new google.maps.LatLngBounds();
    // Extend the boundaries of the map for each marker and display the marker
    for (var i = 0; i < markers.length; i++) {
        var num = array_filter.indexOf(markers[i].title);
        if (num != -1) {
            markers[i].setMap(map);
            bounds.extend(markers[i].position);
        } else {
            markers[i].setMap(null);
        }
    }
    map.fitBounds(bounds);
}

function marker_action_listView(marker) {
    largeInfowindow = new google.maps.InfoWindow();
    if (marker.getAnimation() !== null) {
        marker.setAnimation(null);
    } else {
        populateInfoWindow(marker, largeInfowindow);
        marker.setAnimation(google.maps.Animation.BOUNCE);
        setTimeout(function() {
            marker.setAnimation(null);
        }, timeout);
    }
}

function populateInfoWindow(marker, infowindow) {
    // Check to make sure the infowindow is not already opened on this marker.
    if (infowindow.marker != marker) {

        var latt = marker.position.lat();
        var lang = marker.position.lng();
        var url = 'https://api.foursquare.com/v2/venues/search?ll=' + latt + ',' + lang + '&client_id=' + client + '&client_secret=' + sec + '&query=' + marker.title + '&v=20180206&m=foursquare';
        var cont;
        $.getJSON(url).done(function(marker) {
            var response = marker.response.venues[0];
            var stre = response && response.location && response.location.formattedAddress[0] || not_found_error;
            var ci = response && response.location && response.location.formattedAddress[1] || not_found_error;
            var name = response && response.name || not_found_error;
            cont =
                '<h1>(' + name + ')</h1>' +
                '<h3> Address: </h3>' +
                '<p>' + stre + '</p>' +
                '<h3> City: </h3>' +
                '<p>' + ci + '</p>';
            infowindow.marker = marker;
            infowindow.setContent(cont);
        }).fail(function() {
            // Send alert
            alert(
                "There is an error dealing with api, try again later"
            );
        });
        infowindow.open(map, marker);
        // Make sure the marker property is cleared if the infowindow is closed.
        infowindow.addListener('closeclick', function() {
            infowindow.setMarker = null;
            marker.setAnimation(null);
        });
    }
}

//------------------------------------------------------------------------------------

function handleFiles(files) {
    // Check for the various File API support.
    if (window.FileReader) {
        // FileReader are supported.
        getAsText(files[0]);
    } else {
        alert('FileReader are not supported in this browser.');
    }
}

function getAsText(fileToRead) {
    var reader = new FileReader();
    // Handle errors load
    reader.onload = loadHandler;
    reader.onerror = errorHandler;
    // Read file into memory as UTF-8      
    reader.readAsText(fileToRead);
}

function loadHandler(event) {
    var csv = event.target.result;
    processData(csv);
}

function processData(csv) {
    var allTextLines = csv.split(/\r\n|\n/);
    var lines = [];
    while (allTextLines.length) {
        lines.push(allTextLines.shift().split(','));
    }
    console.log("Loadded markers: " + lines.length);
    drawOutput(lines);
}


//if your csv file contains the column names as the first line
function processDataAsObj(csv) {
    var allTextLines = csv.split(/\r\n|\n/);
    var lines = [];

    //first line of csv
    var keys = allTextLines.shift().split(',');

    while (allTextLines.length) {
        var arr = allTextLines.shift().split(',');
        var obj = {};
        for (var i = 0; i < keys.length; i++) {
            obj[keys[i]] = arr[i];
        }
        lines.push(obj);
    }
    console.log("Loadded markers: " + lines.length);
    drawOutputAsObj(lines);
}

function errorHandler(evt) {
    if (evt.target.error.name == "NotReadableError") {
        alert("Canno't read file !");
    }
}

function view_custom_data(choice){
	
}

function read_data(file_name) {
    var rawFile = new XMLHttpRequest();
    rawFile.open("GET", file_name, true);
    rawFile.onreadystatechange = function() {
        if (rawFile.readyState === 4) { // Makes sure the document is ready to parse.
            if (rawFile.status === 200 || rawFile.status == 0) { // Makes sure it's found the file.
                allText = rawFile.responseText;
                allTextLines = allText.split("/\r\n|\n/"); // Will separate each line into an array
                var lines = [];

                //first line of csv
                var keys = allTextLines.shift().split(',');

                while (allTextLines.length) {
                    var arr = allTextLines.shift().split(',');
                    var obj = {};
                    obj[keys[0]] = arr[0];
                    obj[keys[1]] = arr[1];
                    lines.push(obj);
                    console.log(obj);
                }
                console.log("number of lines is: ");
                console.log(lines.length);

                //drawOutputAsObj(lines);
            } else{
            	alert("File `"+ file_name +"` is not found");
            }
        } else{
        	alert("File `"+ file_name +"` is not ready/found")
        }
    }
    rawFile.send(null)
}

function drawOutput(lines) {
    //Clear previous data
    //	document.getElementById("output").innerHTML = "";
    //	var table = document.createElement("table");
    largeInfowindow = new google.maps.InfoWindow();
    for (var i = 1; i < 10000; i++) {
        var position = {
            lat: Number(lines[i][0]),
            lng: Number(lines[i][1])
        };
        var marker = new google.maps.Marker({
            map: map,
            position: position,
            icon: red_icon,
            id: i
        });
        markers.push(marker);
        marker.addListener('click', marker_actions);
    }
    //	document.getElementById("output").appendChild(table);
}

//draw the table, if first line contains heading
function drawOutputAsObj(lines) {
    //the data
    largeInfowindow = new google.maps.InfoWindow();
    for (var i = 1; i < 10000; i++) {
        var position = {
            lat: Number(lines[i][0]),
            lng: Number(lines[i][1])
        };
        var marker = new google.maps.Marker({
            map: map,
            position: position,
            icon: red_icon,
            id: i
        });
        markers.push(marker);
        marker.addListener('click', marker_actions);
    }
    //	document.getElementById("output").appendChild(table);
}


function marker_actions() {
    populateInfoWindow(this, largeInfowindow);
    if (this.getAnimation() !== null) {
        this.setAnimation(null);
    } else {
        var self = this;
        this.setAnimation(google.maps.Animation.BOUNCE);
        setTimeout(function() {
            self.setAnimation(null);
        }, timeout);
    }
}
//---------------------------------------------------------------------------------

var err = function err() {
    alert(
        'There is an error while Loading, Try again'
    );
};


/*Info api from Foursquare */
var client = "R00ABNGHFWLZPZXS3Y1WTIMEWC0YRWPRODGQBMTONSBYJI4J";
var sec = "4Z5SHLHLFA023RY4GR1EBRL4V4T5Z5GE13SVJYLLUF4RQ3A1";
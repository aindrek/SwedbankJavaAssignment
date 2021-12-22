# Test Assignment
Assigment specifications can be found in the Assignment_2021.pdf file.

### Running
You can either download a compiled jar from the releases or import the Maven project and build it yourself.
The application will listen on localhost:8080

## API Documentation

### Post Requests
#### Single post

```POST /api/purchase/post```

Example body of a post request:

```
{
  "pricePerShare": 100,
  "volumeOfShares": 1000,
  "acquiredDate": "2021-11-22",
  "employeeId": "MM001",
  "companyName": "Enefit Green AS",
  "shareName": "EGR1T",
  "shareIsin": "EE3100137985",
  "country": "Estonia",
  "fieldOfEconomicActivity": "Utilities"
}
```


#### Multi posting
```POST /api/purchase/post/file```

Multiple entries have to be formated in a .csv file in a similar manner to the example file ```test.csv```

### Get Requests

All GET requests have an optional parameter ```employeeId``` to get information about a specific employee.

#### Get total spent amount of money grouped by month.

```GET /api/purchase/get/total```

Example response:

```
{
  "2021-12":421.99999999999994,
  "2021-11":100000.0
}
```

#### List share acquiring records for specified month.

```GET /api/purchase/get/groupBy/{year}/{month}```

Where {year} and {month} represent the year and month, you are looking to examine.

Example response:

```
[
  {
    "id":1,
    "pricePerShare":100.0,
    "volumeOfShares":1000,
    "acquiredDate":"2021-11-22",
    "employeeId":"MM001",
    "companyName":"Enefit Green AS",
    "shareName":"EGR1T",
    "shareIsin":"EE3100137985",
    "country":"Estonia",
    "fieldOfEconomicActivity":"Utilities",
    "totalPrice":100000.0
  }
]
```

#### Statistics for each month, list share acquiring records grouped by share id.

```GET /api/purchase/get/groupByShare```

Example response:

```
{
    "2021-12": {
        "EE3100137985": {
            "totalVolume": 40,
            "shareIsin": "EE3100137985",
            "country": "Estonia",
            "totalPrice": 421.99999999999994,
            "companyName": "Enefit Green AS",
            "fieldOfEconomicActivity": "Utilities",
            "shareName": "EGR1T",
            "averagePrice": 10.549999999999999
        }
    },
    "2021-11": {
        "EE3100137985": {
            "totalVolume": 1000,
            "shareIsin": "EE3100137985",
            "country": "Estonia",
            "totalPrice": 100000.0,
            "companyName": "Enefit Green AS",
            "fieldOfEconomicActivity": "Utilities",
            "shareName": "EGR1T",
            "averagePrice": 100.0
        }
    }
}
```

 ## How to run the project

If you want to use your own api-key you got from NASA you need to modify the _config.txt_ file in the root of the project which has the following content:

        db-database-jdbc-uri="jdbc:postgresql://localhost:5444/iridium"
        db-user=iridium
        db-password=iridium
        client-config.api-key=DEMO_KEY

You need a PostgreSQL database where you execute the following SQL script:

    CREATE TABLE favourites (
        id INT PRIMARY KEY,
        name VARCHAR(255) UNIQUE NOT NULL
    );

Please run db/docker-compose.yml to start the database.

Then you can run the project with the following command:

    sbt server/run
    
Please see _test.http_ file for the API documentation.

_ApplicationTest.scala_ contains the integration tests.

_LogicTest.scala_ contains the business logic tests about ranges having more than 7 days.

_CirceTest.scala_ contains the JSON parsing tests.

Although the project template I use is a full-stack application, I have only implemented the backend side. The frontend side is not implemented.


## Tech challenge 1

Suppose you have a database with three tables: "users", "orders", and "products". The "users" table contains columns id, name, and email. The "orders" table contains columns id, user_id, product_id, quantity, and created_at. The "products" table contains columns id, name, price, and category.

Write a single SQL query that returns a list of all users who have made at least 3 orders in the "Electronics" category and have spent more than $1000 on those orders, sorted by the total amount they have spent in descending order. The output should include the user's name, email, and the total amount they have spent on "Electronics" orders.

## Tech challenge 2

_You'll be asked of developing a small full-stack application (with both backend and frontend) using the best of your knowledges, taking into account best practices and code reusability.
We also want you to have fun with this!_

Take the test and submit us your solution. Please don't use the word Iridium on your repo name.
We're keen on reading your code, so take the test only if you want to!

### Test description

Create an app to see information about asteroids.
The app should:
- Display a list of asteroids
- Search by a range of dates
- See the detail of the asteroids by clicking on one of the items
- Sort the asteroids by name

Optional
- Add them to favourite
- Show a list of favourite
- Display details of favourite asteroids by click on the items form the list

*Don't forget that this must be a full-stack application.*
We expect an app with the implementation of a backend and a frontend side.

Use the following API:

- <https://api.nasa.gov/neo/rest/v1/feed?start_date=2015-09-07&end_date=2015-09-08&api_key=DEMO_KEY>

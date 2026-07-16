# Artisanal Food Exchange

A full-stack marketplace application connecting local artisanal food producers with consumers. Consumers can discover nearby producers, browse and order handcrafted food products, subscribe to recurring deliveries, and leave reviews. Producers get a dashboard with sales analytics.\
This is the deployed link to see how it look like :- artisanal-food-app2311-byfcf7dzcwcwbte8.centralindia-01.azurewebsites.net

Built with **Spring Boot 3 (Java 17)** on the backend, **MongoDB** for persistence, and a vanilla **HTML/CSS/JS** frontend (Chart.js for analytics charts).

## Features

- **Two user roles**: Consumer and Producer
- **Product catalog** with category filters, keyword search, and geolocation-based "near me" filtering (distance calculated from latitude/longitude)
- **Ordering** — place orders, view order history by consumer or by producer
- **Subscriptions** — recurring deliveries (weekly/biweekly/monthly) with active/paused status
- **Reviews & ratings** per product
- **Producer analytics dashboard** — sales breakdown by product, powered by Chart.js
- **Data seeder** — automatically populates the database with sample users, products, orders, reviews, and subscriptions on every startup

## Tech Stack

| Layer      | Technology                                   |
|------------|-----------------------------------------------|
| Backend    | Java 17, Spring Boot 3.3.4 (Web, Validation)  |
| Database   | MongoDB (Spring Data MongoDB)                 |
| Frontend   | HTML5, CSS3, vanilla JavaScript               |
| Charts     | Chart.js (CDN)                                |
| Fonts/Icons| Google Fonts, Font Awesome (CDN)              |
| Build Tool | Maven                                         |

## Project Structure

```
Artisanal Food exchange/
├── pom.xml
├── src/main/java/com/example/artisanalfood/
│   ├── ArtisanalFoodApplication.java   # Spring Boot entry point
│   ├── DataSeeder.java                 # Seeds sample data on startup
│   ├── controller/                     # REST controllers
│   │   ├── AnalyticsController.java
│   │   ├── OrderController.java
│   │   ├── ProductController.java
│   │   ├── ReviewController.java
│   │   ├── SubscriptionController.java
│   │   └── UserController.java
│   ├── dto/
│   │   └── ProducerAnalytics.java
│   ├── model/                          # MongoDB document models
│   │   ├── Order.java
│   │   ├── Product.java
│   │   ├── Review.java
│   │   ├── Subscription.java
│   │   └── User.java
│   ├── repository/                     # Spring Data MongoDB repositories
│   └── service/                        # Business logic
└── src/main/resources/
    ├── application.properties
    └── static/                         # Frontend (served by Spring Boot)
        ├── index.html
        ├── index.css
        ├── app.js
        └── images/
```

## Prerequisites

- Java 17+
- Maven 3.6+
- MongoDB running locally (default: `mongodb://localhost:27017`)

## Getting Started

1. **Start MongoDB** locally (default port `27017`).

2. **Configure** `src/main/resources/application.properties` if your MongoDB instance differs from the default:
   ```properties
   server.port=8080
   spring.data.mongodb.uri=mongodb://localhost:27017/artisanal_food_db
   spring.data.mongodb.database=artisanal_food_db
   ```

3. **Run the application**:
   ```bash
   mvn spring-boot:run
   ```
   Or build and run the jar:
   ```bash
   mvn clean package
   java -jar target/artisanal-food-exchange-0.0.1-SNAPSHOT.jar
   ```

4. **Open the app** at [http://localhost:8080](http://localhost:8080)

> Note: `DataSeeder` clears and repopulates all collections (users, products, orders, reviews, subscriptions) every time the app starts, so the database resets to sample data on each run.

## API Overview

Base path: `/api`

### Users — `/api/users`
| Method | Endpoint            | Description                |
|--------|----------------------|----------------------------|
| POST   | `/api/users`          | Create a user              |
| GET    | `/api/users/{id}`     | Get user by ID             |
| GET    | `/api/users/producers`| List all producers         |
| GET    | `/api/users`          | List all users             |

### Products — `/api/products`
| Method | Endpoint                          | Description                                                                 |
|--------|-------------------------------------|-------------------------------------------------------------------------------|
| POST   | `/api/products`                     | Create a product                                                             |
| GET    | `/api/products/{id}`                | Get product by ID                                                            |
| GET    | `/api/products`                     | List products (query params: `category`, `search`, `latitude`, `longitude`, `maxDistance`) |
| GET    | `/api/products/producer/{producerId}`| List products by producer                                                   |
| DELETE | `/api/products/{id}`                | Delete a product                                                             |

When `latitude`/`longitude` are provided, results include distance from that point and can be limited with `maxDistance`.

### Orders — `/api/orders`
| Method | Endpoint                        | Description                     |
|--------|-----------------------------------|----------------------------------|
| POST   | `/api/orders`                     | Place an order                   |
| GET    | `/api/orders?userId={userId}`     | List orders for a consumer       |
| GET    | `/api/orders/producer/{producerId}`| List orders for a producer       |

### Reviews — `/api/products/{productId}/reviews`
| Method | Endpoint                                | Description                |
|--------|--------------------------------------------|-----------------------------|
| POST   | `/api/products/{productId}/reviews`         | Add a review                |
| GET    | `/api/products/{productId}/reviews`         | List reviews for a product  |

### Subscriptions — `/api/subscriptions`
| Method | Endpoint                          | Description                            |
|--------|-------------------------------------|------------------------------------------|
| POST   | `/api/subscriptions`                | Create a subscription                    |
| GET    | `/api/subscriptions?userId={userId}`| List subscriptions for a user            |
| PUT    | `/api/subscriptions/{id}/status`    | Toggle subscription status (active/paused)|
| DELETE | `/api/subscriptions/{id}`           | Cancel a subscription                    |

### Analytics — `/api/analytics`
| Method | Endpoint                            | Description                            |
|--------|---------------------------------------|------------------------------------------|
| GET    | `/api/analytics/producer/{producerId}`| Sales analytics for a producer           |

## Data Models

- **User** — `id`, `name`, `email`, `role` (`CONSUMER`/`PRODUCER`), `bio`, `latitude`, `longitude`, `address`, `rating`
- **Product** — `id`, `name`, `description`, `price`, `unit`, `imageUrl`, `origin`, `ingredients`, `producerId`, `producerName`, `category`, `stock`, `latitude`, `longitude`
- **Order** — `id`, `userId`, `userName`, `productId`, `productName`, `producerId`, `price`, `quantity`, `totalAmount`, `createdAt`
- **Review** — `id`, `productId`, `userId`, `userName`, `rating` (1–5), `comment`, `createdAt`
- **Subscription** — `id`, `userId`, `productId`, `productName`, `producerName`, `frequency` (`WEEKLY`/`BIWEEKLY`/`MONTHLY`), `status` (`ACTIVE`/`PAUSED`), `quantity`, `nextDeliveryDate`

## Frontend

The frontend is a single-page app served as static resources from `src/main/resources/static/`:
- `index.html` — login screen (consumer/producer), marketplace, product details, cart/orders, subscriptions, and producer dashboard views
- `app.js` — handles API calls and UI state
- `index.css` — styling
- `images/` — sample product images (apples, cheese, ghee, honey)

## License

No license specified.

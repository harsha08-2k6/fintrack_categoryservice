# Category Microservice

Manages spending categories, budget limits, icons, and visual color codes.

- **Port**: `8082`
- **Database**: `spendwise_category`
- **Endpoints**:
  - `GET /api/categories` - List default & custom categories
  - `GET /api/categories/{id}` - Get category by ID
  - `POST /api/categories` - Create new category
  - `PUT /api/categories/{id}` - Update custom category
  - `DELETE /api/categories/{id}` - Delete custom category

### Running
```bash
mvn spring-boot:run
```

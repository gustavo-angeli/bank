## Bank API Documentation

| Method | Url                     | Description                               | Request Body         | Headers                 | 
|--------|-------------------------|-------------------------------------------|----------------------|-------------------------|
| POST   | /api/v1/auth            | User login                                | [JSON](#login)       |                         |
| PUT    | /api/v1/auth            | Generate new tokens                       |                      | [Authorization](#token) |
| POST   | /api/v1/management      | Create an account                         | [JSON](#create)      |                         |
| DELETE | /api/v1/management      | Delete account with token                 |                      | [Authorization](#token) |
| DELETE | /api/v1/management/{id} | Delete account with UUID (**Admin only**) |                      | [Authorization](#token) |
| PATCH  | /api/v1/transaction     | Deposit money into an account             | [JSON](#transaction) |                         |
| PATCH  | /api/v1/transaction     | Transfer money to an account              | [JSON](#transaction) | [Authorization](#token) |




#### <a id="create"> Create Request Body -> /api/v1/management</a>
```json
{
  "username": "user",
  "email": "user@email.com",
  "password": "1234"
}
```

#### <a id="login"> Login Request Body -> /api/v1/auth</a>

```json
{
  "email": "user@email.com",
  "password": "1234"
}
```

#### <a id="transaction"> Deposit and Transfer Request Body -> /api/v1/auth</a>

```json
{
  "to": "user@email.com",
  "value": "1000" // or 10.12
}
```

#### <a id="token"> Authorization Header -> /api/v1/auth</a>

```Authorization Header
Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJndXN0YSIsInJvbGVzIjpbIlJPTEVfVVNFUiJdLCJleHAiOjE3MDI3NDM3NzMsImlhdCI6MTcwMjc0Mzc3M30.38hldLfR8XehVPf-gZk15HetE1lVoM9w63XqPjDmxay4JTgdvA8JzWnZ9Mr63Lz9Jd-e0BqZXYx0JgfTGLud8A
```
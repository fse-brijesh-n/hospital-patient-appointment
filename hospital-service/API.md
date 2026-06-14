
- Start auth-service on port 8001.

- Run mvn spring-boot:run from the hospital-service folder.

- Obtain a Super Admin token:

```curl -X POST http://localhost:8001/auth/login -H "Content-Type: application/json" -d "{\"email\":\"admin@healthcare.com\",\"password\":\"Admin@123\"}"```

- Create a hospital:


```
curl -X POST http://localhost:8002/hospitals -H "Authorization: Bearer <super-admin-token>" -H "Content-Type: application/json" -d "{\"name\":\"City Hospital\",\"address\":\"123 Main St\",\"phone\":\"+1234567890\",\"email\":\"info@cityhospital.com\",\"adminEmail\":\"admin@cityhospital.com\",\"adminPassword\":\"Admin@123\"}"
```
- Verify public listing:


```curl http://localhost:8002/hospitals```
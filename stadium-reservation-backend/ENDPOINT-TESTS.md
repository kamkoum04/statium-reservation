# Endpoint Test Cookbook — Stadium Reservation API

All examples assume the backend is running on **http://localhost:8081** (adjust the `BASE` variable if not).

```bash
BASE=http://localhost:8081
```

> Tip: paste the snippets into a Linux/macOS shell, **Git Bash on Windows**, or WSL. For PowerShell, replace `curl` with `curl.exe` and adapt single‑quotes accordingly.

---

## 0. Endpoint matrix

| # | Method | Path                                       | Auth required          |
| - | ------ | ------------------------------------------ | ---------------------- |
| 1 | POST   | `/auth/register`                           | public                 |
| 2 | POST   | `/auth/login`                              | public                 |
| 3 | POST   | `/auth/refresh`                            | public (token in body) |
| 4 | GET    | `/admin/get-all-users`                     | ADMIN                  |
| 5 | GET    | `/admin/get-users/{userId}`                | ADMIN                  |
| 6 | PUT    | `/adminuser/update/{userId}`               | ADMIN or USER          |
| 7 | GET    | `/adminuser/get-profile`                   | ADMIN or USER          |
| 8 | DELETE | `/admin/delete/{userId}`                   | ADMIN                  |
| 9 | POST   | `/public/stadium/addStadium`               | public (multipart)     |
|10 | GET    | `/public/stadiums`                         | public                 |
|11 | PUT    | `/admin/stadium/update/{id}`               | ADMIN                  |
|12 | DELETE | `/admin/stadium/delete/{id}`               | ADMIN                  |
|13 | POST   | `/public/footballMatch/addFootball`        | public (multipart)     |
|14 | GET    | `/public/FootballMatch`                    | public                 |
|15 | PUT    | `/admin/footballMatch/update/{id}`         | ADMIN                  |
|16 | DELETE | `/admin/footballMatch/delete/{id}`         | ADMIN                  |
|17 | GET    | `/public/matchSeat/{matchId}`              | public                 |
|18 | POST   | `/public/reserve`                          | public                 |
|19 | GET    | `/admin/ticket`                            | ADMIN                  |
|20 | GET    | `/public/ticket/{userId}`                  | public                 |
|21 | DELETE | `/public/delete/{ticketId}`                | public                 |
|22 | GET    | `/actuator/health`                         | public                 |
|23 | GET    | `/swagger-ui/index.html`                   | public                 |

> CORS is wide open (`*`) — convenient for development, tighten before going to production.

---

## 1. Register two users (admin + regular)

```bash
curl -s -X POST $BASE/auth/register \
  -H "Content-Type: application/json" \
  -d '{"nom":"Admin","prenom":"Root","email":"admin@test.com","password":"adminpass","role":"ADMIN","cin":12345678}'

curl -s -X POST $BASE/auth/register \
  -H "Content-Type: application/json" \
  -d '{"nom":"User","prenom":"One","email":"user@test.com","password":"userpass","role":"USER","cin":87654321}'
```

## 2. Login and capture tokens

```bash
ADMIN_TOKEN=$(curl -s -X POST $BASE/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@test.com","password":"adminpass"}' \
  | python3 -c "import sys,json;print(json.load(sys.stdin)['token'])")

USER_TOKEN=$(curl -s -X POST $BASE/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@test.com","password":"userpass"}' \
  | python3 -c "import sys,json;print(json.load(sys.stdin)['token'])")

echo "ADMIN=$ADMIN_TOKEN"
echo "USER=$USER_TOKEN"
```

## 3. Refresh

```bash
curl -s -X POST $BASE/auth/refresh \
  -H "Content-Type: application/json" \
  -d "{\"token\":\"$ADMIN_TOKEN\"}"
```

## 4–8. User management

```bash
# List all users (ADMIN)
curl -s -H "Authorization: Bearer $ADMIN_TOKEN" $BASE/admin/get-all-users

# Get one user (ADMIN)
curl -s -H "Authorization: Bearer $ADMIN_TOKEN" $BASE/admin/get-users/1

# Own profile (any logged-in user)
curl -s -H "Authorization: Bearer $USER_TOKEN" $BASE/adminuser/get-profile

# Update a user
curl -s -X PUT -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"nom":"User","prenom":"Updated","email":"user@test.com","cin":87654321,"role":"USER"}' \
  $BASE/adminuser/update/2

# Delete (ADMIN)
# curl -X DELETE -H "Authorization: Bearer $ADMIN_TOKEN" $BASE/admin/delete/2
```

Expected access-control behaviour:

```bash
curl -s -o /dev/null -w "no token: %{http_code}\n" $BASE/admin/get-all-users           # 403
curl -s -o /dev/null -w "user on admin: %{http_code}\n" -H "Authorization: Bearer $USER_TOKEN" $BASE/admin/get-all-users  # 403
```

## 9–12. Stadium

```bash
# Tiny 1×1 PNG for the multipart payloads
echo -n "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNkYAAAAAYAAjCB0C8AAAAASUVORK5CYII=" \
  | base64 -d > /tmp/img.png

# Create
curl -s -X POST $BASE/public/stadium/addStadium \
  -F 'stadiumDto={"name":"Stade Olympique","location":"Tunis","blocks":[{"name":"BlocA","totalPlace":3,"defaultPrice":50.0},{"name":"BlocB","totalPlace":3,"defaultPrice":30.0}]};type=application/json' \
  -F "file=@/tmp/img.png;type=image/png"

# List
curl -s $BASE/public/stadiums

# Update (ADMIN)
curl -s -X PUT -H "Authorization: Bearer $ADMIN_TOKEN" \
  -F 'stadiumDto={"name":"Stade Olympique v2","location":"Tunis"};type=application/json' \
  -F "file=@/tmp/img.png;type=image/png" \
  $BASE/admin/stadium/update/1

# Delete (ADMIN)
# curl -X DELETE -H "Authorization: Bearer $ADMIN_TOKEN" $BASE/admin/stadium/delete/1
```

## 13–16. Football match

```bash
# Create — requires an existing stadium ID
curl -s -X POST $BASE/public/footballMatch/addFootball \
  -F 'footballMatchDto={"date":"2026-12-01","time":"20:00:00","equipe1":"EST","equipe2":"CA","stadiumId":1,"matchPriceModifier":5.0};type=application/json' \
  -F "equipe1Logo=@/tmp/img.png;type=image/png" \
  -F "equipe2Logo=@/tmp/img.png;type=image/png"

# List
curl -s $BASE/public/FootballMatch

# Update (ADMIN)
curl -s -X PUT -H "Authorization: Bearer $ADMIN_TOKEN" \
  -F 'footballMatchDto={"date":"2026-12-02","time":"21:00:00","equipe1":"EST","equipe2":"CA","stadiumId":1,"matchPriceModifier":10.0};type=application/json' \
  -F "equipe1Logo=@/tmp/img.png;type=image/png" \
  -F "equipe2Logo=@/tmp/img.png;type=image/png" \
  $BASE/admin/footballMatch/update/1

# Delete (ADMIN)
# curl -X DELETE -H "Authorization: Bearer $ADMIN_TOKEN" $BASE/admin/footballMatch/delete/1
```

## 17. Seats for a match

`seatPrice = defaultPrice (bloc) + matchPriceModifier`.

```bash
curl -s $BASE/public/matchSeat/1
```

Sample row:

```json
{
  "id": 1,
  "seatPrice": 55.0,
  "placeNuméro": 1,
  "placeId": 1,
  "blocName": "BlocA",
  "blocId": 1,
  "reserved": false
}
```

## 18. Reserve seats

```bash
curl -s -X POST $BASE/public/reserve \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 2,
    "footballMatchId": 1,
    "userEmail": "user@test.com",
    "seatRequests": [
      { "seatNumber": 1, "blocId": 1 }
    ]
  }'
```

> If SMTP credentials are not configured, the JSON response will include `"Authentication failed"` for the e‑mail step — the ticket itself **is** persisted (verify with the next two calls).

## 19. List all tickets (ADMIN)

```bash
curl -s -H "Authorization: Bearer $ADMIN_TOKEN" $BASE/admin/ticket
```

## 20. List one user's tickets

```bash
curl -s $BASE/public/ticket/2
```

## 21. Delete a ticket (frees the seat)

```bash
curl -s -X DELETE $BASE/public/delete/1
```

## 22–23. Misc

```bash
curl -s $BASE/actuator/health
curl -I -s $BASE/swagger-ui/index.html | head -1
curl -s $BASE/v3/api-docs | head -c 400
```

---

## Full one-shot script

Save as `test-all.sh`, then `bash test-all.sh`:

```bash
#!/usr/bin/env bash
set -euo pipefail
BASE=${BASE:-http://localhost:8081}

echo "== Register =="
curl -s -X POST $BASE/auth/register -H "Content-Type: application/json" \
  -d '{"nom":"Admin","prenom":"Root","email":"admin@test.com","password":"adminpass","role":"ADMIN","cin":12345678}' >/dev/null || true
curl -s -X POST $BASE/auth/register -H "Content-Type: application/json" \
  -d '{"nom":"User","prenom":"One","email":"user@test.com","password":"userpass","role":"USER","cin":87654321}' >/dev/null || true

echo "== Login =="
ADMIN_TOKEN=$(curl -s -X POST $BASE/auth/login -H "Content-Type: application/json" \
  -d '{"email":"admin@test.com","password":"adminpass"}' | python3 -c "import sys,json;print(json.load(sys.stdin)['token'])")

echo "== Stadium =="
echo -n "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNkYAAAAAYAAjCB0C8AAAAASUVORK5CYII=" | base64 -d > /tmp/img.png
curl -s -X POST $BASE/public/stadium/addStadium \
  -F 'stadiumDto={"name":"Stade Olympique","location":"Tunis","blocks":[{"name":"BlocA","totalPlace":3,"defaultPrice":50.0},{"name":"BlocB","totalPlace":3,"defaultPrice":30.0}]};type=application/json' \
  -F "file=@/tmp/img.png;type=image/png" >/dev/null

echo "== Match =="
curl -s -X POST $BASE/public/footballMatch/addFootball \
  -F 'footballMatchDto={"date":"2026-12-01","time":"20:00:00","equipe1":"EST","equipe2":"CA","stadiumId":1,"matchPriceModifier":5.0};type=application/json' \
  -F "equipe1Logo=@/tmp/img.png;type=image/png" \
  -F "equipe2Logo=@/tmp/img.png;type=image/png" >/dev/null

echo "== Seats =="
curl -s $BASE/public/matchSeat/1 | python3 -c "import sys,json;print('seats:',len(json.load(sys.stdin)))"

echo "== Reserve =="
curl -s -X POST $BASE/public/reserve -H "Content-Type: application/json" \
  -d '{"userId":2,"footballMatchId":1,"userEmail":"user@test.com","seatRequests":[{"seatNumber":1,"blocId":1}]}' >/dev/null

echo "== Admin tickets =="
curl -s -H "Authorization: Bearer $ADMIN_TOKEN" $BASE/admin/ticket | python3 -c "import sys,json;print('tickets:',len(json.load(sys.stdin)))"
```

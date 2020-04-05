```bash
$ sbt docker:stage
$ sudo chmod 700 apiServer/target/docker/stage/opt/docker/bin/commodity-exchange-server
$ docker-compose up --build
```


```bash
$ export API_ENDPOINT=localhost:8080/v2

$ curl -X POST -H "Content-Type: application/json" -d '{"mail":"bambootuna@gmail.com","pass":"pass"}' ${API_ENDPOINT}/signup -i
$ curl -X POST -H "Content-Type: application/json" -d '{"mail":"bambootuna@gmail.com","pass":"pass"}' ${API_ENDPOINT}/signin -i
// HeadreName: Set-AuthorizationにSessionTokenがセットされている

$ export SESSION_TOKEN=[~~~]
$ curl -X GET -H "Authorization: $SESSION_TOKEN" ${API_ENDPOINT}/health -i
```

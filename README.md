# Demo Spring Webflux

### Webclient

To test `WebClient` we use [jsonplaceholder](https://jsonplaceholder.typicode.com/guide/) posts
endpoint (https://jsonplaceholder.typicode.com/posts)

###### Features

- [x] Get all posts
- [ ] Get one post (Path parameter)
- [ ] Create new post (POST)
- [ ] Update existing post (PUT)
- [ ] Update part of existing post (PATCH)
- [ ] Delete post (DELETE)
- [ ] Get all posts using userId (Query parameters)
- [x] Retry when error occur (Retryable)
- [x] Configure timeout

###### Make it run

- `mvn clean install` to build
- `mvn spring-boot:run` to run
- `curl http://localhost:8080/posts`

---

### Spring Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.7.5/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.7.5/maven-plugin/reference/html/#build-image)
* [Spring Reactive Web](https://docs.spring.io/spring-boot/docs/2.7.5/reference/htmlsingle/#web.reactive)

### Guides

The following guides illustrate how to use some features concretely:

* [Building a Reactive RESTful Web Service](https://spring.io/guides/gs/reactive-rest-service/)



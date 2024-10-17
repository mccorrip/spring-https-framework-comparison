<h1>Framework Comparison</h1>

This project implements a typical REST API, utilising different HTTP classes in SpringBoot.
Each implementation as it's own branch. The branches are as follows:
1. RestTemplate
2. RestClient
3. WebClient

<h3>Package</h3>

```mvn clean package```

<h3>Docker Build</h3>

```docker build -t tomcat-21-restclient:latest .```

<h3>Docker Run</h3>
Each branch includes a docker compose file that launches all required dependencies:
1. Downstream test service
2. Postgres database populated with data

```docker compose up```
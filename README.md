# Game Management System

The Game Management System is a RESTful API application built with Spring Boot for managing games and developers.

## Running the Application

### Prerequisites

- Docker Engine or Docker Desktop running

### Steps to Run

1. **Clone the Repository**

   ```bash
   git clone https://github.com/olivertate9/VideoGames.git

2. **Run docker-compose from docker-compose.yml in IDE or in CLI**
   ```bash
   docker-compose up

3. **Build the Application**

   ```bash
   mvn clean package

4. **Run main method**
   
    from `dev/profitsoft/videogames/VideoGamesApplication.java`

5. **Access the API Documentation**
   
   Once the application is running, you can access the Swagger UI to explore and interact with the APIs: [http://localhost:8080/videogames-api-ui.html](http://localhost:8080/videogames-api-ui.html)
   
6. **JSON-file with game data**

   located at `src/main/resources/gamedata.json`

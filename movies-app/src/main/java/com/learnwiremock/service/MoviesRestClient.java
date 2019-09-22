package com.learnwiremock.service;

import com.learnwiremock.constants.MoviesAppConstants;
import com.learnwiremock.dto.Movie;
import com.learnwiremock.exception.MovieErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static com.learnwiremock.constants.MoviesAppConstants.*;

@Slf4j
public class MoviesRestClient {

    private WebClient webClient;

    public MoviesRestClient(WebClient _webClient) {
        this.webClient = _webClient;
    }

    /**
     * Retrieve all the movies from the service.
     *
     * @return
     */
    public List<Movie> retrieveAllMovies() {
        List<Movie> movieList;
        try {
            log.info("Inside retrieve all movies");
            movieList = webClient.get().uri(GET_ALL_MOVIES_V1)
                    .retrieve() // actual call is made to the api
                    .bodyToFlux(Movie.class) //body is converted to flux(Represents multiple items)
                    .collectList() // collecting the httpResponse as a list\
                    .block(); // This call makes the Webclient to behave as a synchronous client.
            log.info("after the call");
        } catch (WebClientResponseException ex) {
            log.error("WebClientResponseException - Error Message is : {} ", ex, ex.getResponseBodyAsString());
            throw new MovieErrorResponse(ex.getStatusText(), ex);
        } catch (Exception ex) {
            log.error("Exception - The Error Message is {} and exception is ", ex.getMessage() + ex);
            throw new MovieErrorResponse(ex.getMessage(), ex);
        }
        return movieList;
    }

    public Movie retrieveMovieById(Integer movieId) {
        Movie movie;
        try {
            movie = webClient.get().uri(MOVIE_BY_ID_PATH_PARAM_V1, movieId) //mapping the movie id to the url
                    .retrieve()
                    .bodyToMono(Movie.class) //body is converted to Mono(Represents single item)
                    .block();
        } catch (WebClientResponseException ex) {
            log.error("WebClientResponseException - Exception is {} and the Error Message is : {}", ex, ex.getResponseBodyAsString());
            throw new MovieErrorResponse(ex.getStatusText(), ex);
        } catch (Exception ex) {
            log.error("Exception - The Error Message is {} ", ex.getMessage());
            throw new MovieErrorResponse(ex);
        }
        return movie;
    }


    public List<Movie> retrieveMovieByName(String movieName) {

        List<Movie> movieList = null;
        String retrieveByNameUri = UriComponentsBuilder.fromUriString( MOVIE_BY_NAME_QUERY_PARAM_V1)
                .queryParam("movie_name", movieName)
                .buildAndExpand()
                .toUriString();

        try {
            movieList = webClient.get().uri(retrieveByNameUri)
                    .retrieve()
                    .bodyToFlux(Movie.class)
                    .collectList()
                    .block();
        } catch (WebClientResponseException ex) {
            log.error("WebClientResponseException - Exception is {} and the Error Message is : {}", ex, ex.getResponseBodyAsString());
            throw new MovieErrorResponse(ex.getStatusText(), ex);
        } catch (Exception ex) {
            log.error("Exception - The Error Message is {} ", ex.getMessage());
            throw new MovieErrorResponse(ex);
        }
        return movieList;
    }


    /**
     * This method makes a REST call to the Movies RESTFUL Service and retrieves a list of Movies as a response based on the year.
     *
     * @param year - Integer (Example : 2012,2013 etc.,)
     * @return - List<Movie>
     */
    public List<Movie> retreieveMovieByYear(Integer year) {
        String retrieveByYearUri = UriComponentsBuilder.fromUriString( MOVIE_BY_YEAR_QUERY_PARAM_V1)
                .queryParam("year", year)
                .buildAndExpand()
                .toUriString();
        List<Movie> movieList;

        try {
            movieList = webClient.get().uri(retrieveByYearUri)
                    .retrieve()
                    .bodyToFlux(Movie.class)
                    .collectList()
                    .block();
        } catch (WebClientResponseException ex) {
            log.error("WebClientResponseException in retreieveMovieByYear {} and the Error Message is : {} ", ex, ex.getResponseBodyAsString());
            throw new MovieErrorResponse(ex.getStatusText(), ex);
        } catch (Exception ex) {
            log.error("Exception - The Error Message is {} ", ex.getMessage());
            throw new MovieErrorResponse(ex);
        }
        return movieList;
    }

    /**
     * This method makes the REST call to add a new Movie to the Movies RESTFUL Service.
     *
     * @param newMovie
     * @return
     */
    public Movie addMovie(Movie newMovie) {
        Movie movie;
        try {
           // String token  = retrieveToken();
            movie = webClient.post().uri( ADD_MOVIE_V1)
                    //.header("token", token)
                    .syncBody(newMovie)
                    .retrieve()
                    .bodyToMono(Movie.class)
                    .block();
            log.info("New Movie SuccessFully addded {} ", movie);
        } catch (WebClientResponseException ex) {
            log.error("WebClientResponseException - Error Message is : {} , and the Error Response Body is {}", ex, ex.getResponseBodyAsString());
            throw new MovieErrorResponse(ex.getStatusText(), ex);
        } catch (Exception ex) {
            log.error("Exception - The Error Message is {} ", ex.getMessage());
            throw new MovieErrorResponse(ex);
        }
        return movie;
    }

    private String retrieveToken() {

        try {
            return webClient.get().uri(RETRIEVE_TOKEN)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        }catch (Exception ex){
            log.error("Exception - The Error Message is {} ", ex.getMessage()+ex);
            throw ex;
        }
    }

    public Movie updateMovie(Integer movieId, Movie movie) {
        Movie updatedMovie;

        try {
            updatedMovie = webClient.put().uri( MOVIE_BY_ID_PATH_PARAM_V1, movieId)
                    .syncBody(movie)
                    .retrieve()
                    .bodyToMono(Movie.class)
                    .block();
            log.info(" Movie SuccessFully updated {} ", updatedMovie);
        } catch (WebClientResponseException ex) {
            log.error("WebClientResponseException - Error Message is : {}", ex, ex.getResponseBodyAsString());
            throw new MovieErrorResponse(ex.getStatusText(), ex);
        } catch (Exception ex) {
            log.error("Exception - The Error Message is {} ", ex.getMessage());
            throw new MovieErrorResponse(ex);
        }

        return updatedMovie;
    }

    public String deleteMovie(Integer movieId) {

       String response;
        try {
            response = webClient.delete().uri( MOVIE_BY_ID_PATH_PARAM_V1, movieId)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        }catch (WebClientResponseException ex) {
            log.error("WebClientResponseException - Error Message is : {}", ex, ex.getResponseBodyAsString());
            throw new MovieErrorResponse(ex.getStatusText(), ex);
        } catch (Exception ex) {
            log.error("Exception - The Error Message is {} ", ex.getMessage());
            throw new MovieErrorResponse(ex);
        }

       return response;

    }

    public String deleteMovieByName(String movieName) {

        String deleteMovieByNameURI = UriComponentsBuilder.fromUriString(MoviesAppConstants.MOVIE_BY_NAME_QUERY_PARAM_V1)
                .queryParam("movie_name", movieName)
                .buildAndExpand()
                .toUriString();

        try {
            webClient.delete().uri(deleteMovieByNameURI)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        }catch (WebClientResponseException ex) {
            log.error("WebClientResponseException - The Status code is {} and the Error Message is : {}", ex.getRawStatusCode(), ex.getResponseBodyAsString());
            throw new MovieErrorResponse(ex.getStatusText(), ex);
        } catch (Exception ex) {
            log.error("Exception - The Error Message is {} ", ex.getMessage());
            throw new MovieErrorResponse(ex);
        }
        return "Movie Deleted Successfully";
    }
}

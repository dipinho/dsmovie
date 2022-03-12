package com.devsuperior.dsmovie.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.Movie;
import com.devsuperior.dsmovie.entities.Score;
import com.devsuperior.dsmovie.entities.User;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.repositories.UserRepository;

@Service	//registra como componente do sistema
public class ScoreService {
	
	@Autowired		// Ele permite que o Spring resolva e injete beans de colaboração em nosso bean
	private MovieRepository movieRepository;
	
	@Autowired		
	private UserRepository userRepository;
	
	@Autowired		
	private ScoreRepository scoreRepository;
	
	@Transactional
	public MovieDTO saveScore(ScoreDTO dto) {	// informar o email, id e valor da avaliacao
		User user = userRepository.findByEmail(dto.getEmail());		// Recuperar o usuario do bd pelo email, caso ele nao exista será inserido
		if(user == null) {
			user = new User();
			user.setEmail(dto.getEmail());
			user = userRepository.saveAndFlush(user); // Save and Flush garante que o objeto será atualizado
		}
		
		
		Movie movie = movieRepository.findById(dto.getMovieId()).get();	// .get vai pegar o objeto dentro do optional
		
		Score score = new Score();
		score.setMovie(movie);
		score.setUser(user);
		score.setValue(dto.getScore());
		
		score = scoreRepository.saveAndFlush(score);
		
		double sum = 0;
		for(Score s : movie.getScores()){	// aqui vai retornar a lista de scores assosciadas com esse filme
			sum = sum + s.getValue();
		}
		
		double avg = sum /movie.getScores().size();
		
		movie.setScore(avg);
		movie.setCount(movie.getScores().size());
		
		movie = movieRepository.save(movie);
		
		return new MovieDTO(movie);
	}

}


package ch.karthi.reactive;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class ReactiveWebExampleApplication implements CommandLineRunner {

	@Autowired
	PersonRepository personRepository;

	public static void main(String[] args) {
		SpringApplication.run(ReactiveWebExampleApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		try {
			TimeUnit.SECONDS.sleep(3);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ExecutorService executorService = Executors.newCachedThreadPool();
		executorService.submit(() -> {
			for (var i = 0; i < 10; i++) {
				Person person = new Person();
				person.setAge(new Random().nextInt());
				personRepository.save(person).subscribe();
				try {
					TimeUnit.SECONDS.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});

	}

}

@RestController
class PersonController{

	@Autowired
	private PersonRepository personRepository;
	
	@GetMapping(value="/persons", produces= {MediaType.TEXT_EVENT_STREAM_VALUE})
	public Flux<Person> getPersons(){
		return personRepository.findWithTailableCursorBy();
	}
}


interface PersonRepository extends ReactiveMongoRepository<Person, String> {
	
	@Tailable
	Flux<Person> findWithTailableCursorBy();
}

@Document
@Data
@NoArgsConstructor
class Person {
	@Id
	private String id;
	private int age;
}
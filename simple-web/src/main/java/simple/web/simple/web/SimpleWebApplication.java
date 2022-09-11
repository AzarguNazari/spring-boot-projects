package simple.web.simple.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@SpringBootApplication
public class SimpleWebApplication implements ApplicationRunner {

	@Autowired
	private PersonRepository personRepository;

	public static void main(String[] args) {
		SpringApplication.run(SimpleWebApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		IntStream.rangeClosed(1, 10)
				.mapToObj(n -> new Person(UUID.randomUUID().toString(), "Name " + n, "Lname " + n))
				.forEach(personRepository::save);
	}
}

@RestController
@RequestMapping("/api/persons")
record PersonController(PersonService personService){

	@GetMapping("/")
	public List<Person> getPersons(){
		return personService.getAllPerson();
	}

	@GetMapping("/{name}")
	public List<Person> getPersonByName(@PathVariable String name){
		return personService.getPersonByName(name);
	}

}

@Service
record PersonService(PersonRepository personRepository){

	public List<Person> getAllPerson(){
		return personRepository.findAll();
	}

	public List<Person> getPersonByName(String name){
		return personRepository.findByName(name);
	}

}

@Repository
interface PersonRepository extends CrudRepository<Person, String> {
	List<Person> findAll();
	List<Person> findByName(String name);
}

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
class Person {
	@Id private String id;
	private String name;
	private String email;
}

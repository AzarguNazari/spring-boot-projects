package simple.web.simple.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
				.mapToObj(n -> Person.builder().name("Name " + n)
						.address("Address " + n)
						.age((int)(Math.random() * 10 * n))
						.email("email" + n + "@gmail.com")
						.gender(Math.random() > 0.5 ? "male" : "female")
						.description("Some description here " + n)
						.build())
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

	@GetMapping("/person/{name}")
	public List<Person> getPersonByName(@PathVariable String name){
		return personService.getPersonByName(name);
	}

	@GetMapping("/18+")
	public List<Person> get18PlusAge(){
		return personService.get18PlusAge();
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

	public List<Person> get18PlusAge(){
		return personRepository.findByAgeGreaterThan(17);
	}
}

@Repository
interface PersonRepository extends CrudRepository<Person, Long> {
	List<Person> findAll();
	List<Person> findByName(String name);
	List<Person> findByAgeGreaterThan(int age);
	List<Person> findByAgeLessThan(int age);

}

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
class Person {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String name;
	private int age;
	private String address;
	private String email;
	private String gender;
	private String description;
}

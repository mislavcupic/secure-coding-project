package hr.algebra.semregprojectbackend;


import hr.algebra.semregprojectbackend.domain.HackerStudent;
import hr.algebra.semregprojectbackend.domain.whitelisting.WhiteListed;
import hr.algebra.semregprojectbackend.exception.AccessNotPossibleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;


@SpringBootApplication
public class SemRegProjectBackendApplication {
	private static final String HACKER_SER = "data/hacker.ser";
	private static final Logger logger = LoggerFactory.getLogger(SemRegProjectBackendApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SemRegProjectBackendApplication.class, args);
		HackerStudent hacker = new HackerStudent();
		serializeObject(hacker, HACKER_SER);
		deserializeObject();
	}



	public static void serializeObject(Object obj, String HACKER_SER) {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(HACKER_SER))) {
			oos.writeObject(obj);
			logger.info("Object serialized to {}", HACKER_SER);
		} catch (Exception e) {
			throw new AccessNotPossibleException("Serialization failed: " + e.getMessage());
		}
	}

	public static Object deserializeObject() {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(HACKER_SER)) {
			@Override
			protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
				String className = desc.getName();
				if (!WhiteListed.WHITE_LISTED.contains(className)) {
					throw new ClassNotFoundException("Class " + className + " is not whitelisted for deserialization");
				}
				return super.resolveClass(desc);
			}
		}) {
			Object obj = ois.readObject();
			logger.info("Object deserialized from {}: {}", HACKER_SER, obj);
			return obj;
		} catch (Exception e) {
			throw new AccessNotPossibleException("Deserialization failed: " + e.getMessage());
		}
	}
}


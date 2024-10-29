package ee.kaido.kmdb.service.checkers;

import ee.kaido.kmdb.controller.exception.BadRequestException;
import ee.kaido.kmdb.controller.exception.ElementExistsException;
import ee.kaido.kmdb.repository.ActorRepository;

public class Checks {
    public static String checkIfStringNotEmpty(String text, String fieldName) throws BadRequestException {
        if (text.trim().isEmpty())
            throw new BadRequestException(fieldName + " can't be empty!");
        return text;
    }

    public static void checkIfActorIdExists(Long id, ActorRepository actorRepository) throws ElementExistsException {
        if (id != null && actorRepository.findById(id).isPresent())
            throw new ElementExistsException("Actor with id " + id + " already exists");
    }
}

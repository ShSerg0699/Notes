package nsu.shserg.Notes.controller;

import nsu.shserg.Notes.entity.Note;
import nsu.shserg.Notes.repository.NoteRepository;
import nsu.shserg.Notes.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@CrossOrigin
public class NoteController {
    private final NoteRepository noteRepository;

    @Autowired
    public NoteController(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @RequestMapping(method = POST, value ="/notes")
    public ResponseEntity<Note> add(@RequestBody Note note){
        noteRepository.save(note);
        Optional<Note> optional = noteRepository.findByContent(note.getContent());
        if (optional.isEmpty()) {
            throw new NotFoundException();
        }
        return new ResponseEntity<Note>(optional.get(), HttpStatus.OK);
    }

    @RequestMapping(method = GET, value = "/notes")
    public ResponseEntity<List<Note>> getAll() {
        return new ResponseEntity<List<Note>>(noteRepository.findAll(), HttpStatus.OK);
    }

    @RequestMapping(method = GET, value = "/notes/{id}")
    public ResponseEntity<Note> get(@PathVariable("id") long id){
        Optional<Note> optional = noteRepository.findById(id);
        if (optional.isEmpty()) {
            throw new NotFoundException();
        }
        return new ResponseEntity<Note>(optional.get(), HttpStatus.OK);
    }

    @RequestMapping(method = PUT, value = "/notes/{id}")
    public ResponseEntity<Note> update(@PathVariable("id") long id,
                                       @RequestBody Note newNote){
        Optional<Note> optional = noteRepository.findById(id);
        if (optional.isEmpty()) {
            throw new NotFoundException();
        }
        Note note = optional.get();
        if(newNote.getTitle() != null) {
            note.setTitle(newNote.getTitle());
        }
        note.setContent(newNote.getContent());
        noteRepository.save(note);
        return new ResponseEntity<Note>(note, HttpStatus.OK);
    }

    @RequestMapping(method = GET, value = "/notes/search")
    public ResponseEntity<List<Note>> search(@RequestParam String query){
        List<Note> notes = noteRepository.findAll();
        return new ResponseEntity<List<Note>>( notes.stream().
                filter(p -> checkQuery(query, p))
                .collect(Collectors.toList()),
                HttpStatus.OK);
    }

    private boolean checkQuery(String query, Note note) {
        if(note.getTitle() != null) {
            return query == null ||
                    note.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                    note.getContent().toLowerCase().contains(query.toLowerCase());
        } else {
            return query == null ||
                    note.getContent().toLowerCase().contains(query.toLowerCase());
        }
    }


}

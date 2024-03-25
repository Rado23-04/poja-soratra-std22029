package hei.school.soratra.endpoint.rest.controller.health;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PutPoeticPhrases {
    @PutMapping("/soratra/{id}")
    public ResponseEntity<Void> processPoeticPhrase(@PathVariable("id") String id, @RequestBody String poeticPhrase) {

        if (id == null || poeticPhrase == null || poeticPhrase.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }


        return ResponseEntity.ok().build();
    }

}

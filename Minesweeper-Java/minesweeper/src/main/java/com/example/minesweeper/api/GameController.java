package com.example.minesweeper.api;

import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import com.example.minesweeper.model.*;
import com.example.minesweeper.service.GameService;

@RestController
@RequestMapping("/api/v1/games")
@CrossOrigin(origins = { "http://127.0.0.1:5500", "http://localhost:5500" })
public class GameController {

    private final GameService service;

    public GameController(GameService service) {
        this.service = service;
    }

    @PostMapping
    public GameState create(@Valid @RequestBody NewGameRequest req) {
        return service.create(req);
    }

    @GetMapping("/{id}")
    public GameState state(@PathVariable String id) {
        return service.get(id);
    }

    @PostMapping("/{id}/reveal")
    public GameState reveal(@PathVariable String id, @Valid @RequestBody RevealRequest req) {
        return service.reveal(id, req);
    }

    @PostMapping("/{id}/flag")
    public GameState flag(@PathVariable String id, @Valid @RequestBody FlagRequest req) {
        return service.flag(id, req);
    }

    @PostMapping("/{id}/chord")
    public GameState chord(@PathVariable String id, @Valid @RequestBody ChordRequest req) {
        return service.chord(id, req);
    }

    @PostMapping("/{id}/reset")
    public GameState reset(@PathVariable String id, @Valid @RequestBody ResetRequest req) {
        return service.reset(id, req);
    }
}

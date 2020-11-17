package neural.controller;

import db.entity.Game;
import db.entity.GameCell;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@RestController
@RequestMapping(value = "/api/g", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "Game")
public class GameController extends AbstractRestController {

    public GameController() {
    }

    // GET a game. sample (no token sign-on needed)
    @ApiOperation(value = "getGame")
    @RequestMapping(value = "/a{g}", method = RequestMethod.GET)
    public ResponseEntity<Game> getGame(
            @RequestHeader("Authorization") String token,
            @RequestParam("g") final int gameSize) {


        // build the game here:
        int greenCells = gameSize * 65/100;
        int yellowCells = gameSize * 25/100;
        int redCells = gameSize - greenCells - yellowCells - 2; // -2 since need 1 for key and 1 for escape

        //Random random = new Random();
        //int[] randomIntArray = random.ints(new Integer(1), gameSize + 1).toArray();
        //IntStream intStream = random.ints(gameSize , 1, gameSize + 1);
        //int[] randomIntArray = intStream.toArray();
        //intStream.limit() // returns a reduced stream of first n elements.


        Game game = new Game();
        List<GameCell> listGameCells = game.getGameCells();
        int index1 = 0;
        int index2 = 0;

        while ( index1 < gameSize ) {
            GameCell gameCell = new GameCell();
            gameCell.setLocationNumber(new Long(index1+1));
            listGameCells.add(gameCell);
            index1++;
        };

        Collections.shuffle(listGameCells);

        for (GameCell x : listGameCells  ) {
            if (index2 < greenCells) { x.setPoints(new Long(1)); }
            else if (index2 < greenCells + yellowCells) { x.setPoints(new Long(2)); }
            else if (index2 < greenCells + yellowCells + redCells) { x.setPoints(new Long(3)); }
            else { x.setPoints(new Long(10)); };
            index2++;
        };

        Collections.sort(listGameCells); // See implements Comparable and method override in the entity GameCell

        game.setGameCells(listGameCells);

        //if (game == null) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); }
        return ResponseEntity.ok(game);
    }


}

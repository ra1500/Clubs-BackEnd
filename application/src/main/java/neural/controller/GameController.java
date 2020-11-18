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
import java.util.ArrayList;
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
        int gameExitkey = gameSize - 1;
        int  gameExit = gameSize;
        double gameSizeDouble = new Double(gameSize);
        int yAxisHeight = (int)Math.sqrt(gameSizeDouble/2);
        int xAxisLength = yAxisHeight * 2;

        Game game = new Game();
        List<GameCell> listGameCells = game.getGameCells();
        int index1 = 0;
        int index2 = 0;

        // create all the game cells
        while ( index1 < gameSize ) {
            GameCell gameCell = new GameCell();
            gameCell.setLocationNumber(new Long(index1+1));
            listGameCells.add(gameCell);
            index1++;
        };

        // randomize the order of the cells
        Collections.shuffle(listGameCells);

        // assign a point value to each cell
        for (GameCell x : listGameCells  ) {
            if (index2 < greenCells) { x.setPoints(new Long(1)); }
            else if (index2 < greenCells + yellowCells) { x.setPoints(new Long(2)); }
            else if (index2 < greenCells + yellowCells + redCells) { x.setPoints(new Long(3)); }
            else if (index2 == gameExitkey-1) { x.setPoints(new Long(-10)); x.setType(new Long(2)); }
            else if (index2 == gameExit-1) { x.setPoints(new Long(0)); x.setType(new Long(3)); };
            index2++;
        };

        // return the list to its original order, thereby having randomized points assigned to each cell
        Collections.sort(listGameCells); // See implements Comparable and method override in the entity GameCell

        // assign to each hexagon cell the wall directional indicators
        for ( GameCell x : listGameCells ) {

            // down
            if (x.getLocationNumber() < (xAxisLength*(yAxisHeight-1))+1 ) { x.setDownValue(listGameCells.get(x.getLocationNumber().intValue() + xAxisLength -1).getPoints()); };  // down indicators
            if (x.getLocationNumber() < (xAxisLength*(yAxisHeight-1))+1 ) { x.setDownLeftValue(listGameCells.get(x.getLocationNumber().intValue() + xAxisLength - 2).getPoints()); };  // downLeft indicators
            if (x.getLocationNumber() < (xAxisLength*(yAxisHeight-1))+1  && x.getLocationNumber().intValue() + xAxisLength < listGameCells.size() ) { x.setDownRightValue(listGameCells.get(x.getLocationNumber().intValue() + xAxisLength).getPoints()); };  // downRight indicators

            // up
            if (x.getLocationNumber() > xAxisLength ) { x.setUpValue(listGameCells.get(x.getLocationNumber().intValue() - xAxisLength -1).getPoints()); };  // up indicators
            if (x.getLocationNumber() > xAxisLength && x.getLocationNumber().intValue() - xAxisLength -2 >= 0) { x.setUpLeftValue(listGameCells.get(x.getLocationNumber().intValue() - xAxisLength - 2).getPoints()); };  // upLeft indicators
            if (x.getLocationNumber() > xAxisLength ) { x.setUpRightValue(listGameCells.get(x.getLocationNumber().intValue() - xAxisLength).getPoints()); };  // upRight indicators

            // left
            if ( x.getLocationNumber()-2 >= 0 ) { x.setLeftValue(listGameCells.get(x.getLocationNumber().intValue() -2).getPoints()); };

            // right
            if ( x.getLocationNumber() < gameSize ) { x.setRightValue(listGameCells.get(x.getLocationNumber().intValue()).getPoints()); };

            // clean up the borders
            if (x.getLocationNumber() < xAxisLength+1) { x.setUpValue(new Long(0)); x.setUpLeftValue(new Long(0)); x.setUpRightValue(new Long(0)); }; // top row, up indicators
            if (x.getLocationNumber() > xAxisLength*(yAxisHeight-1)) { x.setDownValue(new Long(0)); x.setDownLeftValue(new Long(0)); x.setDownRightValue(new Long(0)); }; // bottom row, down indicators
            if (new Double(x.getLocationNumber()-1) % new Double(xAxisLength) == 0) { x.setLeftValue(new Long(0)); x.setUpLeftValue(new Long(0)); x.setDownLeftValue(new Long(0)); }; // left column, left indicators
            if ( new Double(x.getLocationNumber()) % new Double(xAxisLength) == 0) { x.setRightValue(new Long(0)); x.setUpRightValue(new Long(0)); x.setDownRightValue(new Long(0)); }; // right column, right indicators

            // clean up the two special cases (key and escape)

        };

        game.setGameCells(listGameCells);
        return ResponseEntity.ok(game);
    }


}

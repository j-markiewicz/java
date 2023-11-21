import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

enum LabyrinthTile {
	Wall,
	Fire,
	Water,
	Path,
	Unknown,
	Exit
}

class TestLabyrinth implements PlayerController {
	List<LabyrinthTile> map = List.of(
		LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall,
		LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Water, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Wall,
		LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Fire, LabyrinthTile.Path, LabyrinthTile.Wall, LabyrinthTile.Wall,
		LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Wall, LabyrinthTile.Wall,
		LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Wall, LabyrinthTile.Wall,
		LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Wall, LabyrinthTile.Wall,
		LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Wall, LabyrinthTile.Wall,
		LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Wall, LabyrinthTile.Fire, LabyrinthTile.Path, LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Wall, LabyrinthTile.Wall,
		LabyrinthTile.Wall, LabyrinthTile.Water, LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Wall, LabyrinthTile.Fire, LabyrinthTile.Path, LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Wall, LabyrinthTile.Wall,
		LabyrinthTile.Wall, LabyrinthTile.Water, LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Wall, LabyrinthTile.Wall,
		LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Wall, LabyrinthTile.Wall,
		LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Wall, LabyrinthTile.Wall,
		LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Fire, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall,
		LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall,
		LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Path, LabyrinthTile.Wall,
		LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Exit, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall
	);
	int height = 16;
	int width = 18;
	Position current;
	boolean foundExit = false;
	List<Position> visited = new ArrayList<>();
	int underwaterMoves = 0;
	Direction nextMoveMustBe = null;
	int maxUnderwaterMoves = 10;

	TestLabyrinth(int col, int row) {
		current = new Position(col, row);
	}

	private int posToIdx(Position pos) {
		return pos.row() * width + pos.col();
	}

	@Override
	public void move(Direction direction) throws OnFire, Flooded, Wall, Exit {
		assert !foundExit;
		assert !map.get(posToIdx(current)).equals(LabyrinthTile.Wall);

		if (nextMoveMustBe != null) {
			assert direction.equals(nextMoveMustBe);
			nextMoveMustBe = null;
		}

		visited.add(current);
		var newPosition = direction.step(current);

		switch (map.get(posToIdx(newPosition))) {
			case Wall -> {
				assert !visited.contains(newPosition);
				throw new Wall();
			}
			case Fire -> {
				underwaterMoves = 0;
				assert !visited.contains(newPosition);
				nextMoveMustBe = opposite(direction);
				current = newPosition;
				throw new OnFire();
			}
			case Water -> {
				underwaterMoves++;
				current = newPosition;
				assert underwaterMoves <= maxUnderwaterMoves;
				throw new Flooded();
			}
			case Path -> {
				current = newPosition;
				underwaterMoves = 0;
			}
			case Unknown -> {
				throw new IllegalStateException("Unknown tile in map");
			}
			case Exit -> {
				current = newPosition;
				underwaterMoves = 0;
				foundExit = true;
				throw new Exit();
			}
		}
	}

	private Direction opposite(Direction dir) {
		switch (dir) {
			case NORTH -> {
				return Direction.SOUTH;
			}
			case SOUTH -> {
				return Direction.NORTH;
			}
			case EAST -> {
				return Direction.WEST;
			}
			case WEST -> {
				return Direction.EAST;
			}
		}

		throw new Error("no opposite direction found");
	}
}

class IndianaTest {
	@org.junit.jupiter.api.Test
	void findExit76() {
		var explorer = new Indiana();
		var labyrinth = new TestLabyrinth(7, 6);
		explorer.underwaterMovesAllowed(labyrinth.maxUnderwaterMoves);
		explorer.setPlayerController(labyrinth);
		explorer.findExit();
		assert labyrinth.foundExit;
	}

	@org.junit.jupiter.api.Test
	void findExit314() {
		var explorer = new Indiana();
		var labyrinth = new TestLabyrinth(3, 14);
		explorer.underwaterMovesAllowed(labyrinth.maxUnderwaterMoves);
		explorer.setPlayerController(labyrinth);
		explorer.findExit();
		assert labyrinth.foundExit;
	}

	@org.junit.jupiter.api.Test
	void findExit610() {
		var explorer = new Indiana();
		var labyrinth = new TestLabyrinth(6, 10);
		explorer.underwaterMovesAllowed(labyrinth.maxUnderwaterMoves);
		explorer.setPlayerController(labyrinth);
		explorer.findExit();
		assert labyrinth.foundExit;
	}

	@org.junit.jupiter.api.Test
	void findExit137() {
		var explorer = new Indiana();
		var labyrinth = new TestLabyrinth(13, 7);
		explorer.underwaterMovesAllowed(labyrinth.maxUnderwaterMoves);
		explorer.setPlayerController(labyrinth);
		explorer.findExit();
		assert labyrinth.foundExit;
	}

	@org.junit.jupiter.api.Test
	void waterDeadEnd() {
		var explorer = new Indiana();
		var labyrinth = new TestLabyrinth(1, 7);
		labyrinth.map = List.of(
				LabyrinthTile.Wall, LabyrinthTile.Exit, LabyrinthTile.Wall,
				LabyrinthTile.Wall, LabyrinthTile.Water, LabyrinthTile.Wall,
				LabyrinthTile.Wall, LabyrinthTile.Water, LabyrinthTile.Wall,
				LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Wall,
				LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Wall,
				LabyrinthTile.Wall, LabyrinthTile.Water, LabyrinthTile.Wall,
				LabyrinthTile.Wall, LabyrinthTile.Water, LabyrinthTile.Wall,
				LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Wall,
				LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Wall,
				LabyrinthTile.Wall, LabyrinthTile.Water, LabyrinthTile.Wall,
				LabyrinthTile.Wall, LabyrinthTile.Water, LabyrinthTile.Wall,
				LabyrinthTile.Wall, LabyrinthTile.Wall, LabyrinthTile.Wall
		);
		labyrinth.width = 3;
		labyrinth.height = 12;
		labyrinth.maxUnderwaterMoves = 5;
		explorer.underwaterMovesAllowed(labyrinth.maxUnderwaterMoves);
		explorer.setPlayerController(labyrinth);
		explorer.findExit();
		assert labyrinth.foundExit;
		assert labyrinth.current.equals(new Position(1, 0));
	}

	@org.junit.jupiter.api.Test
	void findExitWater() {
		var explorer = new Indiana();
		var labyrinth = new TestLabyrinth(1, 7);
		labyrinth.map = List.of(
			LabyrinthTile.Wall, LabyrinthTile.Exit, LabyrinthTile.Wall,
			LabyrinthTile.Wall, LabyrinthTile.Water, LabyrinthTile.Wall,
			LabyrinthTile.Wall, LabyrinthTile.Water, LabyrinthTile.Wall,
			LabyrinthTile.Wall, LabyrinthTile.Water, LabyrinthTile.Wall,
			LabyrinthTile.Wall, LabyrinthTile.Water, LabyrinthTile.Wall,
			LabyrinthTile.Wall, LabyrinthTile.Water, LabyrinthTile.Wall,
			LabyrinthTile.Wall, LabyrinthTile.Water, LabyrinthTile.Wall,
			LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Wall,
			LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Wall,
			LabyrinthTile.Wall, LabyrinthTile.Water, LabyrinthTile.Wall,
			LabyrinthTile.Wall, LabyrinthTile.Water, LabyrinthTile.Wall,
			LabyrinthTile.Wall, LabyrinthTile.Exit, LabyrinthTile.Wall
		);
		labyrinth.width = 3;
		labyrinth.height = 12;
		labyrinth.maxUnderwaterMoves = 5;
		explorer.underwaterMovesAllowed(labyrinth.maxUnderwaterMoves);
		explorer.setPlayerController(labyrinth);
		explorer.findExit();
		assert labyrinth.foundExit;
		assert labyrinth.current.equals(new Position(1, 11));
	}

	@org.junit.jupiter.api.Test
	void findExitWaterInverted() {
		var explorer = new Indiana();
		var labyrinth = new TestLabyrinth(1, 4);
		labyrinth.map = List.of(
				LabyrinthTile.Wall, LabyrinthTile.Exit, LabyrinthTile.Wall,
				LabyrinthTile.Wall, LabyrinthTile.Water, LabyrinthTile.Wall,
				LabyrinthTile.Wall, LabyrinthTile.Water, LabyrinthTile.Wall,
				LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Wall,
				LabyrinthTile.Wall, LabyrinthTile.Path, LabyrinthTile.Wall,
				LabyrinthTile.Wall, LabyrinthTile.Water, LabyrinthTile.Wall,
				LabyrinthTile.Wall, LabyrinthTile.Water, LabyrinthTile.Wall,
				LabyrinthTile.Wall, LabyrinthTile.Water, LabyrinthTile.Wall,
				LabyrinthTile.Wall, LabyrinthTile.Water, LabyrinthTile.Wall,
				LabyrinthTile.Wall, LabyrinthTile.Water, LabyrinthTile.Wall,
				LabyrinthTile.Wall, LabyrinthTile.Water, LabyrinthTile.Wall,
				LabyrinthTile.Wall, LabyrinthTile.Exit, LabyrinthTile.Wall
		);
		labyrinth.width = 3;
		labyrinth.height = 12;
		labyrinth.maxUnderwaterMoves = 5;
		explorer.underwaterMovesAllowed(labyrinth.maxUnderwaterMoves);
		explorer.setPlayerController(labyrinth);
		explorer.findExit();
		assert labyrinth.foundExit;
		assert labyrinth.current.equals(new Position(1, 0));
	}
}
